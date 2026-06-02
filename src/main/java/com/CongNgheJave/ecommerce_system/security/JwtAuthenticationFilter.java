package com.CongNgheJave.ecommerce_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip auth endpoints and static resources
        String path = request.getServletPath();
        if (path.contains("/api/auth") || path.equals("/login") || path.equals("/admin/login") || path.equals("/admin/logout") || path.equals("/register") ||
            path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") ||
            path.startsWith("/uploads/") || path.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        
        // Try to get JWT from Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        // If not in header, try to get from cookies (for Thymeleaf frontend)
        boolean isAdminPath = path.startsWith("/admin");
        if (jwt == null && request.getCookies() != null) {
            String cookieName = isAdminPath ? "adminJwtToken" : "jwtToken";
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String username = jwtService.extractUsername(jwt);
        final Integer userId = jwtService.extractUserId(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                // Nếu tài khoản không còn tồn tại trong DB mới, xoá cookie JWT lỗi thời này
                String cookieName = isAdminPath ? "adminJwtToken" : "jwtToken";
                Cookie expiredCookie = new Cookie(cookieName, null);
                expiredCookie.setPath("/");
                expiredCookie.setHttpOnly(true);
                expiredCookie.setMaxAge(0);
                response.addCookie(expiredCookie);
                
                filterChain.doFilter(request, response);
                return;
            }

            // Kiểm tra tài khoản có bị khoá không (isActive = false).
            // Nếu bị khoá: xoá cookie JWT và chuyển hướng về trang đăng nhập ngay lập tức.
            if (!userDetails.isEnabled()) {
                // Xoá cookie JWT để buộc đăng xuất
                String cookieName = isAdminPath ? "adminJwtToken" : "jwtToken";
                Cookie expiredCookie = new Cookie(cookieName, null);
                expiredCookie.setPath("/");
                expiredCookie.setHttpOnly(true);
                expiredCookie.setMaxAge(0);
                response.addCookie(expiredCookie);

                // Redirect về trang login với thông báo tài khoản bị khoá
                if (isAdminPath) {
                    response.sendRedirect("/admin/login?error=Locked");
                } else {
                    response.sendRedirect("/login?error=Locked");
                }
                return;
            }

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Set name as userId instead of username, so CartController's auth.getName() gets the ID
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        String.valueOf(userId), // Set principal as the ID string
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
