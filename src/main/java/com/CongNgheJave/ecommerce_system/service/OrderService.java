package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse checkout(Integer userId, CheckoutRequest request);
}
