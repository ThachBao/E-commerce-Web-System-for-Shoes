package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.CategoryRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllActiveCategories();
    List<CategoryResponse> getActiveRootCategories();
    List<CategoryResponse> getActiveSubCategories(Integer parentId);
    Page<CategoryResponse> getCategories(String keyword, Pageable pageable);
    CategoryResponse getCategoryById(Integer id);
    CategoryResponse getCategoryBySlug(String slug);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Integer id, CategoryRequest request);
    void deleteCategory(Integer id);
}
