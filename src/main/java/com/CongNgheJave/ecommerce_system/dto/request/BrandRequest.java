package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {
    @NotBlank(message = "Mã thương hiệu không được để trống")
    @Size(max = 20, message = "Mã thương hiệu không được vượt quá 20 ký tự")
    private String code;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 100, message = "Tên thương hiệu không được vượt quá 100 ký tự")
    private String name;

    private String description;
    
    private Boolean isActive = true;
}
