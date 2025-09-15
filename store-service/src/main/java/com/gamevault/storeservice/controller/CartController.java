package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.model.dto.CartDTO;
import com.gamevault.storeservice.model.dto.OrderDTO;
import com.gamevault.storeservice.service.CartService;
import com.gamevault.storeservice.service.discount.IDiscountStrategy;
import com.gamevault.storeservice.service.discount.DiscountFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDTO> addToCart(@PathVariable Long userId,
                                             @RequestParam(name = "gameId") Long gameId,
                                             @RequestParam(name = "quantity", defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addGame(userId, gameId, quantity));
    }

    @DeleteMapping("/{userId}/items/{gameId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Long userId,
                                                  @PathVariable Long gameId) {
        return ResponseEntity.ok(cartService.removeGame(userId, gameId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @PostMapping("/{userId}/discount")
    public ResponseEntity<CartDTO> applyDiscount(@PathVariable Long userId,
                                                 @RequestBody DiscountRequest request) {
        IDiscountStrategy strategy = DiscountFactory.createDiscount(request.getStrategyType());
        cartService.setDiscountStrategy(strategy);
        cartService.applyDiscounts(userId);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

     @PostMapping("/{userId}/checkout")
     public ResponseEntity<OrderDTO> checkout(@PathVariable Long userId,
                                              @RequestParam PaymentMethod method) {
         return ResponseEntity.ok(cartService.checkout(userId, method));
     }

    @Data
    private static class DiscountRequest {
        private String strategyType;
    }
}
