package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String shippingFullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String shippingPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddressLine;

    private String shippingWard;

    private String shippingDistrict;

    private String shippingCity;

    private String shippingCountry;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private String note;

    public String getShippingAddress() {
        return shippingAddressLine;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddressLine = shippingAddress;
    }
}


