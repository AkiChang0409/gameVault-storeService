package com.gamevault.storeservice.repository;

import com.gamevault.storeservice.model.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<OrderItem> findRecentItemsByUser(Long userId, int limit) {
        return em.createQuery(
                        "SELECT oi FROM OrderItem oi WHERE oi.userId = :uid ORDER BY oi.orderDate DESC",
                        OrderItem.class
                ).setParameter("uid", userId)
                .setMaxResults(limit)
                .getResultList();
    }
}
