package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Integer id;
    private Integer variantId;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemTotal;
}
