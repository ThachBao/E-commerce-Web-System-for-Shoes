package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Product_Variant (Biến thể sản phẩm).
 * Mỗi biến thể = 1 sản phẩm + 1 size + 1 color → SKU, giá, tồn kho.
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    // ==================== Tìm kiếm ====================

    /** Tìm biến thể theo mã SKU */
    Optional<ProductVariant> findBySku(String sku);

    /** Kiểm tra SKU đã tồn tại chưa */
    boolean existsBySku(String sku);

    /** Tìm biến thể và thực hiện Khóa bi quan (Pessimistic Write Lock) để tránh race condition */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM ProductVariant v WHERE v.id = :id")
    Optional<ProductVariant> findByIdForUpdate(@Param("id") Integer id);

    /** Lấy tất cả biến thể của một sản phẩm */
    List<ProductVariant> findByProductId(Integer productId);

    /** Lấy biến thể đang hoạt động của một sản phẩm */
    List<ProductVariant> findByProductIdAndIsActiveTrue(Integer productId);

    /** Tìm biến thể chính xác theo sản phẩm + size + color */
    Optional<ProductVariant> findByProductIdAndSizeIdAndColorId(Integer productId, Integer sizeId, Integer colorId);

    // ==================== Tồn kho ====================

    /** Lấy các biến thể còn hàng (stockQuantity > 0) của một sản phẩm */
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId "
         + "AND v.stockQuantity > 0 AND v.isActive = true")
    List<ProductVariant> findInStockByProductId(@Param("productId") Integer productId);

    /** Lấy các biến thể hết hàng (stockQuantity = 0) */
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity = 0 AND v.isActive = true")
    List<ProductVariant> findOutOfStock();

    /** Lấy các biến thể sắp hết hàng (stockQuantity <= threshold) */
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity <= :threshold "
         + "AND v.stockQuantity > 0 AND v.isActive = true")
    List<ProductVariant> findLowStock(@Param("threshold") int threshold);

    /** Tính tổng tồn kho của một sản phẩm (tất cả biến thể) */
    @Query("SELECT COALESCE(SUM(v.stockQuantity), 0) FROM ProductVariant v "
         + "WHERE v.product.id = :productId AND v.isActive = true")
    int sumStockByProductId(@Param("productId") Integer productId);

    // ==================== Xóa cascade ====================

    /** Xóa tất cả biến thể theo màu sắc */
    void deleteByColorId(Integer colorId);

    /** Xóa tất cả biến thể theo kích cỡ */
    void deleteBySizeId(Integer sizeId);

    // ==================== Realtime Chat Search ====================
    @Query("SELECT v FROM ProductVariant v "
         + "JOIN FETCH v.product p "
         + "JOIN FETCH v.size s "
         + "JOIN FETCH v.color c "
         + "LEFT JOIN FETCH p.brand b "
         + "LEFT JOIN FETCH p.category cat "
         + "WHERE p.isActive = true AND v.isActive = true "
         + "AND (:brand IS NULL OR :brand = '' OR LOWER(b.name) = LOWER(:brand)) "
         + "AND (:category IS NULL OR :category = '' OR LOWER(cat.name) = LOWER(:category)) "
         + "AND (:gender IS NULL OR :gender = '' OR LOWER(p.gender) = LOWER(:gender)) "
         + "AND (:size IS NULL OR :size = '' OR LOWER(s.name) = LOWER(:size)) "
         + "AND (:color IS NULL OR :color = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :color, '%'))) "
         + "AND (:keyword IS NULL OR :keyword = '' OR "
         + "     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
         + "     LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
         + "     LOWER(cat.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ProductVariant> searchByCriteria(
            @Param("brand") String brand,
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("size") String size,
            @Param("color") String color,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT v FROM ProductVariant v "
         + "JOIN FETCH v.product p "
         + "JOIN FETCH v.size s "
         + "JOIN FETCH v.color c "
         + "LEFT JOIN FETCH p.brand b "
         + "LEFT JOIN FETCH p.category cat "
         + "WHERE p.isActive = true AND v.isActive = true "
         + "ORDER BY v.stockQuantity DESC")
    List<ProductVariant> findTopStockVariants(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT v FROM ProductVariant v "
         + "JOIN FETCH v.product p "
         + "JOIN FETCH v.size s "
         + "JOIN FETCH v.color c "
         + "LEFT JOIN FETCH p.brand b "
         + "LEFT JOIN FETCH p.category cat "
         + "WHERE p.isActive = true AND v.isActive = true "
         + "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
         + "     LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ProductVariant> findProductDetailByKeyword(@Param("keyword") String keyword);

    @Query("SELECT v FROM ProductVariant v "
         + "JOIN FETCH v.product p "
         + "JOIN FETCH v.size s "
         + "JOIN FETCH v.color c "
         + "LEFT JOIN FETCH p.brand b "
         + "LEFT JOIN FETCH p.category cat "
         + "WHERE p.isActive = true AND v.isActive = true "
         + "AND p.isFeatured = true")
    List<ProductVariant> findFeaturedVariants();

    @Query("SELECT v FROM ProductVariant v "
         + "JOIN FETCH v.product p "
         + "JOIN FETCH v.size s "
         + "JOIN FETCH v.color c "
         + "LEFT JOIN FETCH p.brand b "
         + "LEFT JOIN FETCH p.category cat "
         + "WHERE p.isActive = true AND v.isActive = true "
         + "ORDER BY (SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi JOIN oi.order o WHERE oi.variant.product.id = p.id AND o.orderStatus = 'COMPLETED') DESC, p.id DESC")
    List<ProductVariant> findBestSellingVariants();

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.variant.id = :variantId")
    boolean isReferencedInOrders(@Param("variantId") Integer variantId);
}

