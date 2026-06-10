package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.exception.InsufficientStockException;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/checkout")
@Slf4j
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Hiển thị form checkout.
     * 
     * URL: GET /checkout
     */
    @GetMapping
    public String showCheckoutForm(Model model) {
        return "checkout-form";
    }

    /**
     * Checkout - tạo đơn hàng từ giỏ hàng.
     * 
     * URL: POST /checkout
     */
    @PostMapping
    public String placeOrder(@Valid @ModelAttribute CheckoutRequest request,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        /*
         * Tạm thời dùng customer id = 5.
         * Sau khi phần Security hoàn thành, thay bằng user đang đăng nhập.
         */
        Integer temporaryCustomerId = 5;

        try {
            Order order = orderService.placeOrder(temporaryCustomerId, request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đặt hàng thành công! Mã đơn hàng: " + order.getOrderCode()
            );

            return "redirect:/orders/" + order.getId();

        } catch (InsufficientStockException ex) {
            model.addAttribute("errorMessage", "❌ " + ex.getMessage());
            return "checkout-form";

        } catch (InvalidOperationException ex) {
            model.addAttribute("errorMessage", "❌ " + ex.getMessage());
            return "checkout-form";
            
        } catch (Exception ex) {
            // Log lỗi để debug
            log.error("Lỗi khi thanh toán: ", ex);
            model.addAttribute("errorMessage", "❌ Lỗi: " + ex.getMessage());
            return "checkout-form";
        }
    }
}
