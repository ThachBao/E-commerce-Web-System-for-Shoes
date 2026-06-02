package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Color (Màu sắc).
 */
@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

    /** Tìm màu sắc theo tên (vd: "Đen") */
    Optional<Color> findByName(String name);

    /** Tìm màu sắc theo tên có phân trang (tìm kiếm tương đối) */
    Page<Color> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    /** Kiểm tra tên màu đã tồn tại chưa */
    boolean existsByName(String name);
}
