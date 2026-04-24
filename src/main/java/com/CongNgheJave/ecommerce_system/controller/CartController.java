package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.request.AddToCartRequest;
import com.CongNgheJave.ecommerce_system.entity.CartItem;
import com.CongNgheJave.ecommerce_system.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    private static final Integer TEMP_USER_ID=5;

    @GetMapping
    public String viewCart(Model model)
    {
        List<CartItem> cartItems = cartService.getCartItems(TEMP_USER_ID);
        model.addAttribute("cartItems",cartItems);
        return "cart/view";
    }
    @PostMapping("/add")
    public String addToCart(@Valid @ModelAttribute AddToCartRequest request)
    {
        cartService.addToCart(TEMP_USER_ID,request);
        return "redirect:/cart";
    }

}
