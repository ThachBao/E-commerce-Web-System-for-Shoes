package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity màu sắc sản phẩm (vd: Trắng #FFFFFF, Đen #000000).
 */
@Entity
@Table(name = "Color")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Mã màu HEX (vd: #FFFFFF)
    @Column(length = 20)
    private String hexCode;

    @OneToMany(mappedBy = "color", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ProductVariant> productVariants = new ArrayList<>();
}
