package com.CongNgheJave.ecommerce_system.service.ai;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatAuthContextService {

    private final AppUserRepository userRepository;

    public Optional<AppUser> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                int userId = Integer.parseInt(auth.getName());
                return userRepository.findById(userId);
            } catch (NumberFormatException e) {
                // Fallback or ignore
            }
        }
        return Optional.empty();
    }
}
