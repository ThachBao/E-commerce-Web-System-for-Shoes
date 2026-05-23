package com.CongNgheJave.ecommerce_system.service.impl;

<<<<<<< HEAD
public interface OrderServiceImpl {
=======
import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.dto.response.OrderResponse;
import com.CongNgheJave.ecommerce_system.entity.*;
import com.CongNgheJave.ecommerce_system.repository.*;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderResponse checkout(Integer userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> allCartItems = cart.getCartItems();
        if (allCartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<Integer> selectedIds = request.getSelectedCartItemIds();
        if (selectedIds == null || selectedIds.isEmpty()) {
            throw new RuntimeException("Chưa chọn sản phẩm nào để thanh toán");
        }

        List<CartItem> cartItems = allCartItems.stream()
                .filter(item -> selectedIds.contains(item.getId()))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm đã chọn trong giỏ hàng");
        }

        // Verify Stock
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            if (variant.getStockQuantity() != null && variant.getStockQuantity() > 0 && variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for SKU: " + variant.getSku());
            }
        }

        // Create Order
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingFullName(request.getShippingFullName());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Create Order Items and Deduct Stock
        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getProductVariant();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setProductName(variant.getSku()); // Fallback to SKU for name
            
            BigDecimal price = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
            orderItem.setUnitPrice(price);
            orderItem.setQuantity(cartItem.getQuantity());
            
            order.getOrderItems().add(orderItem);
            
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            
            // Deduct stock if stock is tracked (not null and > 0)
            if (variant.getStockQuantity() != null && variant.getStockQuantity() > 0) {
                variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        order.setTotalAmount(totalAmount);

        // Create Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setPaymentStatus("PENDING");
        order.setPayment(payment);

        orderRepository.save(order);

        // Clear selected items from Cart
        cartItemRepository.deleteAll(cartItems);
        cart.getCartItems().removeAll(cartItems);

        OrderResponse response = new OrderResponse();
        response.setOrderCode(order.getOrderCode());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderStatus(order.getOrderStatus());

        return response;
    }
>>>>>>> origin/member3_cart_payment
}
