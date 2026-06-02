package com.CongNgheJave.ecommerce_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String code;
    private String name;
    private String slug;
    private String description;
    private String gender;
    private Boolean isFeatured;
    private Boolean isActive;
    
    private String thumbnailUrl;
    private List<String> imageUrls;
}
