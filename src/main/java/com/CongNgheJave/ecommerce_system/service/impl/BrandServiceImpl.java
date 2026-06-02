package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.BrandRequest;
import com.CongNgheJave.ecommerce_system.dto.response.BrandResponse;
import com.CongNgheJave.ecommerce_system.entity.Brand;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.BrandRepository;
import com.CongNgheJave.ecommerce_system.service.BrandService;
import com.CongNgheJave.ecommerce_system.service.FileStorageService;
import com.CongNgheJave.ecommerce_system.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    public List<BrandResponse> getAllActiveBrands() {
        return brandRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BrandResponse> getBrands(String keyword, Pageable pageable) {
        Page<Brand> brandPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            brandPage = brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            brandPage = brandRepository.findAll(pageable);
        }
        return brandPage.map(this::mapToResponse);
    }

    @Override
    public BrandResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));
        return mapToResponse(brand);
    }

    @Override
    public BrandResponse getBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với slug: " + slug));
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request, MultipartFile logo) {
        if (brandRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã thương hiệu đã tồn tại: " + request.getCode());
        }
        if (brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên thương hiệu đã tồn tại: " + request.getName());
        }

        Brand brand = modelMapper.map(request, Brand.class);
        
        // Sinh slug
        String slug = SlugUtil.generateSlug(request.getName());
        if (brandRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        brand.setSlug(slug);

        // Lưu logo nếu có
        if (logo != null && !logo.isEmpty()) {
            String fileName = fileStorageService.storeFile(logo);
            brand.setLogoUrl(fileName);
        }

        brand = brandRepository.save(brand);
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Integer id, BrandRequest request, MultipartFile logo) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        // Kiểm tra mã và tên mới có trùng với thương hiệu KHÁC không
        if (!brand.getCode().equals(request.getCode()) && brandRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã thương hiệu đã tồn tại: " + request.getCode());
        }
        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên thương hiệu đã tồn tại: " + request.getName());
        }

        brand.setCode(request.getCode());
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setIsActive(request.getIsActive());

        // Cập nhật slug nếu đổi tên
        String slug = SlugUtil.generateSlug(request.getName());
        if (!brand.getSlug().equals(slug)) {
            if (brandRepository.existsBySlug(slug)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            brand.setSlug(slug);
        }

        // Cập nhật logo
        if (logo != null && !logo.isEmpty()) {
            // Xóa ảnh cũ
            if (brand.getLogoUrl() != null) {
                fileStorageService.deleteFile(brand.getLogoUrl());
            }
            // Lưu ảnh mới
            String fileName = fileStorageService.storeFile(logo);
            brand.setLogoUrl(fileName);
        }

        brand = brandRepository.save(brand);
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        if (brandRepository.hasProducts(id)) {
            // Soft delete nếu có sản phẩm
            brand.setIsActive(false);
            brandRepository.save(brand);
        } else {
            // Hard delete nếu chưa có sản phẩm
            if (brand.getLogoUrl() != null) {
                fileStorageService.deleteFile(brand.getLogoUrl());
            }
            brandRepository.delete(brand);
        }
    }

    private BrandResponse mapToResponse(Brand brand) {
        BrandResponse response = modelMapper.map(brand, BrandResponse.class);
        // Map lại URL logo
        if (brand.getLogoUrl() != null) {
            response.setLogoUrl(fileStorageService.getFileUrl(brand.getLogoUrl()));
        }
        return response;
    }
}
