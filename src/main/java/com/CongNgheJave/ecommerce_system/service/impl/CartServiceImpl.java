package com.CongNgheJave.ecommerce_system.service.impl;


import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.entity.Cart;
import com.CongNgheJave.ecommerce_system.entity.CartItem;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.CartItemRepository;
import com.CongNgheJave.ecommerce_system.repository.CartRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public void addToCart(Integer userId, AddToCartRequest request)
    {
        Cart cart = getOrCreateActiveCart(userId);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId()).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy biến thể của sản phẩm"));

        validateVariantCanBePurchased(variant);
        validateStock(variant, request.getQuantity());

        CartItem cartItem = cartItemRepository
                .findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElseGet(() -> createNewCartItem(cart.getId(), variant));

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());

        cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getCartItems(Integer userId)
    {
        Cart cart=getOrCreateActiveCart(userId);
        return cartItemRepository.findByCartId(cart.getId());
    }
    private Cart getOrCreateActiveCart(Integer userId)
    {
        return cartRepository.findByUserId(userId).orElseGet(()->createCartForUser(userId));
    }
    private Cart createCartForUser(Integer userId)
    {
        Cart cart =new Cart();
        cart.setUserId((userId));
        cart.setStatus("ACTIVE");
        return cartRepository.save(cart);
    }

    private void validateVariantCanBePurchased(ProductVariant variant)
    {
        if (variant.getActive() == null || !variant.getActive()) {
            throw new InvalidOperationException("Sản phẩm hiện không còn được bán");
        }
    }

    private CartItem createNewCartItem(Integer cartId, ProductVariant variant)
    {
        CartItem cartItem= new CartItem();
        cartItem.setCartId(cartId);
        cartItem.setVariantId(variant.getId());
        cartItem.setQuantity(0);
        cartItem.setUnitPrice(getFinalPrice(variant));
        return cartItem;
    }
    private BigDecimal getFinalPrice(ProductVariant variant)
    {
        if(variant.getSalePrice()!=null)
        {
            return variant.getSalePrice();
        }
        return  variant.getPrice();
    }
    private void validateStock(ProductVariant variant, Integer quantity) {
        if (variant.getStockQuantity() == null || variant.getStockQuantity() < quantity) {
            throw new InvalidOperationException("Số lượng tồn kho không đủ");
        }
    }
}
