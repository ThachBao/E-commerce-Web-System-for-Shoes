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
}
