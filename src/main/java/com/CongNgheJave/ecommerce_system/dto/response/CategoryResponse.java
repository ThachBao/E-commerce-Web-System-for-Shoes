package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Integer id;
    private Integer parentId;
    private String parentName;
    private String code;
    private String name;
    private String slug;
    private Boolean isActive;
}
