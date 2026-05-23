package com.CongNgheJave.ecommerce_system.dto.request;

<<<<<<< HEAD
public class CheckoutRequest {
=======
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Thiếu phương thức thanh toán")
    private String paymentMethod;
    
    @NotBlank(message = "Thiếu tên người nhận")
    private String shippingFullName;
    
    @NotBlank(message = "Thiếu số điện thoại")
    private String shippingPhone;
    
    @NotBlank(message = "Thiếu địa chỉ giao hàng")
    private String shippingAddress;
    
    private String note;
    
    private java.util.List<Integer> selectedCartItemIds;
>>>>>>> origin/member3_cart_payment
}
