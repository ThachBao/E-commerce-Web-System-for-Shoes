package com.CongNgheJave.ecommerce_system.dto.ai;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedProduct {
    private Integer productId;
    private String name;
    private String slug;
    private String code;
}
