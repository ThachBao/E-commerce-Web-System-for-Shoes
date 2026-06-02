package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.dto.request.UserCreationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<AppUser> searchUsers(String keyword, String role, Pageable pageable);
    void toggleUserActive(Integer targetUserId, String creatorUsername, String creatorRole);
    void resetPassword(Integer targetUserId, String creatorUsername, String creatorRole);
    AppUser createUser(UserCreationRequest request, String creatorUsername);
    void deleteUser(Integer targetUserId, String creatorUsername, String creatorRole);
}
