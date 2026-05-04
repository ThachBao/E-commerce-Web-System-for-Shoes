package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity danh mục sản phẩm.
 * Hỗ trợ phân cấp cha-con qua parentId (vd: Sneaker → Air Jordan).
 */
@Entity
@Table(name = "Category")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Danh mục cha (null nếu là danh mục gốc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @ToString.Exclude
    private Category parent;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Danh sách danh mục con
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Category> children = new ArrayList<>();

    // Danh sách sản phẩm thuộc danh mục
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();
}
