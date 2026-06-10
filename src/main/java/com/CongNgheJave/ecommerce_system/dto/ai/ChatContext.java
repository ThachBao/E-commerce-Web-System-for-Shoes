package com.CongNgheJave.ecommerce_system.dto.ai;
 
import lombok.Getter;
import lombok.Setter;
import java.util.List;
 
@Getter
@Setter
public class ChatContext {
    private List<SuggestedProduct> lastSuggestedProducts;
    private SuggestedProduct currentProduct;
}
