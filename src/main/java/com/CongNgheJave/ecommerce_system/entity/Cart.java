package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="userId",nullable = false,unique = true)
    private  Integer userId;

    @Column(name="status")
    private String status;

}
