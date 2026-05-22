package com.CongNgheJave.ecommerce_system.entity;

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
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private AppUser user;

    @Column(name = "orderCode", nullable = false, unique = true, length = 30)
    private String orderCode;

    @Column(name = "orderStatus", length = 30)
    private String orderStatus = "PENDING";

    @Column(name = "paymentStatus", length = 30)
    private String paymentStatus = "PENDING";

    @Column(name = "paymentMethod", length = 30)
    private String paymentMethod = "COD";

    @Column(name = "subTotal", precision = 12, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "shippingFee", precision = 12, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "discountAmount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "totalAmount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "shippingFullName", nullable = false, length = 100)
    private String shippingFullName;

    @Column(name = "shippingPhone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shippingAddressLine", length = 255)
    private String shippingAddressLine;

    @Column(name = "shippingWard", length = 100)
    private String shippingWard;

    @Column(name = "shippingDistrict", length = 100)
    private String shippingDistrict;

    @Column(name = "shippingCity", length = 100)
    private String shippingCity;

    @Column(name = "shippingCountry", length = 100)
    private String shippingCountry;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "placedAt")
    private LocalDateTime placedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public String getShippingAddress() {
        return shippingAddressLine;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddressLine = shippingAddress;
    }

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.items = orderItems;
    }
}
