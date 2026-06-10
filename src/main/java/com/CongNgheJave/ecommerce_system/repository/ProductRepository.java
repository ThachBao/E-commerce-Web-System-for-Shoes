package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Product.
 * Hỗ trợ tìm kiếm, lọc đa tiêu chí và phân trang.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    // ==================== Tìm kiếm theo field duy nhất ====================

    /** Tìm sản phẩm theo slug (URL thân thiện) */
    Optional<Product> findBySlug(String slug);

    /** Tìm sản phẩm theo mã sản phẩm */
    Optional<Product> findByCode(String code);

    // ==================== Kiểm tra tồn tại ====================

    /** Kiểm tra mã sản phẩm đã tồn tại chưa */
    boolean existsByCode(String code);

    /** Kiểm tra slug đã tồn tại chưa */
    boolean existsBySlug(String slug);

    // ==================== Danh sách theo trạng thái ====================

    /** Lấy danh sách sản phẩm đang hoạt động (có phân trang) */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /** Lấy danh sách sản phẩm nổi bật đang hoạt động */
    List<Product> findByIsFeaturedTrueAndIsActiveTrue();

    // ==================== Lọc theo quan hệ ====================

    /** Lấy sản phẩm theo danh mục (có phân trang) */
    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);

    /** Lấy sản phẩm theo thương hiệu (có phân trang) */
    Page<Product> findByBrandIdAndIsActiveTrue(Integer brandId, Pageable pageable);

    // ==================== Tìm kiếm theo từ khóa ====================

    /** Tìm kiếm sản phẩm theo tên (không phân biệt hoa thường) */
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String keyword, Pageable pageable);

    // ==================== Query phức tạp (JPQL) ====================

    /**
     * Tìm kiếm sản phẩm kết hợp nhiều tiêu chí (keyword, category, brand, gender).
     * Các tham số null sẽ được bỏ qua trong điều kiện lọc.
     */
    @Query("SELECT p FROM Product p WHERE "
         + "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
         + "AND (:categoryId IS NULL OR p.category.id = :categoryId) "
         + "AND (:brandId IS NULL OR p.brand.id = :brandId) "
         + "AND (:gender IS NULL OR p.gender = :gender) "
         + "AND (:isActive IS NULL OR p.isActive = :isActive)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("brandId") Integer brandId,
                                 @Param("gender") String gender,
                                 @Param("isActive") Boolean isActive,
                                 Pageable pageable);

    /** Đếm số sản phẩm đang hoạt động theo danh mục */
    long countByCategoryIdAndIsActiveTrue(Integer categoryId);

    /** Đếm số sản phẩm đang hoạt động theo thương hiệu */
    long countByBrandIdAndIsActiveTrue(Integer brandId);

    /** Kiểm tra xem sản phẩm đã có trong đơn hàng nào chưa */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.variant.product.id = :productId")
    boolean isProductOrdered(@Param("productId") Integer productId);
}
