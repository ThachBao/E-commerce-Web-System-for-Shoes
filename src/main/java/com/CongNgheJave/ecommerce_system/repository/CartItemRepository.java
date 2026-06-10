package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.variant.id = :variantId")
    Optional<CartItem> findByCartIdAndVariantId(@Param("cartId") Integer cartId, @Param("variantId") Integer variantId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.variant.id = :variantId")
    void deleteByVariantId(@Param("variantId") Integer variantId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.variant.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
