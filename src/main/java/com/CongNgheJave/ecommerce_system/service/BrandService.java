package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.BrandRequest;
import com.CongNgheJave.ecommerce_system.dto.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllActiveBrands();
    Page<BrandResponse> getBrands(String keyword, Pageable pageable);
    BrandResponse getBrandById(Integer id);
    BrandResponse getBrandBySlug(String slug);
    BrandResponse createBrand(BrandRequest request, MultipartFile logo);
    BrandResponse updateBrand(Integer id, BrandRequest request, MultipartFile logo);
    void deleteBrand(Integer id);
}
