package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.service.CategoryService;
import com.CongNgheJave.ecommerce_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller trang chủ.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model) {
        // Lấy các sản phẩm nổi bật
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        // Lấy danh mục để hiển thị menu
        model.addAttribute("menuCategories", categoryService.getActiveRootCategories());
        return "index";
    }
}
