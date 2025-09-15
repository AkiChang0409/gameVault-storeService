package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.Game;
import com.gamevault.storeservice.model.dto.GameDTO;
import com.gamevault.storeservice.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameRepository repo;
    private GameService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(GameRepository.class);
        service = new GameService(repo);
    }

    @Test
    void testFindAll() {
        Game g1 = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        Game g2 = new Game("Elden Ring", "FromSoftware", BigDecimal.valueOf(69.99), "eldenring.png");
        when(repo.findAll()).thenReturn(Arrays.asList(g1, g2));

        List<GameDTO> games = service.findAll();

        assertEquals(2, games.size());
        assertEquals("Halo", games.get(0).getTitle());
        assertEquals("Elden Ring", games.get(1).getTitle());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Game g1 = new Game("Halo", "Bungie", BigDecimal.valueOf(59.99), "halo.png");
        when(repo.findById(1L)).thenReturn(Optional.of(g1));

        Optional<GameDTO> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Halo", result.get().getTitle());
        assertEquals(BigDecimal.valueOf(59.99), result.get().getPrice());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void testSearchByTitle() {
        Game g1 = new Game("Halo Infinite", "343", BigDecimal.valueOf(59.99), "halo.png");
        when(repo.findByTitleContainingIgnoreCase("halo")).thenReturn(List.of(g1));

        List<GameDTO> results = service.searchByTitle("halo");

        assertEquals(1, results.size());
        assertEquals("Halo Infinite", results.get(0).getTitle());
        verify(repo, times(1)).findByTitleContainingIgnoreCase("halo");
    }
}
