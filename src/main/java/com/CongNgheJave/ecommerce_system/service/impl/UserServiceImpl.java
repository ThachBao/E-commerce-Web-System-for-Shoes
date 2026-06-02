package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.dto.request.UserCreationRequest;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import com.CongNgheJave.ecommerce_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AppUser> searchUsers(String keyword, String role, Pageable pageable) {
        return userRepository.searchUsers(keyword, role, pageable);
    }

    @Override
    @Transactional
    public void toggleUserActive(Integer targetUserId, String creatorUsername, String creatorRole) {
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + targetUserId));

        // 1. Bảo vệ tài khoản hệ thống mặc định
        if (Boolean.TRUE.equals(targetUser.getIsSystem())) {
            throw new IllegalArgumentException("Không thể khóa hoặc chỉnh sửa tài khoản hệ thống mặc định!");
        }

        // 2. Không cho tự khóa chính mình
        boolean isSelf = targetUser.getUsername().equals(creatorUsername);
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            if (targetUser.getId().equals(creatorId)) {
                isSelf = true;
            }
        } catch (NumberFormatException ignored) {}

        if (isSelf) {
            throw new IllegalArgumentException("Bạn không thể tự khóa tài khoản của chính mình!");
        }

        // 3. Phân quyền chặt chẽ dựa trên vai trò thực tế của người thực hiện
        AppUser creator = null;
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            creator = userRepository.findById(creatorId).orElse(null);
        } catch (NumberFormatException ignored) {}

        if (creator == null) {
            creator = userRepository.findByUsername(creatorUsername).orElse(null);
        }

        if (creator == null) {
            throw new AccessDeniedException("Không xác định được thông tin quản trị viên thực hiện!");
        }

        // Kiểm tra vai trò thao tác
        if ("ROLE_ADMIN".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên tối cao (Root Admin / isSystem = true) mới được tác động lên tài khoản ADMIN khác
            if (!Boolean.TRUE.equals(creator.getIsSystem())) {
                throw new AccessDeniedException("Chỉ Quản trị viên tối cao (Root Admin) mới có quyền khóa hoặc mở khóa tài khoản của Quản trị viên khác!");
            }
        } else if ("ROLE_STAFF".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên (ROLE_ADMIN) mới được tác động lên tài khoản STAFF
            if (!"ROLE_ADMIN".equals(creator.getRole())) {
                throw new AccessDeniedException("Chỉ Quản trị viên mới có quyền khóa hoặc mở khóa tài khoản của Nhân viên!");
            }
        }

        // Đảo ngược trạng thái hoạt động
        targetUser.setIsActive(!targetUser.getIsActive());
        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void resetPassword(Integer targetUserId, String creatorUsername, String creatorRole) {
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + targetUserId));

        // 1. Bảo vệ tài khoản hệ thống mặc định
        if (Boolean.TRUE.equals(targetUser.getIsSystem())) {
            throw new IllegalArgumentException("Không thể đặt lại mật khẩu của tài khoản hệ thống mặc định!");
        }

        // 2. Phân quyền chặt chẽ dựa trên vai trò thực tế của người thực hiện
        AppUser creator = null;
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            creator = userRepository.findById(creatorId).orElse(null);
        } catch (NumberFormatException ignored) {}

        if (creator == null) {
            creator = userRepository.findByUsername(creatorUsername).orElse(null);
        }

        if (creator == null) {
            throw new AccessDeniedException("Không xác định được thông tin quản trị viên thực hiện!");
        }

        // Kiểm tra vai trò thao tác
        if ("ROLE_ADMIN".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên tối cao (Root Admin / isSystem = true) mới được tác động lên tài khoản ADMIN khác
            if (!Boolean.TRUE.equals(creator.getIsSystem())) {
                throw new AccessDeniedException("Chỉ Quản trị viên tối cao (Root Admin) mới có quyền đặt lại mật khẩu cho tài khoản của Quản trị viên khác!");
            }
        } else if ("ROLE_STAFF".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên (ROLE_ADMIN) mới được tác động lên tài khoản STAFF
            if (!"ROLE_ADMIN".equals(creator.getRole())) {
                throw new AccessDeniedException("Chỉ Quản trị viên mới có quyền đặt lại mật khẩu cho tài khoản của Nhân viên!");
            }
        }

        // Đặt lại mật khẩu mặc định admin123
        targetUser.setPassword(passwordEncoder.encode("admin123"));
        // Cưỡng chế đổi mật khẩu ở lần đăng nhập tiếp theo
        targetUser.setForceChangePassword(true);

        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public AppUser createUser(UserCreationRequest request, String creatorUsername) {
        // Tìm thông tin quản trị viên thực hiện
        AppUser creator = null;
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            creator = userRepository.findById(creatorId).orElse(null);
        } catch (NumberFormatException ignored) {}

        if (creator == null) {
            creator = userRepository.findByUsername(creatorUsername).orElse(null);
        }

        if (creator == null) {
            throw new AccessDeniedException("Không xác định được thông tin quản trị viên thực hiện!");
        }

        // 1. Kiểm tra giới hạn quyền hạn khi tạo tài khoản
        if ("ROLE_ADMIN".equals(request.getRole())) {
            // Chỉ Quản trị viên tối cao (Root Admin / isSystem = true) mới có quyền tạo ADMIN khác
            if (!Boolean.TRUE.equals(creator.getIsSystem())) {
                throw new AccessDeniedException("Chỉ Quản trị viên tối cao (Root Admin) mới có quyền tạo tài khoản Quản trị viên khác!");
            }
        } else if ("ROLE_STAFF".equals(request.getRole())) {
            // Chỉ Quản trị viên (ROLE_ADMIN) mới có quyền tạo nhân viên STAFF
            if (!"ROLE_ADMIN".equals(creator.getRole())) {
                throw new AccessDeniedException("Chỉ Quản trị viên mới có quyền tạo tài khoản Nhân viên!");
            }
        }

        // 2. Validate dữ liệu trùng lặp
        if (userRepository.findByUsername(request.getUsername().trim()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.findByEmailIgnoreCase(request.getEmail().trim()).isPresent()) {
                throw new IllegalArgumentException("Địa chỉ Email đã được sử dụng!");
            }
        }

        // 3. Khởi tạo tài khoản mới
        AppUser newUser = new AppUser();
        newUser.setUsername(request.getUsername().trim());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName().trim());
        newUser.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        newUser.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        newUser.setRole(request.getRole());
        newUser.setIsActive(true);
        newUser.setIsSystem(false);
        newUser.setForceChangePassword(false);

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer targetUserId, String creatorUsername, String creatorRole) {
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + targetUserId));

        // 1. Bảo vệ tài khoản hệ thống mặc định
        if (Boolean.TRUE.equals(targetUser.getIsSystem())) {
            throw new IllegalArgumentException("Không thể xóa tài khoản hệ thống mặc định!");
        }

        // 2. Không cho tự xóa chính mình
        boolean isSelf = targetUser.getUsername().equals(creatorUsername);
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            if (targetUser.getId().equals(creatorId)) {
                isSelf = true;
            }
        } catch (NumberFormatException ignored) {}

        if (isSelf) {
            throw new IllegalArgumentException("Bạn không thể tự xóa tài khoản của chính mình!");
        }

        // 3. Phân quyền chặt chẽ dựa trên vai trò thực tế của người thực hiện
        AppUser creator = null;
        try {
            Integer creatorId = Integer.parseInt(creatorUsername);
            creator = userRepository.findById(creatorId).orElse(null);
        } catch (NumberFormatException ignored) {}

        if (creator == null) {
            creator = userRepository.findByUsername(creatorUsername).orElse(null);
        }

        if (creator == null) {
            throw new AccessDeniedException("Không xác định được thông tin quản trị viên thực hiện!");
        }

        // Kiểm tra vai trò thao tác
        if ("ROLE_ADMIN".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên tối cao (Root Admin / isSystem = true) mới được xóa tài khoản ADMIN khác
            if (!Boolean.TRUE.equals(creator.getIsSystem())) {
                throw new AccessDeniedException("Chỉ Quản trị viên tối cao (Root Admin) mới có quyền xóa tài khoản của Quản trị viên khác!");
            }
        } else if ("ROLE_STAFF".equals(targetUser.getRole())) {
            // Chỉ Quản trị viên (ROLE_ADMIN) mới được xóa tài khoản STAFF
            if (!"ROLE_ADMIN".equals(creator.getRole())) {
                throw new AccessDeniedException("Chỉ Quản trị viên mới có quyền xóa tài khoản của Nhân viên!");
            }
        }

        // 4. Thực hiện xóa an toàn
        try {
            userRepository.delete(targetUser);
            userRepository.flush(); // Đồng bộ ngay với DB để bắt lỗi ràng buộc FK nếu có
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Tài khoản này đã có lịch sử giao dịch hoặc xử lý đơn hàng trên hệ thống, không thể xóa vĩnh viễn! Vui lòng sử dụng chức năng KHÓA để vô hiệu hóa tài khoản.");
        }
    }
}
