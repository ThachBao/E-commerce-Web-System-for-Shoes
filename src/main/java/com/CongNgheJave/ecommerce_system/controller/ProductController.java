package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.response.ProductResponse;
import com.CongNgheJave.ecommerce_system.service.BrandService;
import com.CongNgheJave.ecommerce_system.service.CategoryService;
import com.CongNgheJave.ecommerce_system.service.ProductService;
import com.CongNgheJave.ecommerce_system.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dành cho khách hàng xem sản phẩm.
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductVariantService variantService;

    // Trang danh sách sản phẩm (Shop)
    @GetMapping
    public String shop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String gender,
            Model model) {
        
        Page<ProductResponse> productPage = productService.searchProducts(
                keyword, categoryId, brandId, gender, true, PageRequest.of(page, size));
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        // Dữ liệu cho bộ lọc sidebar
        model.addAttribute("categories", categoryService.getActiveRootCategories());
        model.addAttribute("brands", brandService.getAllActiveBrands());
        
        return "product/list";
    }

    // Trang chi tiết sản phẩm
    @GetMapping("/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        ProductResponse product = productService.getProductBySlug(slug);
        
        model.addAttribute("product", product);
        // Lấy các biến thể đang còn hàng của sản phẩm này
        model.addAttribute("variants", variantService.getActiveVariantsByProductId(product.getId()));
        
        return "product/detail";
    }
}
