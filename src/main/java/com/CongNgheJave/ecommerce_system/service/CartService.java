package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.entity.CartItem;

import java.util.List;

public interface CartService {
    void addToCart(Integer userId, AddToCartRequest request);

    List<CartItem> getCartItems(Integer userId);
}
