package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.ProductVariantRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ProductVariantResponse;
import com.CongNgheJave.ecommerce_system.entity.Color;
import com.CongNgheJave.ecommerce_system.entity.Product;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.entity.Size;
import com.CongNgheJave.ecommerce_system.exception.DuplicateResourceException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.ColorRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.repository.SizeRepository;
import com.CongNgheJave.ecommerce_system.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ProductVariantResponse> getVariantsByProductId(Integer productId) {
        return variantRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariantResponse> getActiveVariantsByProductId(Integer productId) {
        return variantRepository.findByProductIdAndIsActiveTrue(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductVariantResponse getVariantById(Integer id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể với ID: " + id));
        return mapToResponse(variant);
    }

    @Override
    @Transactional
    public ProductVariantResponse createVariant(ProductVariantRequest request) {
        // Kiểm tra SKU
        if (variantRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU đã tồn tại: " + request.getSku());
        }

        // Validate các ID
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích cỡ với ID: " + request.getSizeId()));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu sắc với ID: " + request.getColorId()));

        // Kiểm tra trùng lặp Size + Color trên cùng Product
        if (variantRepository.findByProductIdAndSizeIdAndColorId(request.getProductId(), request.getSizeId(), request.getColorId()).isPresent()) {
            throw new DuplicateResourceException("Biến thể với kích cỡ và màu sắc này đã tồn tại cho sản phẩm.");
        }

        ProductVariant variant = modelMapper.map(request, ProductVariant.class);
        variant.setProduct(product);
        variant.setSize(size);
        variant.setColor(color);

        variant = variantRepository.save(variant);
        return mapToResponse(variant);
    }

    @Override
    @Transactional
    public ProductVariantResponse updateVariant(Integer id, ProductVariantRequest request) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể với ID: " + id));

        // Kiểm tra SKU mới
        if (!variant.getSku().equals(request.getSku()) && variantRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU đã tồn tại: " + request.getSku());
        }

        // Validate các ID
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích cỡ với ID: " + request.getSizeId()));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu sắc với ID: " + request.getColorId()));

        // Kiểm tra trùng lặp Size + Color nếu đổi size hoặc color
        if (!variant.getSize().getId().equals(request.getSizeId()) || !variant.getColor().getId().equals(request.getColorId()) || !variant.getProduct().getId().equals(request.getProductId())) {
            if (variantRepository.findByProductIdAndSizeIdAndColorId(request.getProductId(), request.getSizeId(), request.getColorId()).isPresent()) {
                throw new DuplicateResourceException("Biến thể với kích cỡ và màu sắc này đã tồn tại cho sản phẩm.");
            }
        }

        variant.setProduct(product);
        variant.setSize(size);
        variant.setColor(color);
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setSalePrice(request.getSalePrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setIsActive(request.getIsActive());

        variant = variantRepository.save(variant);
        return mapToResponse(variant);
    }

    @Override
    @Transactional
    public void deleteVariant(Integer id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể với ID: " + id));
        // Đồ án nhỏ thì có thể xóa vật lý hoặc xóa mềm tùy quyết định. Ở đây tuân theo rules ưu tiên soft delete.
        variant.setIsActive(false);
        variantRepository.save(variant);
    }

    private ProductVariantResponse mapToResponse(ProductVariant variant) {
        ProductVariantResponse response = modelMapper.map(variant, ProductVariantResponse.class);
        response.setProductName(variant.getProduct().getName());
        response.setSizeName(variant.getSize().getName());
        response.setColorName(variant.getColor().getName());
        response.setColorHex(variant.getColor().getHexCode());
        return response;
    }
}
