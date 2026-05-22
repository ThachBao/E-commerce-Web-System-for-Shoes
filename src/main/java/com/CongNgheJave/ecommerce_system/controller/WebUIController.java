package com.CongNgheJave.ecommerce_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUIController {

    @GetMapping("/cart-ui")
    public String viewCart() {
        return "cart/view";
    }

    @GetMapping("/checkout-ui")
    public String viewCheckout() {
        return "order/checkout";
    }

    @GetMapping("/success-ui")
    public String viewSuccess() {
        return "order/success";
    }

    @GetMapping("/payment-ui/{orderCode}")
    public String viewPayment(@org.springframework.web.bind.annotation.PathVariable String orderCode, org.springframework.ui.Model model) {
        model.addAttribute("orderCode", orderCode);
        return "order/payment";
    }
}
