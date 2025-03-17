package com.uClothes.uClothes.repositories;

import com.uClothes.uClothes.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsBySessionId(String sessionId);
    List<Order> findByUser_Id(UUID userId);
}
