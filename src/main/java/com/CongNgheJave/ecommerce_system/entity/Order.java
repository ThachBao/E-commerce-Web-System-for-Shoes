package com.CongNgheJave.ecommerce_system.entity;

<<<<<<< HEAD
public class Order {
=======
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "orderCode", nullable = false, unique = true, length = 30)
    private String orderCode;

    @Column(name = "orderStatus", length = 30)
    private String orderStatus = "PENDING";

    @Column(name = "paymentStatus", length = 30)
    private String paymentStatus = "UNPAID";

    @Column(name = "paymentMethod", length = 30)
    private String paymentMethod = "COD";

    @Column(name = "totalAmount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "shippingFullName", nullable = false, length = 100)
    private String shippingFullName;

    @Column(name = "shippingPhone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shippingAddress", nullable = false, length = 255)
    private String shippingAddress;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "placedAt")
    private LocalDateTime placedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;
>>>>>>> origin/member3_cart_payment
}
