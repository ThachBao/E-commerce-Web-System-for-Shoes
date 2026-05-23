package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý truy vấn bảng Product_Image (Hình ảnh sản phẩm).
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    /** Lấy tất cả ảnh của một sản phẩm */
    List<ProductImage> findByProductId(Integer productId);

    /** Lấy ảnh thumbnail (ảnh đại diện) của sản phẩm */
    Optional<ProductImage> findByProductIdAndIsThumbnailTrue(Integer productId);

    /** Đếm số ảnh của một sản phẩm */
    long countByProductId(Integer productId);

    /** Xóa tất cả ảnh của một sản phẩm */
    void deleteByProductId(Integer productId);
}
