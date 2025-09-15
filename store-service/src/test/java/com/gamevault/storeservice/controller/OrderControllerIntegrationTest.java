package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.OrderDTO;
import com.gamevault.storeservice.repository.GameRepository;
import com.gamevault.storeservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OrderRepository orderRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/orders";
    }

    private Long userId = 1L;
    private Long gameId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        gameRepository.deleteAll();

        Game game = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        game.setIsActive(true);
        gameId = gameRepository.save(game).getGameId();
    }

    @Test
    void testCheckoutAndGetOrder() {
        // 先加一个游戏到购物车
        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/cart/" + userId + "/items?gameId=" + gameId + "&quantity=2",
                null,
                Object.class
        );

        // Checkout
        ResponseEntity<OrderDTO> checkoutResponse = restTemplate.postForEntity(
                getBaseUrl() + "/checkout/" + userId + "?paymentMethod=CREDIT_CARD",
                null,
                OrderDTO.class
        );

        assertThat(checkoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderDTO order = checkoutResponse.getBody();
        assertThat(order).isNotNull();
        assertThat(order.getOrderItems()).hasSize(2);
        assertThat(order.getFinalAmount()).isEqualByComparingTo("119.98");

        // GET /api/orders/{id}
        ResponseEntity<OrderDTO> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + order.getOrderId(),
                OrderDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getOrderId()).isEqualTo(order.getOrderId());

        // GET /api/orders/user/{userId}
        ResponseEntity<OrderDTO[]> listResponse = restTemplate.getForEntity(
                getBaseUrl() + "/user/" + userId,
                OrderDTO[].class
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotEmpty();
    }
}
