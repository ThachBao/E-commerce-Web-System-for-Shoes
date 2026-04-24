package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Cart_Item")
public class CartItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cartId", nullable = false)
    private Integer cartId;

    @Column(name = "variantId", nullable = false)
    private Integer variantId;

    @Column(name="quantity",nullable = false)
    private Integer  quantity;

    @Column(name = "unitPrice", nullable = false)
    private BigDecimal unitPrice;
}
