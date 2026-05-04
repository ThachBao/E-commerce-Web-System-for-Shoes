package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity kích cỡ giày (vd: 38, 39, 40, 41, 42).
 */
@Entity
@Table(name = "Size")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @OneToMany(mappedBy = "size", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ProductVariant> productVariants = new ArrayList<>();
}
