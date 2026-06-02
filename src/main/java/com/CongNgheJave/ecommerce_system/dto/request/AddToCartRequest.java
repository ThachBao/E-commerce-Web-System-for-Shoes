package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull(message = "Thiếu ID biến thể sản phẩm (variantId)")
    private Integer variantId;

    @NotNull(message = "Thiếu số lượng")
    private Integer quantity;
}
