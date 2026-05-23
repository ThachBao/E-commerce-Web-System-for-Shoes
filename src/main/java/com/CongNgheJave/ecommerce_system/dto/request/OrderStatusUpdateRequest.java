package com.CongNgheJave.ecommerce_system.dto.request;

public class OrderStatusUpdateRequest {

    // Trạng thái mới admin chọn trên form.
    private String newStatus;

    // Ghi chú khi đổi trạng thái.
    private String note;

    public OrderStatusUpdateRequest() {
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}