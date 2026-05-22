package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @EntityGraph(attributePaths = {"items", "items.variant", "items.variant.product", "items.variant.size", "items.variant.color"})
    Optional<Cart> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"items", "items.variant", "items.variant.product", "items.variant.size", "items.variant.color"})
    Optional<Cart> findByUser_Id(Integer userId);
}
