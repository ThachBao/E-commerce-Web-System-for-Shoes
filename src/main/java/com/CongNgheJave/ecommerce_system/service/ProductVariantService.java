package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.ProductVariantRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {
    List<ProductVariantResponse> getVariantsByProductId(Integer productId);
    List<ProductVariantResponse> getActiveVariantsByProductId(Integer productId);
    ProductVariantResponse getVariantById(Integer id);
    ProductVariantResponse createVariant(ProductVariantRequest request);
    ProductVariantResponse updateVariant(Integer id, ProductVariantRequest request);
    void deleteVariant(Integer id);
}
