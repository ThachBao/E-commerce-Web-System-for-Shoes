package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.dto.response.OrderResponse;
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.OrderStatusHistory;
import com.CongNgheJave.ecommerce_system.entity.Payment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Checkout - tạo đơn hàng từ giỏ hàng (dùng bởi OrderController).
    OrderResponse checkout(Integer userId, CheckoutRequest request);

    // Admin xem danh sách đơn hàng và lọc theo trạng thái.
    Page<Order> getAdminOrders(String status, int page, int size);

    // Admin xem chi tiết đơn hàng.
    Order getOrderDetail(Integer orderId);

    // Customer xem lịch sử đơn hàng.
    List<Order> getOrdersByCustomer(Integer userId);

    // Customer xem chi tiết đơn hàng của mình.
    Order getCustomerOrderDetail(Integer orderId, Integer customerId);

    // Admin cập nhật trạng thái và ghi lịch sử.
    void updateOrderStatus(Integer orderId, String newStatus, Integer changedByUserId, String note);

    // Xem lịch sử trạng thái.
    List<OrderStatusHistory> getStatusHistory(Integer orderId);

    // Hiển thị thông tin thanh toán.
    Optional<Payment> getPaymentByOrderId(Integer orderId);

    // Tạo đơn hàng từ giỏ hàng (internal).
    Order placeOrder(Integer userId, CheckoutRequest request);

    // Customer cancel order.
    void cancelOrder(Integer orderId, Integer customerId);

    // Admin cancel order.
    void adminCancelOrder(Integer orderId, Integer adminId, String note);

    // Admin complete order.
    void completeOrder(Integer orderId, Integer adminId, String note);
}
