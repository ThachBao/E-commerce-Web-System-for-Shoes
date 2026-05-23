package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity hình ảnh sản phẩm.
 * Mỗi sản phẩm có nhiều ảnh, 1 ảnh được đánh dấu thumbnail.
 */
@Entity
@Table(name = "Product_Image")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    @ToString.Exclude
    private Product product;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    // true = ảnh đại diện sản phẩm
    @Column(nullable = false)
    private Boolean isThumbnail = false;
}
