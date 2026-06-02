package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.dto.response.CategoryResponse;
import com.CongNgheJave.ecommerce_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(basePackages = "com.CongNgheJave.ecommerce_system.controller")
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
    private final com.CongNgheJave.ecommerce_system.repository.AppUserRepository userRepository;

    @ModelAttribute("menuCategories")
    public List<CategoryResponse> getMenuCategories() {
        try {
            return categoryService.getActiveRootCategories();
        } catch (Exception e) {
            return List.of();
        }
    }

    @ModelAttribute("currentUser")
    public com.CongNgheJave.ecommerce_system.entity.AppUser getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                Integer userId = Integer.parseInt(auth.getName());
                return userRepository.findById(userId).orElse(null);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
