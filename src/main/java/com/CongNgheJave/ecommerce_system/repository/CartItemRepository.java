package com.CongNgheJave.ecommerce_system.repository;

<<<<<<< HEAD
public class CartItemRepository {
=======
import com.CongNgheJave.ecommerce_system.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
<<<<<<< HEAD
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productVariant.id = :variantId")
    Optional<CartItem> findByCartIdAndVariantId(@Param("cartId") Integer cartId, @Param("variantId") Integer variantId);
>>>>>>> origin/member3_cart_payment
=======
>>>>>>> origin/member2-product-catalog
}
