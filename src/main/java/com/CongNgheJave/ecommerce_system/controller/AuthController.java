package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import com.CongNgheJave.ecommerce_system.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import com.CongNgheJave.ecommerce_system.entity.PasswordResetToken;
import com.CongNgheJave.ecommerce_system.repository.PasswordResetTokenRepository;
import com.CongNgheJave.ecommerce_system.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginUser(
            @Valid @ModelAttribute com.CongNgheJave.ecommerce_system.dto.auth.LoginRequestDTO request,
            org.springframework.validation.BindingResult bindingResult,
            HttpServletResponse response
    ) {
        String redirect = request.getRedirect();
        if (bindingResult.hasErrors()) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/login?error=InvalidInput" + redirectParam;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            AppUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            
            // Block Admin/Staff accounts from client login
            if ("ROLE_ADMIN".equals(user.getRole()) || "ROLE_STAFF".equals(user.getRole()) || "ROLE_ROOT_ADMIN".equals(user.getRole())) {
                String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
                return "redirect:/login?error=AdminBlocked" + redirectParam;
            }

            String jwtToken = jwtService.generateToken(user);

            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Requires HTTPS on VPS
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            cookie.setAttribute("SameSite", "Strict"); // Prevent CSRF
            response.addCookie(cookie);

            if (redirect != null && !redirect.trim().isEmpty()) {
                return "redirect:" + redirect;
            }
            return "redirect:/";
        } catch (DisabledException e) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/login?error=Locked" + redirectParam;
        } catch (Exception e) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/login?error=true" + redirectParam;
        }
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute com.CongNgheJave.ecommerce_system.dto.auth.RegisterRequestDTO request,
            org.springframework.validation.BindingResult bindingResult
    ) {
        String redirectParam = (request.getRedirect() != null && !request.getRedirect().trim().isEmpty()) ? "&redirect=" + request.getRedirect() : "";
        
        if (bindingResult.hasErrors()) {
            return "redirect:/register?error=InvalidInput" + redirectParam;
        }
        
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        String phone = request.getPhone();
        String password = request.getPassword();
        
        // Check username duplicate
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=UsernameExists" + redirectParam;
        }
        
        // Check email duplicate
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return "redirect:/register?error=EmailExists" + redirectParam;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(firstName + " " + lastName);
        user.setRole("ROLE_USER");
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }
        
        userRepository.save(user);

        return "redirect:/login?registered=true" + redirectParam;
    }

    @GetMapping({"/forgot-password", "/forgot password", "/forgot%20password"})
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login?logout=true";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "admin/auth/login";
    }

    @PostMapping("/admin/login")
    public String adminLoginUser(
            @Valid @ModelAttribute com.CongNgheJave.ecommerce_system.dto.auth.LoginRequestDTO request,
            org.springframework.validation.BindingResult bindingResult,
            HttpServletResponse response
    ) {
        String redirect = request.getRedirect();
        if (bindingResult.hasErrors()) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/admin/login?error=InvalidInput" + redirectParam;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            AppUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            
            // Check if the user is an ADMIN or STAFF
            if (!"ROLE_ADMIN".equals(user.getRole()) && !"ROLE_STAFF".equals(user.getRole())) {
                return "redirect:/admin/login?error=Unauthorized";
            }

            String jwtToken = jwtService.generateToken(user);

            Cookie cookie = new Cookie("adminJwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Requires HTTPS on VPS
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            cookie.setAttribute("SameSite", "Strict"); // Prevent CSRF
            response.addCookie(cookie);

            if (redirect != null && !redirect.trim().isEmpty()) {
                return "redirect:" + redirect;
            }
            return "redirect:/admin/products";
        } catch (DisabledException e) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/admin/login?error=Locked" + redirectParam;
        } catch (Exception e) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/admin/login?error=true" + redirectParam;
        }
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpServletResponse response) {
        Cookie cookie = new Cookie("adminJwtToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/admin/login?logout=true";
    }

    @GetMapping({"/admin", "/admin/"})
    public String adminRootRedirect() {
        return "redirect:/admin/products";
    }

    @PostMapping("/forgot-password")
    @Transactional
    public String processForgotPassword(@RequestParam String email) {
        try {
            Optional<AppUser> userOpt = userRepository.findByUsername(email);
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByEmailIgnoreCase(email);
            }

            if (userOpt.isEmpty()) {
                return "redirect:/forgot-password?error=UserNotFound";
            }

            AppUser user = userOpt.get();

            // Clear any existing reset tokens for this user
            resetTokenRepository.deleteByUser_Id(user.getId());

            // Generate new token valid for 15 minutes
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user, 15);
            resetTokenRepository.save(resetToken);

            // Send email
            String cleanBaseUrl = (baseUrl != null && baseUrl.endsWith("/")) ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            String resetLink = cleanBaseUrl + "/reset-password?token=" + token;
            boolean emailSent = emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
            if (!emailSent) {
                resetTokenRepository.delete(resetToken);
                return "redirect:/forgot-password?error=SystemError";
            }

            return "redirect:/forgot-password?success=true";
        } catch (Exception e) {
            log.error("Lỗi khi xử lý quên mật khẩu: ", e);
            return "redirect:/forgot-password?error=SystemError";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return "redirect:/login?error=InvalidToken";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    @Transactional
    public String processResetPassword(@RequestParam String token, @RequestParam String password) {
        Optional<PasswordResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return "redirect:/login?error=InvalidToken";
        }

        PasswordResetToken resetToken = tokenOpt.get();
        AppUser user = resetToken.getUser();

        // Validate password: minimum 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return "redirect:/reset-password?token=" + token + "&error=InvalidPassword";
        }

        // Update password with BCrypt hashing
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Delete the utilized token
        resetTokenRepository.delete(resetToken);

        return "redirect:/login?resetSuccess=true";
    }
}
