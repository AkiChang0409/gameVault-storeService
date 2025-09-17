package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.dto.OrderDTO;
import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.service.CartService;
import com.gamevault.storeservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    /** 1) 结账：生成 Pending 订单 */
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@AuthenticationPrincipal Jwt jwt,
                                             @RequestParam PaymentMethod method) {
        Long userId = Long.valueOf(jwt.getSubject()); // 或 jwt.getClaim("uid")

        var cart = cartService.getCartEntity(userId);
        var dto  = orderService.createPendingOrder(cart, method);

        // ✅ 结账后清空/锁定购物车，避免重复下单
        cartService.markCheckedOut(userId, method);

        return ResponseEntity.ok(dto);
    }

    /** 2) 查询单个订单详情 */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId,
                                             @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return orderService.findById(orderId)
                .filter(o -> o.getUserId().equals(userId)) // ✅ 确保只能查自己的订单
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 3) 查询当前用户的所有订单 */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }

    /** 4) 模拟支付成功：置为 PAID 并分配激活码 */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderDTO> pay(@PathVariable Long orderId,
                                        @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(orderService.captureAndFulfill(orderId, userId));
    }

    /** 5) 模拟支付失败：置为 FAILED */
    @PostMapping("/{orderId}/fail")
    public ResponseEntity<Void> fail(@PathVariable Long orderId,
                                     @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        orderService.markFailed(orderId, userId);
        return ResponseEntity.ok().build();
    }
}
