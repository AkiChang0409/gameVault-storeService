package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.*;
import com.gamevault.storeservice.model.dto.*;
import com.gamevault.storeservice.model.ENUM.OrderStatus;
import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderDTO createOrder(Cart cart, PaymentMethod method) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot create order");
        }

        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setPaymentMethod(method);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal finalAmount = BigDecimal.ZERO;

        for (CartItem ci : cart.getCartItems()) {
            // 展开 CartItem 数量
            for (int i = 0; i < ci.getQuantity(); i++) {
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setUserId(cart.getUserId());
                oi.setOrderDate(order.getOrderDate());
                oi.setOrderStatus(OrderStatus.PENDING);
                oi.setGameId(ci.getGameId());
                oi.setUnitPrice(ci.getPrice());

                finalAmount = finalAmount.add(ci.getPrice());
                order.getOrderItems().add(oi);
            }
        }

        order.setFinalAmount(finalAmount);

        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    public Optional<OrderDTO> findById(Long orderId) {
        return orderRepository.findById(orderId).map(this::convertToDTO);
    }

    public List<OrderDTO> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
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

        dto.setOrderItems(order.getOrderItems().stream().map(oi -> {
            OrderItemDTO oid = new OrderItemDTO();
            oid.setOrderItemId(oi.getOrderItemId());
            oid.setOrderId(order.getOrderId());
            oid.setUserId(oi.getUserId());
            oid.setOrderDate(oi.getOrderDate());
            oid.setOrderStatus(oi.getOrderStatus().name());
            oid.setGameId(oi.getGameId());
            oid.setUnitPrice(oi.getUnitPrice());
            return oid;
        }).collect(Collectors.toList()));

        return dto;
    }
}
