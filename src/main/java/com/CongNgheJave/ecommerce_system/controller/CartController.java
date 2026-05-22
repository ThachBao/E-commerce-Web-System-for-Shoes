package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.dto.request.UpdateCartItemRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ApiResponse;
import com.CongNgheJave.ecommerce_system.dto.response.CartResponse;
import com.CongNgheJave.ecommerce_system.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                // Assuming username is the ID, or parsing from Principal.
                // For now, if your team uses JWT, the ID might be in auth.getName()
                return Integer.parseInt(auth.getName());
            } catch (NumberFormatException e) {
                // Ignore and fallback
            }
        }
        return 5; // Hardcode mapping to user 5 (Customer 01) in DB for testing
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            Integer userId = getCurrentUserId();
            cartService.addToCart(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đã thêm sản phẩm vào giỏ hàng", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        try {
            Integer userId = getCurrentUserId();
            CartResponse cart = cartService.getCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin giỏ hàng thành công", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCartItem(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            Integer userId = getCurrentUserId();
            cartService.updateCartItemQuantity(userId, id, request.getQuantity());
            return ResponseEntity.ok(ApiResponse.success("Cập nhật số lượng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Integer id) {
        try {
            Integer userId = getCurrentUserId();
            cartService.removeCartItem(userId, id);
            return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        try {
            Integer userId = getCurrentUserId();
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Đã làm trống giỏ hàng", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
