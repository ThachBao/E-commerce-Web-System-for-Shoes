package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private Integer id;
    private String code;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private Boolean isActive;
}
