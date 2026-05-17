package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ApiResponse;
import com.CongNgheJave.ecommerce_system.dto.response.CartResponse;
import com.CongNgheJave.ecommerce_system.service.CartService;
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

    // TODO: Khi ráp với Spring Security, thay thế bằng cách lấy từ SecurityContextHolder
    private Integer getCurrentUserId() {
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
}
