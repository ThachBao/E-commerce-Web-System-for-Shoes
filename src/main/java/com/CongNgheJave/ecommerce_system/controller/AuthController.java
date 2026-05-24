package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import com.CongNgheJave.ecommerce_system.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, org.springframework.ui.Model model) {
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/login")
    public String authenticate(@RequestParam String username, @RequestParam String password, @RequestParam(required = false) String redirect, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            AppUser user = userRepository.findByUsername(username).orElseThrow();
            String jwtToken = jwtService.generateToken(user);

            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(cookie);

            if (redirect != null && !redirect.trim().isEmpty()) {
                return "redirect:" + redirect;
            }
            return "redirect:/";
        } catch (Exception e) {
            String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
            return "redirect:/login?error=true" + redirectParam;
        }
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String redirect, org.springframework.ui.Model model) {
        model.addAttribute("redirect", redirect);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String redirect
    ) {
        String redirectParam = (redirect != null && !redirect.trim().isEmpty()) ? "&redirect=" + redirect : "";
        
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=UsernameExists" + redirectParam;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(firstName + " " + lastName);
        user.setRole("ROLE_USER");
        
        userRepository.save(user);

        return "redirect:/login?registered=true" + redirectParam;
    }

    @GetMapping("/forgot-password")
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
}
