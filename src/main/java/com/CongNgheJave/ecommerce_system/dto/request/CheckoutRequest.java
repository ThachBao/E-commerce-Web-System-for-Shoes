package com.CongNgheJave.ecommerce_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

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

    private List<Integer> selectedCartItemIds;

    public CheckoutRequest() {
    }

    public String getShippingFullName() {
        return shippingFullName;
    }

    public void setShippingFullName(String shippingFullName) {
        this.shippingFullName = shippingFullName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddressLine() {
        return shippingAddressLine;
    }

    public void setShippingAddressLine(String shippingAddressLine) {
        this.shippingAddressLine = shippingAddressLine;
    }

    public String getShippingWard() {
        return shippingWard;
    }

    public void setShippingWard(String shippingWard) {
        this.shippingWard = shippingWard;
    }

    public String getShippingDistrict() {
        return shippingDistrict;
    }

    public void setShippingDistrict(String shippingDistrict) {
        this.shippingDistrict = shippingDistrict;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Integer> getSelectedCartItemIds() {
        return selectedCartItemIds;
    }

    public void setSelectedCartItemIds(List<Integer> selectedCartItemIds) {
        this.selectedCartItemIds = selectedCartItemIds;
    }
}
