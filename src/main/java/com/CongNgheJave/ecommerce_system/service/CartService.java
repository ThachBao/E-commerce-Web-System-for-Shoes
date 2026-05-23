package com.CongNgheJave.ecommerce_system.service;

<<<<<<< HEAD
<<<<<<< HEAD
public class CartService {
=======
import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CartResponse;

public interface CartService {
    void addToCart(Integer userId, AddToCartRequest request);

    CartResponse getCart(Integer userId);

    void updateCartItemQuantity(Integer userId, Integer cartItemId, int newQuantity);

    void removeCartItem(Integer userId, Integer cartItemId);

    void clearCart(Integer userId);
>>>>>>> origin/member3_cart_payment
=======
public class CartService {
>>>>>>> origin/member2-product-catalog
}
