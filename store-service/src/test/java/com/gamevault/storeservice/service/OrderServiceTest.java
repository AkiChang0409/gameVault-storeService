package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.*;
import com.gamevault.storeservice.model.dto.OrderDTO;
import com.gamevault.storeservice.model.ENUM.OrderStatus;
import com.gamevault.storeservice.model.ENUM.PaymentMethod;
import com.gamevault.storeservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    void testCreateOrderFromCart() {
        Cart cart = new Cart(1L);

        // 构造一个 CartItem，数量 2
        CartItem item = new CartItem(1001L, BigDecimal.valueOf(59.99), 2);
        cart.addGame(item);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(500L);
            order.getOrderItems().forEach(oi -> oi.setOrderItemId((long) (Math.random() * 1000)));
            return order;
        });

        OrderDTO dto = orderService.createOrder(cart, PaymentMethod.CREDIT_CARD);

        assertNotNull(dto.getOrderId());
        assertEquals(1L, dto.getUserId());
        assertEquals(OrderStatus.PENDING.name(), dto.getStatus());
        assertEquals(2, dto.getOrderItems().size()); // quantity=2 → 2 条 OrderItem
        assertEquals(BigDecimal.valueOf(119.98).setScale(2), dto.getFinalAmount());
    }

    @Test
    void testFindById() {
        Order order = new Order();
        order.setOrderId(600L);
        order.setUserId(2L);

        when(orderRepository.findById(600L)).thenReturn(Optional.of(order));

        Optional<OrderDTO> result = orderService.findById(600L);
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getUserId());
    }
}
