package com.gamevault.storeservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "unused_game_activation_codes")
public class UnusedGameActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activationId;

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = false, unique = true)
    private String activationCode;


}
