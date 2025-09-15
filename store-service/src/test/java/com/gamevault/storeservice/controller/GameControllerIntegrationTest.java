package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.GameDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 激活 application-test.properties
class GameControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/games";
    }

    @Test
    void testCreateAndGetGame() {
        // 创建一个新游戏（请求体还是实体 Game）
        Game newGame = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        newGame.setGenre("Action");
        newGame.setPlatform("PC");
        newGame.setReleaseDate(LocalDate.now());

        ResponseEntity<GameDTO> postResponse =
                restTemplate.postForEntity(getBaseUrl(), newGame, GameDTO.class);

        // 确认返回 201
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        GameDTO savedGame = postResponse.getBody();
        assertThat(savedGame).isNotNull();
        assertThat(savedGame.getGameId()).isNotNull();

        // GET /api/games
        ResponseEntity<GameDTO[]> listResponse =
                restTemplate.getForEntity(getBaseUrl(), GameDTO[].class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotEmpty();

        // GET /api/games/{id}
        ResponseEntity<GameDTO> getResponse =
                restTemplate.getForEntity(getBaseUrl() + "/" + savedGame.getGameId(), GameDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Halo");

        // GET /api/games?q=Halo
        ResponseEntity<GameDTO[]> searchResponse =
                restTemplate.getForEntity(getBaseUrl() + "?q=Halo", GameDTO[].class);

        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotEmpty();
    }
}
