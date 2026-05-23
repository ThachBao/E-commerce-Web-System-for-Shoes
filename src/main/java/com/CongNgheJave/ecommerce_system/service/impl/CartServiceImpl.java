package com.CongNgheJave.ecommerce_system.service.impl;

<<<<<<< HEAD
public interface CartServiceImpl {
=======
import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CartItemResponse;
import com.CongNgheJave.ecommerce_system.dto.response.CartResponse;
import com.CongNgheJave.ecommerce_system.entity.Cart;
import com.CongNgheJave.ecommerce_system.entity.CartItem;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.entity.User;
import com.CongNgheJave.ecommerce_system.repository.CartItemRepository;
import com.CongNgheJave.ecommerce_system.repository.CartRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.repository.UserRepository;
import com.CongNgheJave.ecommerce_system.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addToCart(Integer userId, AddToCartRequest request) {
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        // Check Stock (Tránh Overselling)
        if (variant.getStockQuantity() != null && variant.getStockQuantity() > 0 && variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        // Tìm hoặc tạo mới Cart
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // Xử lý cộng dồn hoặc tạo mới
        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElse(null);

        if (cartItem != null) {
            // Incremental Logic
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (newQuantity <= 0) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
                return;
            }
            if (variant.getStockQuantity() != null && variant.getStockQuantity() > 0 && variant.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock available for incremental add");
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new
            if (request.getQuantity() <= 0) {
                throw new RuntimeException("Cannot add non-positive quantity");
            }
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductVariant(variant);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        List<CartItemResponse> itemResponses = cart.getCartItems().stream().map(item -> {
            CartItemResponse response = new CartItemResponse();
            response.setId(item.getId());
            response.setVariantId(item.getProductVariant().getId());
            response.setSku(item.getProductVariant().getSku());
            response.setQuantity(item.getQuantity());
            
            BigDecimal price = item.getProductVariant().getSalePrice() != null ? 
                               item.getProductVariant().getSalePrice() : 
                               item.getProductVariant().getPrice();
            response.setUnitPrice(price);
            
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            response.setItemTotal(itemTotal);
            
            return response;
        }).collect(Collectors.toList());

        BigDecimal subTotal = itemResponses.stream()
                .map(CartItemResponse::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setItems(itemResponses);
        response.setSubTotal(subTotal);

        return response;
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Integer userId, Integer cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa sản phẩm này");
        }

        if (newQuantity <= 0) {
            cartItem.getCart().getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
            return;
        }

        if (cartItem.getProductVariant().getStockQuantity() != null && cartItem.getProductVariant().getStockQuantity() > 0 && cartItem.getProductVariant().getStockQuantity() < newQuantity) {
            throw new RuntimeException("Không đủ số lượng trong kho");
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeCartItem(Integer userId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa sản phẩm này");
        }

        cartItem.getCart().getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng của user"));
        
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
>>>>>>> origin/member3_cart_payment
}
