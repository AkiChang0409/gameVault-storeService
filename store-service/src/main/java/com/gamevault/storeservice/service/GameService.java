package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.GameDTO;
import com.gamevault.storeservice.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository repo;

    public GameService(GameRepository repo) {
        this.repo = repo;
    }

    public List<GameDTO> findAll() {
        return repo.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<GameDTO> findById(Long id) {
        return repo.findById(id).map(this::convertToDTO);
    }

    public List<GameDTO> searchByTitle(String q) {
        return repo.findByTitleContainingIgnoreCase(q).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findByGenre(String genre) {
        return repo.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findByPlatform(String platform) {
        return repo.findByPlatform(platform).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findTopDiscountedGames(int limit) {
        return repo.findTopDiscountedGames(limit).stream()
                .map(this::convertToDTO)
                .toList();
    }


    public GameDTO save(Game game) {
        Game saved = repo.save(game);
        return convertToDTO(saved);
    }

    // --- DTO 转换 ---
    private GameDTO convertToDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setGameId(game.getGameId());
        dto.setTitle(game.getTitle());
        dto.setDeveloper(game.getDeveloper());
        dto.setDescription(game.getDescription());
        dto.setPrice(game.getPrice());
        dto.setDiscountPrice(game.getDiscountPrice());
        dto.setGenre(game.getGenre());
        dto.setPlatform(game.getPlatform());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setIsActive(game.getIsActive());
        dto.setImageUrl(game.getImageUrl());
        return dto;
    }
}
