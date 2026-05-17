package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Category.
 * Hỗ trợ phân cấp danh mục (parent → children).
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // ==================== Tìm kiếm theo field duy nhất ====================

    /** Tìm danh mục theo slug */
    Optional<Category> findBySlug(String slug);

    /** Tìm danh mục theo mã */
    Optional<Category> findByCode(String code);

    // ==================== Kiểm tra tồn tại ====================

    /** Kiểm tra mã danh mục đã tồn tại chưa */
    boolean existsByCode(String code);

    /** Kiểm tra slug đã tồn tại chưa */
    boolean existsBySlug(String slug);

    // ==================== Danh sách theo trạng thái ====================

    /** Lấy tất cả danh mục đang hoạt động */
    List<Category> findByIsActiveTrue();

    /** Lấy danh sách danh mục đang hoạt động có phân trang */
    Page<Category> findByIsActiveTrue(Pageable pageable);

    // ==================== Phân cấp danh mục ====================

    /** Lấy danh mục gốc (không có parent) đang hoạt động */
    List<Category> findByParentIsNullAndIsActiveTrue();

    /** Lấy danh mục gốc đang hoạt động có phân trang */
    Page<Category> findByParentIsNullAndIsActiveTrue(Pageable pageable);

    /** Lấy danh mục con theo parent ID đang hoạt động */
    List<Category> findByParentIdAndIsActiveTrue(Integer parentId);

    /** Lấy tất cả danh mục gốc (kể cả không hoạt động) */
    List<Category> findByParentIsNull();

    // ==================== Tìm kiếm ====================

    /** Tìm danh mục theo tên (không phân biệt hoa thường) */
    List<Category> findByNameContainingIgnoreCase(String keyword);

    /** Tìm danh mục theo tên có phân trang */
    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    /** Đếm số danh mục con trực tiếp */
    long countByParentId(Integer parentId);

    /** Lấy tất cả danh mục con theo parent ID (kể cả không hoạt động) */
    List<Category> findByParentId(Integer parentId);

    /** Kiểm tra danh mục có sản phẩm hay không (tránh xóa danh mục đang dùng) */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END "
         + "FROM Product p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Integer categoryId);
}
