package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.SizeRequest;
import com.CongNgheJave.ecommerce_system.dto.response.SizeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SizeService {
    List<SizeResponse> getAllSizes();
    Page<SizeResponse> getSizes(String keyword, Pageable pageable);
    SizeResponse getSizeById(Integer id);
    SizeResponse createSize(SizeRequest request);
    SizeResponse updateSize(Integer id, SizeRequest request);
    void deleteSize(Integer id);
}
