package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.dto.request.ProductCreateRequest;
import com.CongNgheJave.ecommerce_system.dto.request.ProductUpdateRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductResponse;
import com.CongNgheJave.ecommerce_system.service.BrandService;
import com.CongNgheJave.ecommerce_system.service.CategoryService;
import com.CongNgheJave.ecommerce_system.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller quản trị sản phẩm.
 */
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    // Danh sách sản phẩm kèm bộ lọc
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String gender,
            Model model) {
        
        Page<ProductResponse> productPage = productService.searchProducts(
                keyword, categoryId, brandId, gender, PageRequest.of(page, size));
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("brands", brandService.getAllActiveBrands());
        
        // Giữ lại các filter để hiển thị trên UI
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("gender", gender);
        
        return "admin/product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productRequest", new ProductCreateRequest());
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("brands", brandService.getAllActiveBrands());
        return "admin/product/add";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("productRequest") ProductCreateRequest request,
                             BindingResult result,
                             @RequestParam("thumbnailFile") MultipartFile thumbnail,
                             @RequestParam("imageFiles") List<MultipartFile> images,
                             RedirectAttributes ra,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("brands", brandService.getAllActiveBrands());
            return "admin/product/add";
        }
        
        try {
            productService.createProduct(request, thumbnail, images);
            ra.addFlashAttribute("success", "Thêm sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        ProductResponse response = productService.getProductById(id);
        
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setCategoryId(response.getCategoryId());
        request.setBrandId(response.getBrandId());
        request.setName(response.getName());
        request.setDescription(response.getDescription());
        request.setGender(response.getGender());
        request.setIsFeatured(response.getIsFeatured());
        request.setIsActive(response.getIsActive());
        
        model.addAttribute("productRequest", request);
        model.addAttribute("productId", id);
        model.addAttribute("productCode", response.getCode());
        model.addAttribute("thumbnailUrl", response.getThumbnailUrl());
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        model.addAttribute("brands", brandService.getAllActiveBrands());
        
        return "admin/product/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Integer id,
                                @Valid @ModelAttribute("productRequest") ProductUpdateRequest request,
                                BindingResult result,
                                @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnail,
                                @RequestParam(value = "imageFiles", required = false) List<MultipartFile> images,
                                RedirectAttributes ra,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.getAllActiveCategories());
            model.addAttribute("brands", brandService.getAllActiveBrands());
            return "admin/product/edit";
        }
        
        try {
            productService.updateProduct(id, request, thumbnail, images);
            ra.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
