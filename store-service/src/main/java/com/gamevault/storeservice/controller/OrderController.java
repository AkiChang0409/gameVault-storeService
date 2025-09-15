package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.dto.OrderDTO;
import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.service.CartService;
import com.gamevault.storeservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    /**
     * 结账并生成订单
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderDTO> checkout(@PathVariable Long userId,
                                             @RequestParam PaymentMethod paymentMethod) {
        // 从 CartService 获取购物车，再传给 OrderService
        var cart = cartService.getCartEntity(userId); // 需要在 CartService 中增加 getCartEntity 方法
        OrderDTO order = orderService.createOrder(cart, paymentMethod);
        return ResponseEntity.ok(order);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        return orderService.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 查询用户的所有订单
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }
}
