package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.dto.request.BrandRequest;
import com.CongNgheJave.ecommerce_system.dto.response.BrandResponse;
import com.CongNgheJave.ecommerce_system.service.BrandService;
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

/**
 * Controller quản trị thương hiệu.
 */
@Controller
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    // Hiển thị danh sách thương hiệu
    @GetMapping
    public String listBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<BrandResponse> brandPage = brandService.getBrands(keyword, PageRequest.of(page, size));
        
        model.addAttribute("brands", brandPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        
        return "admin/brand/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brandRequest", new BrandRequest());
        return "admin/brand/add";
    }

    @PostMapping("/add")
    public String addBrand(@Valid @ModelAttribute("brandRequest") BrandRequest request,
                           BindingResult result,
                           @RequestParam("logoFile") MultipartFile logoFile,
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/brand/add";
        }
        
        try {
            brandService.createBrand(request, logoFile);
            ra.addFlashAttribute("success", "Thêm thương hiệu thành công!");
            return "redirect:/admin/brands";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/brands/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        BrandResponse response = brandService.getBrandById(id);
        
        BrandRequest request = new BrandRequest();
        request.setCode(response.getCode());
        request.setName(response.getName());
        request.setDescription(response.getDescription());
        request.setIsActive(response.getIsActive());
        
        model.addAttribute("brandRequest", request);
        model.addAttribute("brandId", id);
        model.addAttribute("logoUrl", response.getLogoUrl());
        return "admin/brand/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable Integer id,
                              @Valid @ModelAttribute("brandRequest") BrandRequest request,
                              BindingResult result,
                              @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/brand/edit";
        }
        
        try {
            brandService.updateBrand(id, request, logoFile);
            ra.addFlashAttribute("success", "Cập nhật thương hiệu thành công!");
            return "redirect:/admin/brands";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/brands/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            brandService.deleteBrand(id);
            ra.addFlashAttribute("success", "Xóa thương hiệu thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/brands";
    }
}
