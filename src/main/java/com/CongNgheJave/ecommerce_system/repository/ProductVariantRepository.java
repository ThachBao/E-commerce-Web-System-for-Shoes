package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
