package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailResponse {
    private String orderCode;
    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddress;
    private LocalDateTime placedAt;
}
