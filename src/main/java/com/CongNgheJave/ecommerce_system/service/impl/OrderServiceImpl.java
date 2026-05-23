package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.dto.request.CheckoutRequest;
import com.CongNgheJave.ecommerce_system.dto.response.OrderResponse;
import com.CongNgheJave.ecommerce_system.entity.*;
import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.*;
import com.CongNgheJave.ecommerce_system.service.InventoryService;
import com.CongNgheJave.ecommerce_system.service.OrderService;
import com.CongNgheJave.ecommerce_system.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AppUserRepository appUserRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            AppUserRepository appUserRepository,
                            OrderStatusHistoryRepository historyRepository,
                            PaymentRepository paymentRepository,
                            CartRepository cartRepository,
                            InventoryService inventoryService,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.appUserRepository = appUserRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    // Checkout - tạo đơn hàng và trả về OrderResponse (dùng bởi OrderController).
    @Override
    @Transactional
    public OrderResponse checkout(Integer userId, CheckoutRequest request) {
        Order order = placeOrder(userId, request);
        OrderResponse response = new OrderResponse();
        response.setOrderCode(order.getOrderCode());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderStatus(order.getOrderStatus());
        return response;
    }

    // Admin xem danh sách đơn hàng và lọc theo trạng thái.
    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAdminOrders(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAdminOrders(status, pageable);
    }

    // Admin xem chi tiết đơn hàng.
    @Override
    @Transactional(readOnly = true)
    public Order getOrderDetail(Integer orderId) {
        return orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id = " + orderId));
    }

    // Customer xem lịch sử đơn hàng.
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Integer userId) {
        return orderRepository.findByUser_IdOrderByPlacedAtDesc(userId);
    }

    // Customer xem chi tiết đơn của chính mình.
    @Override
    @Transactional(readOnly = true)
    public Order getCustomerOrderDetail(Integer orderId, Integer customerId) {
        Order order = getOrderDetail(orderId);

        if (order.getUser() == null || !order.getUser().getId().equals(customerId)) {
            throw new InvalidOperationException("Bạn không có quyền xem đơn hàng này");
        }

        return order;
    }

    // Admin cập nhật trạng thái: PENDING -> CONFIRMED -> PROCESSING -> SHIPPING -> COMPLETED.
    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, String newStatus, Integer changedByUserId, String note) {
        Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

        String oldStatus = order.getOrderStatus();

        validateStatusChange(oldStatus, newStatus);

        AppUser changedBy = appUserRepository.findById(changedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id = " + changedByUserId));

        order.setOrderStatus(newStatus);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        // Include changedBy info in the note if needed, or just keep original note
        history.setNote(note != null ? note : "Changed by user " + changedByUserId);
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
        orderRepository.save(order);
    }

    // Kiểm tra luồng đổi trạng thái hợp lệ.
    private void validateStatusChange(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null || newStatus.isBlank()) {
            throw new InvalidOperationException("Trạng thái đơn hàng không hợp lệ");
        }

        if (oldStatus.equals(newStatus)) {
            throw new InvalidOperationException("Trạng thái mới đang trùng trạng thái hiện tại");
        }

        boolean valid = switch (oldStatus) {
            case "PENDING" -> newStatus.equals("CONFIRMED");
            case "CONFIRMED" -> newStatus.equals("PROCESSING");
            case "PROCESSING" -> newStatus.equals("SHIPPING");
            case "SHIPPING" -> newStatus.equals("COMPLETED");
            default -> false;
        };

        if (!valid) {
            throw new InvalidOperationException("Không được đổi trạng thái từ " + oldStatus + " sang " + newStatus);
        }
    }

    // Admin xem lịch sử đổi trạng thái.
    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getStatusHistory(Integer orderId) {
        return historyRepository.findByOrder_IdOrderByCreatedAtDesc(orderId);
    }

    // Hiển thị thông tin thanh toán.
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByOrderId(Integer orderId) {
        return paymentRepository.findByOrder_Id(orderId);
    }

    // Tạo đơn hàng từ giỏ hàng.
    @Override
    @Transactional
    public Order placeOrder(Integer userId, CheckoutRequest request) {
        // Lấy user
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id = " + userId));

        // Lấy giỏ hàng
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        // Validate giỏ hàng không rỗng
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidOperationException("Giỏ hàng trống, không thể đặt hàng");
        }

        // Validate stock cho tất cả items
        for (CartItem cartItem : cart.getItems()) {
            inventoryService.validateStock(cartItem.getVariant(), cartItem.getQuantity());
        }

        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(generateOrderCode());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());

        // Shipping info
        order.setShippingFullName(request.getShippingFullName());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddressLine());
        order.setNote(request.getNote());

        // Calculate totals
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            BigDecimal price = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(lineTotal);
        }

        order.setTotalAmount(subTotal);
        order.setPlacedAt(LocalDateTime.now());

        // Save order first
        order = orderRepository.save(order);

        // Create order items with snapshot
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            Product product = variant.getProduct();
            Size size = variant.getSize();
            Color color = variant.getColor();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);

            // Snapshot product name with color and size
            String colorName = (color != null && color.getName() != null) ? color.getName() : "Unknown";
            String sizeName = (size != null && size.getName() != null) ? size.getName() : "Unknown";

            String productName;
            if (product != null && product.getName() != null) {
                productName = product.getName() + " - " + colorName + " - " + sizeName;
            } else {
                productName = "Product - " + colorName + " - " + sizeName;
            }
            orderItem.setProductName(productName);

            BigDecimal price = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
            orderItem.setUnitPrice(price);
            orderItem.setQuantity(cartItem.getQuantity());

            order.getItems().add(orderItem);

            // Deduct stock
            inventoryService.deductStock(variant, cartItem.getQuantity());
        }

        // Save order with items
        order = orderRepository.save(order);

        // Create payment
        paymentService.createPayment(order);

        // Create order status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("PENDING");
        history.setNote("Đơn hàng được tạo");
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    // Customer cancel order.
    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer customerId) {
        Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

        // Validate ownership
        if (order.getUser() == null || !order.getUser().getId().equals(customerId)) {
            throw new InvalidOperationException("Bạn không có quyền hủy đơn hàng này");
        }

        // Validate status - customer can only cancel PENDING or CONFIRMED
        String currentStatus = order.getOrderStatus();
        if (!currentStatus.equals("PENDING") && !currentStatus.equals("CONFIRMED")) {
            throw new InvalidOperationException("Không thể hủy đơn hàng ở trạng thái " + currentStatus);
        }

        // Update order status
        String oldStatus = order.getOrderStatus();
        order.setOrderStatus("CANCELLED");
        order.setPaymentStatus("CANCELLED");

        // Restore stock
        for (OrderItem item : order.getItems()) {
            inventoryService.restoreStock(item.getVariant(), item.getQuantity());
        }

        // Create history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("CANCELLED");
        history.setNote("Khách hàng hủy đơn");
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
        orderRepository.save(order);
    }

    // Admin cancel order.
    @Override
    @Transactional
    public void adminCancelOrder(Integer orderId, Integer adminId, String note) {
        Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

        AppUser admin = appUserRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id = " + adminId));

        // Validate status - cannot cancel if already completed or cancelled
        String currentStatus = order.getOrderStatus();
        if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
            throw new InvalidOperationException("Không thể hủy đơn hàng ở trạng thái " + currentStatus);
        }

        // Update order status
        String oldStatus = order.getOrderStatus();
        order.setOrderStatus("CANCELLED");
        order.setPaymentStatus("CANCELLED");

        // Restore stock
        for (OrderItem item : order.getItems()) {
            inventoryService.restoreStock(item.getVariant(), item.getQuantity());
        }

        // Create history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("CANCELLED");
        history.setNote(note != null ? note : "Admin hủy đơn");
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
        orderRepository.save(order);
    }

    // Admin complete order.
    @Override
    @Transactional
    public void completeOrder(Integer orderId, Integer adminId, String note) {
        Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

        AppUser admin = appUserRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id = " + adminId));

        // Validate status - can only complete from SHIPPING
        String currentStatus = order.getOrderStatus();
        if (!currentStatus.equals("SHIPPING")) {
            throw new InvalidOperationException("Chỉ có thể hoàn thành đơn hàng ở trạng thái SHIPPING");
        }

        // Update order status
        String oldStatus = order.getOrderStatus();
        order.setOrderStatus("COMPLETED");

        // Update payment status for COD
        if ("COD".equals(order.getPaymentMethod())) {
            order.setPaymentStatus("PAID");
            Payment payment = paymentRepository.findByOrder_Id(orderId).orElse(null);
            if (payment != null) {
                payment.setPaymentStatus("PAID");
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }
        }

        // Create history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("COMPLETED");
        history.setNote(note != null ? note : "Đơn hàng hoàn thành");
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
        orderRepository.save(order);
    }

    // Generate order code: ORD + timestamp
    private String generateOrderCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + timestamp;
    }
}
