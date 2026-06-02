package com.CongNgheJave.ecommerce_system.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String path = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");

        boolean isAjaxOrApi = (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equals(requestedWith)
                || path.startsWith("/carts/")
                || path.startsWith("/api/");

        if (isAjaxOrApi) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\",\"error\":\"Vui lòng đăng nhập để thực hiện thao tác này!\"}");
        } else {
            if (path.startsWith("/admin")) {
                response.sendRedirect("/admin/login");
            } else {
                response.sendRedirect("/login");
            }
        }
    }
}
