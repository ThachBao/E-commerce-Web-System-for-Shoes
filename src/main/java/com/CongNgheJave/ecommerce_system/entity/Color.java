package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Color")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "hexCode", length = 20)
    private String hexCode;

    @OneToMany(mappedBy = "color", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ProductVariant> productVariants = new ArrayList<>();
}
