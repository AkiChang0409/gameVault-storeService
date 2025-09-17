package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.*;
import com.gamevault.storeservice.model.dto.*;
import com.gamevault.storeservice.model.ENUM.OrderStatus;
import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.repository.GameRepository;
import com.gamevault.storeservice.repository.OrderRepository;
import com.gamevault.storeservice.repository.PurchasedGameActivationCodeRepository;
import com.gamevault.storeservice.repository.UnusedGameActivationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {


    final private OrderRepository orderRepository;
    final private UnusedGameActivationCodeRepository unusedRepo;
    final private PurchasedGameActivationCodeRepository purchasedRepo;
    final private GameActivationCodeService codeService;
    final private GameRepository gameRepository;

    /** Step1: 生成 Pending 订单（不分配激活码） */
    @Transactional
    public OrderDTO createPendingOrder(Cart cart, PaymentMethod method) {
        if (cart.isEmpty()) throw new IllegalStateException("Cart is empty");
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setPaymentMethod(method);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal finalAmount = BigDecimal.ZERO;
        for (CartItem ci : cart.getCartItems()) {
            for (int i = 0; i < ci.getQuantity(); i++) {
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setUserId(cart.getUserId());
                oi.setOrderDate(order.getOrderDate());
                oi.setOrderStatus(OrderStatus.PENDING);
                oi.setGameId(ci.getGameId());
                oi.setUnitPrice(ci.getPrice());
                order.getOrderItems().add(oi);
                finalAmount = finalAmount.add(ci.getPrice());
            }
        }
        order.setFinalAmount(finalAmount);

        // 保存订单并返回
        Order saved = orderRepository.saveAndFlush(order);
        return convertToDTO(saved);
    }

    /** Step2: 支付成功 → 置为 PAID 并分配激活码 */
    @Transactional
    public OrderDTO captureAndFulfill(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getUserId().equals(userId)) throw new IllegalStateException("Forbidden");
        if (order.getStatus() == OrderStatus.PAID) return convertToDTO(order); // 幂等

        order.setStatus(OrderStatus.PAID);

        for (OrderItem oi : order.getOrderItems()) {
            // 低水位补码
            long remaining = unusedRepo.countByGameId(oi.getGameId());
            if (remaining < 10) codeService.replenishCodes(oi.getGameId(), 30);

            UnusedGameActivationCode unused = unusedRepo.findFirstByGameId(oi.getGameId())
                    .orElseThrow(() -> new IllegalStateException("No activation codes"));

            PurchasedGameActivationCode purchased = new PurchasedGameActivationCode();
            purchased.setUserId(order.getUserId());
            purchased.setOrderItemId(oi.getOrderItemId());
            purchased.setGameId(oi.getGameId());
            purchased.setActivationCode(unused.getActivationCode());
            purchasedRepo.save(purchased);

            unusedRepo.delete(unused);
            oi.setOrderStatus(OrderStatus.PAID);
        }

        return convertToDTO(order);
    }

    /** 可选：支付失败标记 */
    @Transactional
    public void markFailed(Long orderId, Long userId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getUserId().equals(userId)) throw new IllegalStateException("Forbidden");
        if (o.getStatus() == OrderStatus.PAID) return;
        o.setStatus(OrderStatus.FAILED);
    }
    public Optional<OrderDTO> findById(Long orderId) {
        return orderRepository.findById(orderId).map(this::convertToDTO);
    }

    public List<OrderDTO> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderIdDesc(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setOrderDate(order.getOrderDate());

        // 收集 gameId
        List<Long> gameIds = order.getOrderItems().stream()
                .map(OrderItem::getGameId)
                .toList();

        // 批量查询 Game 表，避免 N+1
        Map<Long, Game> gameMap = gameRepository.findAllById(gameIds)
                .stream()
                .collect(Collectors.toMap(Game::getGameId, g -> g));

        // 转 DTO
        dto.setOrderItems(order.getOrderItems().stream().map(oi -> {
            OrderItemDTO oid = new OrderItemDTO();
            oid.setOrderItemId(oi.getOrderItemId());
            oid.setOrderId(order.getOrderId());
            oid.setUserId(oi.getUserId());
            oid.setOrderDate(oi.getOrderDate());
            oid.setOrderStatus(oi.getOrderStatus().name());
            oid.setGameId(oi.getGameId());
            oid.setUnitPrice(oi.getUnitPrice());

            // 回填激活码
            purchasedRepo.findByOrderItemId(oi.getOrderItemId())
                    .ifPresent(code -> oid.setActivationCode(code.getActivationCode()));

            // 回填游戏信息
            Game game = gameMap.get(oi.getGameId());
            if (game != null) {
                oid.setGameTitle(game.getTitle());
                oid.setImageUrl(game.getImageUrl());
            }

            return oid;
        }).collect(Collectors.toList()));

        return dto;
    }


    // convertToDTO 保持你现有实现（如需把激活码回填进 OrderItemDTO，再用 purchasedRepo.findByOrderItemId(...) 取）
}
