package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderResponse {
    private String orderCode;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String orderStatus;
}
