package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Size (Kích cỡ giày).
 */
@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    /** Tìm kích cỡ theo tên (vd: "40") */
    Optional<Size> findByName(String name);

    /** Tìm kích cỡ theo tên có phân trang (tìm kiếm tương đối) */
    Page<Size> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    /** Kiểm tra tên kích cỡ đã tồn tại chưa */
    boolean existsByName(String name);
}
