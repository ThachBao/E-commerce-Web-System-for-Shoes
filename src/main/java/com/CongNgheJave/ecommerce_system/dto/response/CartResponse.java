package com.CongNgheJave.ecommerce_system.dto.response;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class CartResponse {
    private Integer id;
    private List<CartItemResponse> items;
    private BigDecimal subTotal;
}
