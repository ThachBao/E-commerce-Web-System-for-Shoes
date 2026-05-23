package com.CongNgheJave.ecommerce_system.dto.response;

<<<<<<< HEAD
<<<<<<< HEAD
public class CartItemResponse {
=======
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
>>>>>>> origin/member3_cart_payment
=======
public class CartItemResponse {
>>>>>>> origin/member2-product-catalog
}
