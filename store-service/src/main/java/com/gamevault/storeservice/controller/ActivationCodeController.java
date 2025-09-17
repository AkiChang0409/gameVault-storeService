package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.model.PurchasedGameActivationCode;
import com.gamevault.storeservice.repository.PurchasedGameActivationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activation-codes")
@RequiredArgsConstructor
public class ActivationCodeController {

    private final PurchasedGameActivationCodeRepository purchasedRepo;

    /**
     * 查询当前用户已购买的所有激活码
     */
    @GetMapping("/my")
    public List<PurchasedGameActivationCode> getMyCodes(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject()); // 从JWT拿sub
        return purchasedRepo.findByUserId(userId);
    }
}
