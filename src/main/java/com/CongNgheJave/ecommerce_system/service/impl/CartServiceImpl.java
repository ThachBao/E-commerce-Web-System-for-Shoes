package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CartItemResponse;
import com.CongNgheJave.ecommerce_system.dto.response.CartResponse;
import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.entity.Cart;
import com.CongNgheJave.ecommerce_system.entity.CartItem;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import com.CongNgheJave.ecommerce_system.repository.CartItemRepository;
import com.CongNgheJave.ecommerce_system.repository.CartRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.service.CartService;
import com.CongNgheJave.ecommerce_system.service.FileStorageService;
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
    private final AppUserRepository appUserRepository;
    private final FileStorageService fileStorageService;

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
        Cart cart = cartRepository.findByUser_Id(userId).orElseGet(() -> {
            AppUser user = appUserRepository.findById(userId)
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
                cart.getItems().remove(cartItem);
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
            cartItem.setVariant(variant);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public CartResponse getCart(Integer userId) {
        Cart cart = cartRepository.findByUser_Id(userId).orElseGet(() -> {
            AppUser user = appUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            CartItemResponse response = new CartItemResponse();
            response.setId(item.getId());
            response.setVariantId(item.getVariant().getId());
            response.setSku(item.getVariant().getSku());
            response.setProductName(item.getVariant().getProduct().getName());
            response.setColorName(item.getVariant().getColor().getName());
            response.setSizeName(item.getVariant().getSize().getName());
            
            // Lấy ảnh thumbnail
            item.getVariant().getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .findFirst()
                .ifPresent(img -> response.setImageUrl(fileStorageService.getFileUrl(img.getImageUrl())));
                
            response.setQuantity(item.getQuantity());

            BigDecimal price = item.getVariant().getSalePrice() != null ?
                               item.getVariant().getSalePrice() :
                               item.getVariant().getPrice();
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
            cartItem.getCart().getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
            return;
        }

        if (cartItem.getVariant().getStockQuantity() != null && cartItem.getVariant().getStockQuantity() > 0 && cartItem.getVariant().getStockQuantity() < newQuantity) {
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

        cartItem.getCart().getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng của user"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
