package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final AppUserRepository userRepository;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                return Integer.parseInt(auth.getName());
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return null;
    }

    @GetMapping
    public String profile(Model model) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @org.springframework.web.bind.annotation.PostMapping("/update")
    public String updateProfile(@org.springframework.web.bind.annotation.RequestParam("fullName") String fullName,
                                @org.springframework.web.bind.annotation.RequestParam("email") String email,
                                @org.springframework.web.bind.annotation.RequestParam("phone") String phone,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        }
        
        return "redirect:/profile";
    }
}
