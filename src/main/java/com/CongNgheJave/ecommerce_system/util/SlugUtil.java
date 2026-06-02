package com.CongNgheJave.ecommerce_system.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Tạo slug từ một chuỗi (vd: "Giày Thể Thao" -> "giay-the-thao")
     */
    public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Loại bỏ dấu tiếng Việt và chuyển sang chữ thường
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        // Thay chữ đ/Đ
        slug = slug.replace("đ", "d").replace("Đ", "D");

        slug = NONLATIN.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
