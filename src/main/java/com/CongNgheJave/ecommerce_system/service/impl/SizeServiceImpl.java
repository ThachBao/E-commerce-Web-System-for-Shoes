package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.SizeRequest;
import com.CongNgheJave.ecommerce_system.dto.response.SizeResponse;
import com.CongNgheJave.ecommerce_system.entity.Size;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.repository.SizeRepository;
import com.CongNgheJave.ecommerce_system.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;
    private final ProductVariantRepository variantRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<SizeResponse> getAllSizes() {
        return sizeRepository.findAll().stream()
                .map(size -> modelMapper.map(size, SizeResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<SizeResponse> getSizes(String keyword, Pageable pageable) {
        Page<Size> sizePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            sizePage = sizeRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            sizePage = sizeRepository.findAll(pageable);
        }
        return sizePage.map(size -> modelMapper.map(size, SizeResponse.class));
    }

    @Override
    public SizeResponse getSizeById(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích cỡ với ID: " + id));
        return modelMapper.map(size, SizeResponse.class);
    }

    @Override
    @Transactional
    public SizeResponse createSize(SizeRequest request) {
        if (sizeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên kích cỡ đã tồn tại: " + request.getName());
        }
        Size size = modelMapper.map(request, Size.class);
        size = sizeRepository.save(size);
        return modelMapper.map(size, SizeResponse.class);
    }

    @Override
    @Transactional
    public SizeResponse updateSize(Integer id, SizeRequest request) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích cỡ với ID: " + id));

        if (!size.getName().equals(request.getName()) && sizeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên kích cỡ đã tồn tại: " + request.getName());
        }

        size.setName(request.getName());
        size = sizeRepository.save(size);
        return modelMapper.map(size, SizeResponse.class);
    }

    @Override
    @Transactional
    public void deleteSize(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích cỡ với ID: " + id));
        // Xóa tất cả biến thể liên quan trước khi xóa kích cỡ
        variantRepository.deleteBySizeId(id);
        sizeRepository.delete(size);
    }
}
