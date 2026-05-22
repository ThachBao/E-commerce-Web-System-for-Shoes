package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.OrderStatusHistory;
import com.CongNgheJave.ecommerce_system.entity.Payment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Luồng 1 + 2: Admin xem danh sách đơn hàng và lọc theo trạng thái.
    Page<Order> getAdminOrders(String status, int page, int size);

    // Luồng 3: Admin xem chi tiết đơn hàng.
    Order getOrderDetail(Integer orderId);

    // Luồng 8: Customer xem lịch sử đơn hàng.
    List<Order> getOrdersByCustomer(Integer userId);

    // Luồng 9: Customer xem chi tiết đơn hàng của mình.
    Order getCustomerOrderDetail(Integer orderId, Integer customerId);

    // Luồng 4 + 5 + 6 + 7: Admin cập nhật trạng thái và ghi lịch sử.
    void updateOrderStatus(Integer orderId, String newStatus, Integer changedByUserId, String note);

    // Luồng 7: Xem lịch sử trạng thái.
    List<OrderStatusHistory> getStatusHistory(Integer orderId);

    // Luồng 10: Hiển thị thông tin thanh toán.
    Optional<Payment> getPaymentByOrderId(Integer orderId);

    // NEW: Checkout - tạo đơn hàng từ giỏ hàng.
    Order placeOrder(Integer userId, CheckoutRequest request);

    // NEW: Customer cancel order.
    void cancelOrder(Integer orderId, Integer customerId);

    // NEW: Admin cancel order.
    void adminCancelOrder(Integer orderId, Integer adminId, String note);

    // NEW: Admin complete order.
    void completeOrder(Integer orderId, Integer adminId, String note);
}