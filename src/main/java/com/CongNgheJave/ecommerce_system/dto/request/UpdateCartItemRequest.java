package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "Thiếu số lượng mới")
    private Integer quantity;
}
