package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryId;

    private Integer brandId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm không được vượt quá 150 ký tự")
    private String name;

    private String description;
    private String gender = "UNISEX";
    private Boolean isFeatured = false;
    private Boolean isActive = true;
}
