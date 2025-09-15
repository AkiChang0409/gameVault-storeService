package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.CartDTO;
import com.gamevault.storeservice.repository.CartRepository;
import com.gamevault.storeservice.repository.GameRepository;
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
class CartControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CartRepository cartRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/cart";
    }

    private Long userId = 1L;
    private Long gameId;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        gameRepository.deleteAll();
        // 确保有一个游戏存在
        Game game = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        game.setGenre("Action");
        game.setPlatform("PC");
        game.setIsActive(true);
        gameId = gameRepository.save(game).getGameId();
    }

    @Test
    void testAddAndGetCart() {
        // 添加游戏到购物车
        ResponseEntity<CartDTO> addResponse = restTemplate.postForEntity(
                getBaseUrl() + "/" + userId + "/items?gameId=" + gameId + "&quantity=2",
                null,
                CartDTO.class);

        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CartDTO cart = addResponse.getBody();
        assertThat(cart).isNotNull();
        assertThat(cart.getCartItems()).isNotEmpty();
        assertThat(cart.getCartItems().get(0).getQuantity()).isEqualTo(2);

        // 获取购物车
        ResponseEntity<CartDTO> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                CartDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getCartItems()).hasSize(1);
    }

    @Test
    void testRemoveItem() {
        // 先加
        restTemplate.postForEntity(
                getBaseUrl() + "/" + userId + "/items?gameId=" + gameId,
                null,
                CartDTO.class);

        // 再删
        restTemplate.delete(getBaseUrl() + "/" + userId + "/items/" + gameId);

        ResponseEntity<CartDTO> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                CartDTO.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCartItems()).isEmpty();
    }

    @Test
    void testClearCart() {
        // 加两个
        restTemplate.postForEntity(getBaseUrl() + "/" + userId + "/items?gameId=" + gameId, null, CartDTO.class);
        restTemplate.postForEntity(getBaseUrl() + "/" + userId + "/items?gameId=" + gameId, null, CartDTO.class);

        // 清空
        restTemplate.delete(getBaseUrl() + "/" + userId);

        ResponseEntity<CartDTO> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                CartDTO.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCartItems()).isEmpty();
    }

    @Test
    void testApplyDiscount() {
        // 加一个游戏
        restTemplate.postForEntity(getBaseUrl() + "/" + userId + "/items?gameId=" + gameId, null, CartDTO.class);

        // 应用折扣
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"strategyType\":\"PERCENTAGE_50\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CartDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + userId + "/discount",
                HttpMethod.POST,
                entity,
                CartDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CartDTO cart = response.getBody();
        assertThat(cart).isNotNull();
        assertThat(cart.getDiscountAmount()).isGreaterThan(BigDecimal.ZERO);
    }
}
