package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.GameDTO;
import com.gamevault.storeservice.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * 游戏控制器，提供 REST API
 * 路径前缀是 /api/games
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameDTO> list(@RequestParam Optional<String> genre,
                              @RequestParam Optional<String> platform,
                              @RequestParam(name = "q") Optional<String> q) {
        if (q.isPresent()) return service.searchByTitle(q.get());
        if (genre.isPresent()) return service.findByGenre(genre.get());
        if (platform.isPresent()) return service.findByPlatform(platform.get());
        return service.findAll();
    }

    @GetMapping("/{id}")
    public GameDTO get(@PathVariable Long id) {
        return service.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    }

    @PostMapping
    public ResponseEntity<GameDTO> create(@RequestBody Game game) {
        GameDTO saved = service.save(game);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .header("Location", "/api/games/" + saved.getGameId()) // 新资源地址
                .body(saved); // 响应体改成 GameDTO
    }
}
