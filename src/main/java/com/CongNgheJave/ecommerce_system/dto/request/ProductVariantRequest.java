package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {
    @NotNull(message = "Sản phẩm không được để trống")
    private Integer productId;

    @NotNull(message = "Kích cỡ không được để trống")
    private Integer sizeId;

    @NotNull(message = "Màu sắc không được để trống")
    private Integer colorId;

    private String sku;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Giá khuyến mãi không được âm")
    private BigDecimal salePrice;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity = 0;

    private Boolean isActive = true;
}
