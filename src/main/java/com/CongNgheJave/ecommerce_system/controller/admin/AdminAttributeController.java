package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.dto.request.ColorRequest;
import com.CongNgheJave.ecommerce_system.dto.request.SizeRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ColorResponse;
import com.CongNgheJave.ecommerce_system.dto.response.SizeResponse;
import com.CongNgheJave.ecommerce_system.service.ColorService;
import com.CongNgheJave.ecommerce_system.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản trị các thuộc tính (Màu sắc, Kích cỡ).
 */
@Controller
@RequestMapping("/admin/attributes")
@RequiredArgsConstructor
public class AdminAttributeController {

    private final ColorService colorService;
    private final SizeService sizeService;

    // --- Quản lý Màu sắc ---

    @GetMapping("/colors")
    public String listColors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<ColorResponse> colorPage = colorService.getColors(keyword, PageRequest.of(page, size));
        model.addAttribute("colors", colorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", colorPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/attribute/color-list";
    }

    @PostMapping("/colors/add")
    public String addColor(@Valid @ModelAttribute("colorRequest") ColorRequest request,
                           BindingResult result,
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu màu sắc không hợp lệ!");
            return "redirect:/admin/attributes/colors";
        }
        try {
            colorService.createColor(request);
            ra.addFlashAttribute("success", "Thêm màu sắc thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/colors";
    }

    @PostMapping("/colors/edit/{id}")
    public String updateColor(@PathVariable Integer id,
                              @Valid @ModelAttribute("colorRequest") ColorRequest request,
                              BindingResult result,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu màu sắc không hợp lệ!");
            return "redirect:/admin/attributes/colors";
        }
        try {
            colorService.updateColor(id, request);
            ra.addFlashAttribute("success", "Cập nhật màu sắc thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/colors";
    }

    @GetMapping("/colors/delete/{id}")
    public String deleteColor(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            colorService.deleteColor(id);
            ra.addFlashAttribute("success", "Xóa màu sắc thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/colors";
    }

    // --- Quản lý Kích cỡ ---

    @GetMapping("/sizes")
    public String listSizes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<SizeResponse> sizePage = sizeService.getSizes(keyword, PageRequest.of(page, size));
        model.addAttribute("sizes", sizePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sizePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/attribute/size-list";
    }

    @PostMapping("/sizes/add")
    public String addSize(@Valid @ModelAttribute("sizeRequest") SizeRequest request,
                          BindingResult result,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu kích cỡ không hợp lệ!");
            return "redirect:/admin/attributes/sizes";
        }
        try {
            sizeService.createSize(request);
            ra.addFlashAttribute("success", "Thêm kích cỡ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/sizes";
    }

    @PostMapping("/sizes/edit/{id}")
    public String updateSize(@PathVariable Integer id,
                             @Valid @ModelAttribute("sizeRequest") SizeRequest request,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu kích cỡ không hợp lệ!");
            return "redirect:/admin/attributes/sizes";
        }
        try {
            sizeService.updateSize(id, request);
            ra.addFlashAttribute("success", "Cập nhật kích cỡ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/sizes";
    }

    @GetMapping("/sizes/delete/{id}")
    public String deleteSize(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            sizeService.deleteSize(id);
            ra.addFlashAttribute("success", "Xóa kích cỡ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/attributes/sizes";
    }
}
