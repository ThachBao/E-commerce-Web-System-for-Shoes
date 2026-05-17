package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.exception.InvalidOperationException;
import com.CongNgheJave.ecommerce_system.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String uploadDir = "uploads/products/";
    private final String urlPrefix = "/uploads/products/";
    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    public FileStorageServiceImpl() {
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ ảnh: " + uploadDir, e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidOperationException("File tải lên trống.");
        }

        // Kiểm tra dung lượng (< 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidOperationException("Kích thước file vượt quá 5MB.");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        
        int lastIndex = originalFileName.lastIndexOf('.');
        if (lastIndex > 0) {
            fileExtension = originalFileName.substring(lastIndex + 1).toLowerCase();
        }

        // Kiểm tra định dạng
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new InvalidOperationException("Chỉ cho phép định dạng ảnh: jpg, jpeg, png, webp.");
        }

        // Sinh tên file mới với UUID
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        try {
            Path targetLocation = Paths.get(uploadDir).resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + newFileName + ". Vui lòng thử lại!", ex);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        return urlPrefix + fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                // Chỉ log ra lỗi, không ném exception làm gián đoạn luồng chính
                System.err.println("Lỗi khi xóa file: " + fileName);
            }
        }
    }
}
