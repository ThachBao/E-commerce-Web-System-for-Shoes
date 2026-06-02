package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.ProductCreateRequest;
import com.CongNgheJave.ecommerce_system.dto.request.ProductUpdateRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> searchProducts(String keyword, Integer categoryId, Integer brandId, String gender, Pageable pageable);
    Page<ProductResponse> getActiveProducts(Pageable pageable);
    List<ProductResponse> getFeaturedProducts();
    ProductResponse getProductById(Integer id);
    ProductResponse getProductBySlug(String slug);
    ProductResponse createProduct(ProductCreateRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    ProductResponse updateProduct(Integer id, ProductUpdateRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    void deleteProduct(Integer id);
    void deleteProductImage(Integer imageId);
}
