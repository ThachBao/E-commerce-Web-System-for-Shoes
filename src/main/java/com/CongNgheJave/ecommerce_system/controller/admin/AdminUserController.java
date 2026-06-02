package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.dto.request.UserCreationRequest;
import com.CongNgheJave.ecommerce_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            Authentication auth,
            Model model) {

        Page<AppUser> userPage = userService.searchUsers(keyword, role, PageRequest.of(page, size));

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);

        // Lấy vai trò người đăng nhập để phân biệt ẩn/hiển nút khóa ở giao diện
        String creatorRole = auth.getAuthorities().iterator().next().getAuthority();
        String creatorUsername = auth.getName();
        model.addAttribute("creatorRole", creatorRole);
        model.addAttribute("creatorUsername", creatorUsername);

        return "admin/user/list";
    }

    @PostMapping("/toggle/{id}")
    public String toggleUserActive(
            @PathVariable Integer id,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            String creatorUsername = auth.getName();
            String creatorRole = auth.getAuthorities().iterator().next().getAuthority();

            userService.toggleUserActive(id, creatorUsername, creatorRole);
            ra.addFlashAttribute("success", "Cập nhật trạng thái tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(
            @PathVariable Integer id,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            String creatorUsername = auth.getName();
            String creatorRole = auth.getAuthorities().iterator().next().getAuthority();

            userService.resetPassword(id, creatorUsername, creatorRole);
            ra.addFlashAttribute("success", "Đã đặt lại mật khẩu về mặc định (admin123) và yêu cầu đổi mật khẩu thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể đặt lại mật khẩu: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/create")
    public String createUser(
            @ModelAttribute("userRequest") UserCreationRequest requestDto,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            String creatorUsername = auth.getName();
            userService.createUser(requestDto, creatorUsername);
            ra.addFlashAttribute("success", "Tạo tài khoản mới thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể tạo tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Integer id,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            String creatorUsername = auth.getName();
            String creatorRole = auth.getAuthorities().iterator().next().getAuthority();

            userService.deleteUser(id, creatorUsername, creatorRole);
            ra.addFlashAttribute("success", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
