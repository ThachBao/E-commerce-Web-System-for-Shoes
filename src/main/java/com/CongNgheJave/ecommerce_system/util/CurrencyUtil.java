package com.CongNgheJave.ecommerce_system.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    /**
     * Định dạng số tiền sang chuẩn VNĐ (vd: 2000000 -> 2.000.000 đ)
     */
    public static String formatVND(BigDecimal amount) {
        if (amount == null) {
            return "0 đ";
        }
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat vn = NumberFormat.getCurrencyInstance(localeVN);
        return vn.format(amount);
    }

    /**
     * Định dạng số tiền từ kiểu long
     */
    public static String formatVND(long amount) {
        return formatVND(BigDecimal.valueOf(amount));
    }
}
