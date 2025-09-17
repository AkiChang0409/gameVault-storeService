package com.gamevault.storeservice.controller;

import com.gamevault.storeservice.client.AuthClient;
import com.gamevault.storeservice.model.dto.UserProfileDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final AuthClient authClient;

    public ProfileController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping("/me")
    public UserProfileDTO proxy(@AuthenticationPrincipal Jwt jwt) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUid(Long.valueOf(jwt.getSubject())); // sub
        dto.setUsername(jwt.getClaimAsString("preferred_username"));
        dto.setEmail(jwt.getClaimAsString("email"));
        dto.setAvatar(jwt.getClaimAsString("avatar"));
        return dto;
    }

//    @GetMapping("/me")
//    public UserProfileDTO proxy(@AuthenticationPrincipal Jwt jwt) {
//        String token = "Bearer " + jwt.getTokenValue();
//        return authClient.getProfile(token);
//    }


}
