package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.dto.request.CategoryRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CategoryResponse;
import com.CongNgheJave.ecommerce_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller quản trị danh mục sản phẩm.
 */
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    // Hiển thị danh sách danh mục có phân trang
    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<CategoryResponse> categoryPage = categoryService.getCategories(keyword, PageRequest.of(page, size));
        
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        
        return "admin/category/list";
    }

    // Hiển thị form thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        // Lấy danh sách danh mục gốc để làm danh mục cha
        model.addAttribute("parentCategories", categoryService.getActiveRootCategories());
        return "admin/category/add";
    }

    // Xử lý thêm mới
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("categoryRequest") CategoryRequest request,
                              BindingResult result,
                              RedirectAttributes ra,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("parentCategories", categoryService.getActiveRootCategories());
            return "admin/category/add";
        }
        
        try {
            categoryService.createCategory(request);
            ra.addFlashAttribute("success", "Thêm danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/categories/add";
        }
    }

    // Hiển thị form cập nhật
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CategoryResponse response = categoryService.getCategoryById(id);
        
        // Chuyển đổi từ Response sang Request để bind vào form
        CategoryRequest request = new CategoryRequest();
        request.setParentId(response.getParentId());
        request.setCode(response.getCode());
        request.setName(response.getName());
        request.setIsActive(response.getIsActive());
        
        model.addAttribute("categoryRequest", request);
        model.addAttribute("categoryId", id);
        model.addAttribute("parentCategories", categoryService.getActiveRootCategories());
        return "admin/category/edit";
    }

    // Xử lý cập nhật
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Integer id,
                                 @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
                                 BindingResult result,
                                 RedirectAttributes ra,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            model.addAttribute("parentCategories", categoryService.getActiveRootCategories());
            return "admin/category/edit";
        }
        
        try {
            categoryService.updateCategory(id, request);
            ra.addFlashAttribute("success", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/categories/edit/" + id;
        }
    }

    // Xử lý xóa
    @GetMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("success", "Xóa danh mục thành công!");
            
            // Kiểm tra nếu trang hiện tại bị trống sau khi xóa, thì lùi lại 1 trang
            if (page > 0) {
                Page<CategoryResponse> categoryPage = categoryService.getCategories(keyword, PageRequest.of(page, 10));
                if (categoryPage.getContent().isEmpty()) {
                    page = page - 1;
                }
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        ra.addAttribute("page", page);
        if (keyword != null && !keyword.trim().isEmpty()) {
            ra.addAttribute("keyword", keyword);
        }
        return "redirect:/admin/categories";
    }
}
