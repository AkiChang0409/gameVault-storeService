package com.gamevault.storeservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderItemDTO {
    private Long orderItemId;
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private Long gameId;
    private BigDecimal unitPrice;

    private String activationCode;
    private String gameTitle;
    private String imageUrl;
}
