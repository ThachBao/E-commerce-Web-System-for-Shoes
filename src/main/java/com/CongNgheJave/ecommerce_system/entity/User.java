package com.CongNgheJave.ecommerce_system.entity;

<<<<<<< HEAD
<<<<<<< HEAD
public class User {
=======
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
>>>>>>> origin/member3_cart_payment
=======
public class User {
>>>>>>> origin/member2-product-catalog
}
