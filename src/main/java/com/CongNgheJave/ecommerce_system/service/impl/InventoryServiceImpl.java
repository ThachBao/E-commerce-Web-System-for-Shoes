package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.entity.InventoryTransaction;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.exception.InsufficientStockException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.InventoryTransactionRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import com.CongNgheJave.ecommerce_system.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductVariantRepository variantRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryServiceImpl(ProductVariantRepository variantRepository,
                                InventoryTransactionRepository transactionRepository) {
        this.variantRepository = variantRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void validateStock(ProductVariant variant, Integer quantity) {
        if (variant.getStockQuantity() == null || variant.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Không đủ hàng trong kho cho sản phẩm SKU: " + variant.getSku()
            );
        }
    }

    @Override
    @Transactional
    public void deductStock(ProductVariant variant, Integer quantity) {
        validateStock(variant, quantity);

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        variantRepository.save(variant);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setVariant(variant);
        transaction.setQuantityChange(-quantity);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void restoreStock(ProductVariant variant, Integer quantity) {
        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        variantRepository.save(variant);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setVariant(variant);
        transaction.setQuantityChange(quantity);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public ProductVariant lockVariant(Integer variantId) {
        return variantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm id = " + variantId));
    }
}
