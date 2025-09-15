package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.Cart;
import com.gamevault.storeservice.model.CartItem;
import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.CartDTO;
import com.gamevault.storeservice.repository.CartRepository;
import com.gamevault.storeservice.repository.GameRepository;
import com.gamevault.storeservice.repository.OrderRepository;
import com.gamevault.storeservice.service.discount.PercentageDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartRepository cartRepository;
    private GameRepository gameRepository;
    private OrderRepository orderRepository;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = Mockito.mock(CartRepository.class);
        gameRepository = Mockito.mock(GameRepository.class);
        cartService = new CartService(cartRepository, gameRepository, orderRepository);
    }

    @Test
    void testAddGameToNewCart() {
        Long userId = 1L;
        Long gameId = 101L;

        Game game = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        game.setGameId(gameId);
        game.setIsActive(true);

        Cart newCart = new Cart(userId);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        CartDTO dto = cartService.addGame(userId, gameId, 1);

        assertNotNull(dto);
        assertEquals(userId, dto.getUserId());
        assertEquals(1, dto.getCartItems().size());
        assertEquals("Halo", dto.getCartItems().get(0).getGame().getTitle());

        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testRemoveGameFromCart() {
        Long userId = 2L;
        Long gameId = 202L;

        Cart cart = new Cart(userId);
        CartItem item = new CartItem(gameId, BigDecimal.TEN, 1);
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDTO dto = cartService.removeGame(userId, gameId);

        assertNotNull(dto);
        assertTrue(dto.getCartItems().isEmpty());
    }

    @Test
    void testClearCart() {
        Long userId = 3L;

        Cart cart = new Cart(userId);
        cart.getCartItems().add(new CartItem(301L, BigDecimal.valueOf(20), 2));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDTO dto = cartService.clearCart(userId);

        assertNotNull(dto);
        assertEquals(0, dto.getCartItems().size());
        assertEquals(BigDecimal.ZERO, dto.getDiscountAmount());
    }

    @Test
    void testApplyDiscounts() {
        Long userId = 4L;
        Long gameId = 401L;

        Game game = new Game("Elden Ring", "FromSoftware", BigDecimal.valueOf(100), "elden.png");
        game.setGameId(gameId);
        game.setIsActive(true);

        Cart cart = new Cart(userId);
        CartItem item = new CartItem(gameId, BigDecimal.valueOf(100), 1);
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // 设置 50% 折扣
        cartService.setDiscountStrategy(new PercentageDiscount(50));
        boolean applied = cartService.applyDiscounts(userId);

        assertTrue(applied);
        assertEquals(BigDecimal.valueOf(50).setScale(2), cart.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(50).setScale(2), cart.getFinalAmount());
    }
}
