package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity thương hiệu giày (Nike, Adidas, Puma...).
 */
@Entity
@Table(name = "Brand")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String logoUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Danh sách sản phẩm thuộc thương hiệu này
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();
}
