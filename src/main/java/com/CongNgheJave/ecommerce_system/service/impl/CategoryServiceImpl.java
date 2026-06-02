package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.CategoryRequest;
import com.CongNgheJave.ecommerce_system.dto.response.CategoryResponse;
import com.CongNgheJave.ecommerce_system.entity.Category;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.CategoryRepository;
import com.CongNgheJave.ecommerce_system.service.CategoryService;
import com.CongNgheJave.ecommerce_system.util.SlugUtil;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getActiveRootCategories() {
        return categoryRepository.findByParentIsNullAndIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getActiveSubCategories(Integer parentId) {
        return categoryRepository.findByParentIdAndIsActiveTrue(parentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CategoryResponse> getCategories(String keyword, Pageable pageable) {
        Page<Category> categoryPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        return categoryPage.map(this::mapToResponse);
    }

    @Override
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
        return mapToResponse(category);
    }

    @Override
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với slug: " + slug));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã danh mục đã tồn tại: " + request.getCode());
        }

        Category category = modelMapper.map(request, Category.class);

        // Sinh slug
        String slug = SlugUtil.generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        category.setSlug(slug);

        // Xử lý danh mục cha
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId()));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra mã mới có trùng với danh mục KHÁC không
        if (!category.getCode().equals(request.getCode()) && categoryRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã danh mục đã tồn tại: " + request.getCode());
        }

        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setIsActive(request.getIsActive());

        // Cập nhật slug nếu đổi tên
        String slug = SlugUtil.generateSlug(request.getName());
        if (!category.getSlug().equals(slug)) {
            if (categoryRepository.existsBySlug(slug)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            category.setSlug(slug);
        }

        // Cập nhật danh mục cha
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new InvalidOperationException("Danh mục cha không thể là chính nó.");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // Gỡ liên kết danh mục con (set parent = null) thay vì chặn xóa
        List<Category> children = categoryRepository.findByParentId(id);
        for (Category child : children) {
            child.setParent(null);
            categoryRepository.save(child);
        }

        // Hard delete luôn
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentName(category.getParent().getName());
        }
        return response;
    }
}
