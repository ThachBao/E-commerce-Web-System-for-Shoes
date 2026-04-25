package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Brand (Thương hiệu).
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    /** Tìm thương hiệu theo slug */
    Optional<Brand> findBySlug(String slug);

    /** Tìm thương hiệu theo mã */
    Optional<Brand> findByCode(String code);

    /** Kiểm tra mã thương hiệu đã tồn tại chưa */
    boolean existsByCode(String code);

    /** Kiểm tra slug đã tồn tại chưa */
    boolean existsBySlug(String slug);

    /** Kiểm tra tên thương hiệu đã tồn tại chưa */
    boolean existsByName(String name);

    /** Lấy tất cả thương hiệu đang hoạt động (dùng cho dropdown/combobox) */
    List<Brand> findByIsActiveTrue();

    /** Lấy danh sách thương hiệu đang hoạt động có phân trang (dùng cho list/bảng quản trị) */
    Page<Brand> findByIsActiveTrue(Pageable pageable);

    /** Tìm thương hiệu theo tên (không phân biệt hoa thường) có phân trang */
    Page<Brand> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    /** Kiểm tra thương hiệu có sản phẩm hay không (tránh xóa thương hiệu đang dùng) */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END "
         + "FROM Product p WHERE p.brand.id = :brandId")
    boolean hasProducts(@Param("brandId") Integer brandId);
}
