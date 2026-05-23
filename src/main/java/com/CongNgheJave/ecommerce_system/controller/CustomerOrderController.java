package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.Payment;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class CustomerOrderController {

    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
     * Customer xem lịch sử đơn hàng.
     *
     * URL:
     * GET /orders
     */
    @GetMapping
    public String myOrders(Model model) {

        /*
         * Tạm thời dùng customer id = 5.
         * Theo database mẫu, customer01 thường là id = 5.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryCustomerId = 5;

        model.addAttribute("orders", orderService.getOrdersByCustomer(temporaryCustomerId));

        return "customer/orders/history";
    }

    /*
     * Customer xem chi tiết đơn hàng của mình.
     * Hiển thị thông tin thanh toán của đơn hàng.
     *
     * URL:
     * GET /orders/{id}
     */
    @GetMapping("/{id}")
    public String myOrderDetail(@PathVariable Integer id, Model model) {

        /*
         * Tạm thời dùng customer id = 5.
         * Nếu customer id 5 cố xem đơn của user khác, service sẽ chặn.
         */
        Integer temporaryCustomerId = 5;

        Order order = orderService.getCustomerOrderDetail(id, temporaryCustomerId);
        Payment payment = orderService.getPaymentByOrderId(id).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);

        return "customer/orders/detail";
    }

    /**
     * Customer cancel order.
     * Chỉ được cancel nếu trạng thái là PENDING hoặc CONFIRMED.
     *
     * URL:
     * POST /orders/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id,
                              RedirectAttributes redirectAttributes) {

        /*
         * Tạm thời dùng customer id = 5.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryCustomerId = 5;

        try {
            orderService.cancelOrder(id, temporaryCustomerId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Hủy đơn hàng thành công"
            );
        } catch (InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/orders/" + id;
    }
}