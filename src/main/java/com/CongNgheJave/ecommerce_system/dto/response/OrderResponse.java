package com.CongNgheJave.ecommerce_system.dto.response;

<<<<<<< HEAD
public class OrderResponse {
=======
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderResponse {
    private String orderCode;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String orderStatus;
>>>>>>> origin/member3_cart_payment
}
