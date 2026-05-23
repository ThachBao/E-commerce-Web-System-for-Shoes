package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.OrderStatusUpdateRequest;
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.Payment;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import com.CongNgheJave.ecommerce_system.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public AdminOrderController(OrderService orderService,
                                PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    /*
     * Admin xem danh sách đơn hàng.
     * Admin lọc đơn hàng theo trạng thái.
     *
     * URL:
     * GET /admin/orders
     * GET /admin/orders?status=PENDING
     */
    @GetMapping
    public String listOrders(@RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {

        if (page < 0) {
            page = 0;
        }

        Page<Order> orderPage = orderService.getAdminOrders(status, page, 10);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("selectedStatus", status);

        return "admin/orders/list";
    }

    /*
     * Admin xem chi tiết đơn hàng.
     * Admin xem lịch sử trạng thái.
     * Admin xem thông tin thanh toán.
     *
     * URL:
     * GET /admin/orders/{id}
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderDetail(id);
        Payment payment = orderService.getPaymentByOrderId(id).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("histories", orderService.getStatusHistory(id));

        // Danh sách trạng thái cho form update.
        model.addAttribute("statuses", List.of(
                "PENDING",
                "CONFIRMED",
                "PROCESSING",
                "SHIPPING",
                "COMPLETED"
        ));

        model.addAttribute("statusUpdateRequest", new OrderStatusUpdateRequest());

        return "admin/orders/detail";
    }

    /*
     * Admin xác nhận đơn hàng: PENDING -> CONFIRMED.
     * Admin chuyển đơn sang đang xử lý: CONFIRMED -> PROCESSING.
     * Admin chuyển đơn sang đang giao: PROCESSING -> SHIPPING.
     * Hệ thống ghi lịch sử đổi trạng thái.
     *
     * URL:
     * POST /admin/orders/{id}/status
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Integer id,
                               @ModelAttribute OrderStatusUpdateRequest request,
                               RedirectAttributes redirectAttributes) {

        /*
         * Tạm thời dùng staff id = 3.
         * Theo database mẫu, staff01 thường là id = 3.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryStaffId = 3;

        try {
            orderService.updateOrderStatus(
                    id,
                    request.getNewStatus(),
                    temporaryStaffId,
                    request.getNote()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Cập nhật trạng thái đơn hàng thành công"
            );
        } catch (InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    /**
     * Admin cancel order.
     * Restore stock khi cancel.
     *
     * URL:
     * POST /admin/orders/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id,
                              @RequestParam(required = false) String note,
                              RedirectAttributes redirectAttributes) {

        /*
         * Tạm thời dùng staff id = 3.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryStaffId = 3;

        try {
            orderService.adminCancelOrder(id, temporaryStaffId, note);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Hủy đơn hàng thành công"
            );
        } catch (InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    /**
     * Admin complete order.
     * Chỉ được complete từ trạng thái SHIPPING.
     * Nếu COD thì tự động mark payment as PAID.
     *
     * URL:
     * POST /admin/orders/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public String completeOrder(@PathVariable Integer id,
                                @RequestParam(required = false) String note,
                                RedirectAttributes redirectAttributes) {

        /*
         * Tạm thời dùng staff id = 3.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryStaffId = 3;

        try {
            orderService.completeOrder(id, temporaryStaffId, note);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Hoàn thành đơn hàng thành công"
            );
        } catch (InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    /**
     * Admin mark payment as paid.
     * Dùng cho BANK_TRANSFER khi admin xác nhận đã nhận tiền.
     *
     * URL:
     * POST /admin/orders/{id}/mark-paid
     */
    @PostMapping("/{id}/mark-paid")
    public String markPaymentAsPaid(@PathVariable Integer id,
                                    RedirectAttributes redirectAttributes) {

        try {
            Payment payment = orderService.getPaymentByOrderId(id)
                    .orElseThrow(() -> new InvalidOperationException("Không tìm thấy thông tin thanh toán"));

            paymentService.markAsPaid(payment.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã xác nhận thanh toán"
            );
        } catch (InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}