package com.gamevault.storeservice.repository;

import com.gamevault.storeservice.model.PurchasedGameActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchasedGameActivationCodeRepository extends JpaRepository<PurchasedGameActivationCode, Long> {
    // 查用户所有激活码
    List<PurchasedGameActivationCode> findByUserId(Long userId);

    // 查某个订单项的激活码（1:1关系）
    Optional<PurchasedGameActivationCode> findByOrderItemId(Long orderItemId);

    // 通过激活码本身查（验证用）
    Optional<PurchasedGameActivationCode> findByActivationCode(String activationCode);
}
