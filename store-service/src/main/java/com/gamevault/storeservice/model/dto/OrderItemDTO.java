package com.gamevault.storeservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItemDTO {
    private Long orderItemId;
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private Long gameId;
    private BigDecimal unitPrice;
}
