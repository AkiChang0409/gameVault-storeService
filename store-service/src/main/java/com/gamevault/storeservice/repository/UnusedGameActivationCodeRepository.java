package com.gamevault.storeservice.repository;

import com.gamevault.storeservice.model.UnusedGameActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 未用码：取一条即可
public interface UnusedGameActivationCodeRepository extends JpaRepository<UnusedGameActivationCode, Long> {
    Optional<UnusedGameActivationCode> findFirstByGameId(Long gameId);
    long countByGameId(Long gameId);
}