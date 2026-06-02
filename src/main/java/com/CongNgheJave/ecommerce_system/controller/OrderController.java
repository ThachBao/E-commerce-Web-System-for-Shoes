package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ApiResponse;
import com.CongNgheJave.ecommerce_system.dto.response.OrderDetailResponse;
import com.CongNgheJave.ecommerce_system.dto.response.OrderResponse;
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.repository.OrderRepository;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    private Integer getCurrentUserId() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                return Integer.parseInt(auth.getName());
            } catch (NumberFormatException e) {
                // Ignore and fallback
            }
        }
        return 5; // Hardcode mapping to user 5 for testing/fallback
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@Valid @RequestBody CheckoutRequest request) {
        try {
            Integer userId = getCurrentUserId();
            OrderResponse response = orderService.checkout(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đặt hàng thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetails(@PathVariable String orderCode) {
        try {
            Order order = orderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            OrderDetailResponse response = new OrderDetailResponse();
            response.setOrderCode(order.getOrderCode());
            response.setOrderStatus(order.getOrderStatus());
            response.setPaymentStatus(order.getPaymentStatus());
            response.setPaymentMethod(order.getPaymentMethod());
            response.setTotalAmount(order.getTotalAmount());
            response.setShippingFullName(order.getShippingFullName());
            response.setShippingPhone(order.getShippingPhone());
            response.setShippingAddress(order.getShippingAddress());
            response.setPlacedAt(order.getPlacedAt());

            return ResponseEntity.ok(ApiResponse.success("Chi tiết đơn hàng", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
