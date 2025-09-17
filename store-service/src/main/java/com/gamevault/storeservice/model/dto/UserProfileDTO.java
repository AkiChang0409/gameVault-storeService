package com.gamevault.storeservice.model.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long uid;
    private String username;
    private String email;
    private String avatar;
}
