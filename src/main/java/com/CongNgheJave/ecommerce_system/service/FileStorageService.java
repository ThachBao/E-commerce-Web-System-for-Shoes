package com.CongNgheJave.ecommerce_system.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Lưu trữ một file ảnh tải lên.
     * @param file File ảnh từ request
     * @return Tên file sau khi lưu (đã tự động thêm UUID để chống trùng lặp)
     */
    String storeFile(MultipartFile file);

    /**
     * Lấy URL đầy đủ của file để trả về cho Frontend hiển thị.
     * @param fileName Tên file
     * @return URL đầy đủ
     */
    String getFileUrl(String fileName);

    /**
     * Xóa một file đã lưu.
     * @param fileName Tên file cần xóa
     */
    void deleteFile(String fileName);
}
