package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer sizeId;
    private String sizeName;
    private Integer colorId;
    private String colorName;
    private String colorHex;
    private String sku;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private Boolean isActive;
}
