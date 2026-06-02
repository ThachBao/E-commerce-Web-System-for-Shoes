package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.dto.request.ProductVariantRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductResponse;
import com.CongNgheJave.ecommerce_system.dto.response.ProductVariantResponse;
import com.CongNgheJave.ecommerce_system.service.ColorService;
import com.CongNgheJave.ecommerce_system.service.ProductService;
import com.CongNgheJave.ecommerce_system.service.ProductVariantService;
import com.CongNgheJave.ecommerce_system.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản trị kho hàng (Biến thể sản phẩm).
 */
@Controller
@RequestMapping("/admin/warehouse")
@RequiredArgsConstructor
public class AdminWarehouseController {

    private final ProductService productService;
    private final ProductVariantService variantService;
    private final ColorService colorService;
    private final SizeService sizeService;

    // Xem danh sách biến thể của một sản phẩm
    @GetMapping("/product/{productId}")
    public String listVariants(@PathVariable Integer productId, Model model) {
        ProductResponse product = productService.getProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("variants", variantService.getVariantsByProductId(productId));
        
        // Dữ liệu để thêm mới biến thể
        ProductVariantRequest newVariant = new ProductVariantRequest();
        newVariant.setProductId(productId);
        model.addAttribute("variantRequest", newVariant);
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("sizes", sizeService.getAllSizes());
        
        return "admin/warehouse/variants";
    }

    @PostMapping("/variant/add")
    public String addVariant(@Valid @ModelAttribute("variantRequest") ProductVariantRequest request,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu biến thể không hợp lệ!");
            return "redirect:/admin/warehouse/product/" + request.getProductId();
        }
        
        try {
            variantService.createVariant(request);
            ra.addFlashAttribute("success", "Thêm biến thể thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/warehouse/product/" + request.getProductId();
    }

    @PostMapping("/variant/update/{id}")
    public String updateVariant(@PathVariable Integer id,
                                @Valid @ModelAttribute("variantRequest") ProductVariantRequest request,
                                BindingResult result,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu cập nhật không hợp lệ!");
            return "redirect:/admin/warehouse/product/" + request.getProductId();
        }
        
        try {
            variantService.updateVariant(id, request);
            ra.addFlashAttribute("success", "Cập nhật biến thể thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/warehouse/product/" + request.getProductId();
    }

    @GetMapping("/variant/delete/{id}")
    public String deleteVariant(@PathVariable Integer id, @RequestParam Integer productId, RedirectAttributes ra) {
        try {
            variantService.deleteVariant(id);
            ra.addFlashAttribute("success", "Xóa biến thể thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/warehouse/product/" + productId;
    }
}
