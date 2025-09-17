package com.gamevault.storeservice.client;

import com.gamevault.storeservice.model.dto.UserProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// TODO
@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthClient {
    @GetMapping("/api/auth/me")
    UserProfileDTO getProfile(@RequestHeader("Authorization") String token);
}

