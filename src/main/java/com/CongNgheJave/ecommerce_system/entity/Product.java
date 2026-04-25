package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity sản phẩm giày.
 * Liên kết với Category, Brand, ProductImage và ProductVariant.
 */
@Entity
@Table(name = "Product")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Danh mục sản phẩm (bắt buộc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    @ToString.Exclude
    private Category category;

    // Thương hiệu (có thể null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandId")
    @ToString.Exclude
    private Brand brand;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Giới tính: UNISEX, MEN, WOMEN
    @Column(length = 20)
    private String gender = "UNISEX";

    // Sản phẩm nổi bật (hiển thị trang chủ)
    @Column(nullable = false)
    private Boolean isFeatured = false;

    // Trạng thái hoạt động (soft delete)
    @Column(nullable = false)
    private Boolean isActive = true;

    // Danh sách hình ảnh (xóa ảnh khi xóa sản phẩm)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductImage> images = new ArrayList<>();

    // Danh sách biến thể: size + color + giá + tồn kho
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductVariant> variants = new ArrayList<>();
}
