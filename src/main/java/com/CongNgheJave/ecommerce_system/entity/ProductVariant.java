package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity biến thể sản phẩm.
 * Mỗi biến thể = 1 sản phẩm + 1 size + 1 color → có SKU, giá, tồn kho riêng.
 */
@Entity
@Table(name = "Product_Variant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"productId", "sizeId", "colorId"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    @ToString.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sizeId", nullable = false)
    @ToString.Exclude
    private Size size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorId", nullable = false)
    @ToString.Exclude
    private Color color;

    // Mã SKU duy nhất (vd: SKU-AF1-40-W)
    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    // Giá gốc (VND)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Giá khuyến mãi (null nếu không giảm giá)
    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;

    // Số lượng tồn kho
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean isActive = true;
}
