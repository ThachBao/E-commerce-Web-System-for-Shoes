package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.ColorRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ColorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColorService {
    List<ColorResponse> getAllColors();
    Page<ColorResponse> getColors(String keyword, Pageable pageable);
    ColorResponse getColorById(Integer id);
    ColorResponse createColor(ColorRequest request);
    ColorResponse updateColor(Integer id, ColorRequest request);
    void deleteColor(Integer id);
}
