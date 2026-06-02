package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.ColorRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ColorResponse;
import com.CongNgheJave.ecommerce_system.entity.Color;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.ColorRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.service.ColorService;
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
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final ProductVariantRepository variantRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ColorResponse> getAllColors() {
        return colorRepository.findAll().stream()
                .map(color -> modelMapper.map(color, ColorResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ColorResponse> getColors(String keyword, Pageable pageable) {
        Page<Color> colorPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            colorPage = colorRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            colorPage = colorRepository.findAll(pageable);
        }
        return colorPage.map(color -> modelMapper.map(color, ColorResponse.class));
    }

    @Override
    public ColorResponse getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu sắc với ID: " + id));
        return modelMapper.map(color, ColorResponse.class);
    }

    @Override
    @Transactional
    public ColorResponse createColor(ColorRequest request) {
        if (colorRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên màu sắc đã tồn tại: " + request.getName());
        }
        Color color = modelMapper.map(request, Color.class);
        color = colorRepository.save(color);
        return modelMapper.map(color, ColorResponse.class);
    }

    @Override
    @Transactional
    public ColorResponse updateColor(Integer id, ColorRequest request) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu sắc với ID: " + id));

        if (!color.getName().equals(request.getName()) && colorRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tên màu sắc đã tồn tại: " + request.getName());
        }

        color.setName(request.getName());
        color.setHexCode(request.getHexCode());
        color = colorRepository.save(color);
        return modelMapper.map(color, ColorResponse.class);
    }

    @Override
    @Transactional
    public void deleteColor(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu sắc với ID: " + id));
        // Xóa tất cả biến thể liên quan trước khi xóa màu
        variantRepository.deleteByColorId(id);
        colorRepository.delete(color);
    }
}
