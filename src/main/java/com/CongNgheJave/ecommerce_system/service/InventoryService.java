package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.entity.ProductVariant;

public interface InventoryService {

    /**
     * Validate stock availability for a variant.
     * Throws InsufficientStockException if not enough stock.
     */
    void validateStock(ProductVariant variant, Integer quantity);

    /**
     * Deduct stock from a variant and create inventory transaction.
     */
    void deductStock(ProductVariant variant, Integer quantity);

    /**
     * Restore stock to a variant and create inventory transaction.
     */
    void restoreStock(ProductVariant variant, Integer quantity);

    /**
     * Lock the variant in the database for pessimistic write update.
     */
    ProductVariant lockVariant(Integer variantId);
}
