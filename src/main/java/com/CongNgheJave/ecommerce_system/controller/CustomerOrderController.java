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

    private Integer getCurrentUserId() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                return Integer.parseInt(auth.getName());
            } catch (NumberFormatException e) {
                // Ignore and fallback
            }
        }
        throw new RuntimeException("Vui lòng đăng nhập để xem đơn hàng");
    }

    @GetMapping
    public String myOrders(Model model) {
        Integer userId = getCurrentUserId();
        model.addAttribute("orders", orderService.getOrdersByCustomer(userId));
        return "customer/orders/history";
    }

    @GetMapping("/{id}")
    public String myOrderDetail(@PathVariable Integer id, Model model) {
        Integer userId = getCurrentUserId();
        Order order = orderService.getCustomerOrderDetail(id, userId);
        Payment payment = orderService.getPaymentByOrderId(id).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        return "customer/orders/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id,
                              @RequestParam(required = false) String note,
                              RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();

        try {
            orderService.cancelOrder(id, userId, note);

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