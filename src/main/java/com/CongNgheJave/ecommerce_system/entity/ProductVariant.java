package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Product_Variant")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku", nullable = false,unique = true)
    private String sku;

    @Column(name="price", nullable = false)
    private BigDecimal price;

    @Column(name = "salePrice")
    private BigDecimal salePrice;

    @Column(name = "stockQuantity")
    private  Integer stockQuantity;

    @Column(name ="isActive")
    private Boolean active;

}
