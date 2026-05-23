package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.ProductCreateRequest;
import com.CongNgheJave.ecommerce_system.dto.request.ProductUpdateRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductResponse;
import com.CongNgheJave.ecommerce_system.entity.Brand;
import com.CongNgheJave.ecommerce_system.entity.Category;
import com.CongNgheJave.ecommerce_system.entity.Product;
import com.CongNgheJave.ecommerce_system.entity.ProductImage;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.BrandRepository;
import com.CongNgheJave.ecommerce_system.repository.CategoryRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductImageRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductRepository;
import com.CongNgheJave.ecommerce_system.service.FileStorageService;
import com.CongNgheJave.ecommerce_system.service.ProductService;
import com.CongNgheJave.ecommerce_system.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Integer categoryId, Integer brandId, String gender, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword : null,
                categoryId,
                brandId,
                (gender != null && !gender.trim().isEmpty()) ? gender : null,
                pageable
        );
        return products.map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable).map(this::mapToResponse);
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với slug: " + slug));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã sản phẩm đã tồn tại: " + request.getCode());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + request.getBrandId()));
        }

        Product product = modelMapper.map(request, Product.class);
        product.setCategory(category);
        product.setBrand(brand);

        String slug = SlugUtil.generateSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        product.setSlug(slug);

        product = productRepository.save(product);

        // Xử lý ảnh thumbnail
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String fileName = fileStorageService.storeFile(thumbnail);
            ProductImage pImage = new ProductImage();
            pImage.setProduct(product);
            pImage.setImageUrl(fileName);
            pImage.setIsThumbnail(true);
            productImageRepository.save(pImage);
        }

        // Xử lý ảnh chi tiết
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);
                    ProductImage pImage = new ProductImage();
                    pImage.setProduct(product);
                    pImage.setImageUrl(fileName);
                    pImage.setIsThumbnail(false);
                    productImageRepository.save(pImage);
                }
            }
        }

        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + request.getBrandId()));
        }

        product.setCategory(category);
        product.setBrand(brand);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setGender(request.getGender());
        product.setIsFeatured(request.getIsFeatured());
        product.setIsActive(request.getIsActive());

        String slug = SlugUtil.generateSlug(request.getName());
        if (!product.getSlug().equals(slug)) {
            if (productRepository.existsBySlug(slug)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            product.setSlug(slug);
        }

        final Product savedProduct = productRepository.save(product);

        // Cập nhật thumbnail mới (sẽ thay thế cái cũ)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String fileName = fileStorageService.storeFile(thumbnail);
            productImageRepository.findByProductIdAndIsThumbnailTrue(id).ifPresentOrElse(
                oldThumb -> {
                    fileStorageService.deleteFile(oldThumb.getImageUrl());
                    oldThumb.setImageUrl(fileName);
                    productImageRepository.save(oldThumb);
                },
                () -> {
                    ProductImage pImage = new ProductImage();
                    pImage.setProduct(savedProduct);
                    pImage.setImageUrl(fileName);
                    pImage.setIsThumbnail(true);
                    productImageRepository.save(pImage);
                }
            );
        }

        // Thêm ảnh chi tiết mới
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);
                    ProductImage pImage = new ProductImage();
                    pImage.setProduct(savedProduct);
                    pImage.setImageUrl(fileName);
                    pImage.setIsThumbnail(false);
                    productImageRepository.save(pImage);
                }
            }
        }

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        // Đồ án này ưu tiên Soft Delete
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProductImage(Integer imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh với ID: " + imageId));
        fileStorageService.deleteFile(image.getImageUrl());
        productImageRepository.delete(image);
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setCategoryName(product.getCategory().getName());
        if (product.getBrand() != null) {
            response.setBrandName(product.getBrand().getName());
        }

        // Map danh sách hình ảnh
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage img : images) {
            String fullUrl = fileStorageService.getFileUrl(img.getImageUrl());
            if (Boolean.TRUE.equals(img.getIsThumbnail())) {
                response.setThumbnailUrl(fullUrl);
            } else {
                imageUrls.add(fullUrl);
            }
        }
        response.setImageUrls(imageUrls);

        return response;
    }
}
