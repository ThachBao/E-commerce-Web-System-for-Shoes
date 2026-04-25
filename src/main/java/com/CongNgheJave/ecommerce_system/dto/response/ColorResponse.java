package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorResponse {
    private Integer id;
    private String name;
    private String hexCode;
}
