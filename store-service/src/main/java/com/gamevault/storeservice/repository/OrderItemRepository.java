package com.gamevault.storeservice.repository;

import com.gamevault.storeservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    List<OrderItem> findByUserId(Long userId);
    List<OrderItem> findByOrder_OrderId(Long orderId);
}

interface OrderItemRepositoryCustom {
    List<OrderItem> findRecentItemsByUser(Long userId, int limit);
}
