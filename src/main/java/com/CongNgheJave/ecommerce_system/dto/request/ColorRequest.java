package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorRequest {
    @NotBlank(message = "Tên màu sắc không được để trống")
    private String name;

    private String hexCode;
}
