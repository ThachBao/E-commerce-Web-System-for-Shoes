package com.CongNgheJave.ecommerce_system;

import org.junit.jupiter.api.Test;
import java.sql.*;

/**
 * Script thêm biến thể cho sản phẩm mới.
 */
public class ScratchTest {

    @Test
    public void testImportSampleData() {
        String url = "jdbc:mysql://localhost:3306/shoe_store_db";
        String user = "root";
        String pass = "P@sshuit123";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            // Lấy Size ID
            System.out.println("=== SIZE IDs ===");
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM Size ORDER BY id");
            while (rs.next()) System.out.println("Size ID=" + rs.getInt("id") + " name=" + rs.getString("name"));

            System.out.println("\n=== COLOR IDs ===");
            rs = stmt.executeQuery("SELECT id, name, hexCode FROM Color ORDER BY id");
            while (rs.next()) System.out.println("Color ID=" + rs.getInt("id") + " name=" + rs.getString("name") + " hex=" + rs.getString("hexCode"));

            // Them variant cho Nike Dunk Low (ID=6)
            addVariant(stmt, 6, 3, 1, "SKU-DUNK-40-W", 3200000L, 2800000L, 25);
            addVariant(stmt, 6, 4, 2, "SKU-DUNK-41-B", 3200000L, 2800000L, 30);
            addVariant(stmt, 6, 3, 3, "SKU-DUNK-40-R", 3200000L, null, 15);

            // Adidas Samba (ID=7)
            addVariant(stmt, 7, 3, 1, "SKU-SAMBA-40-W", 2800000L, 2400000L, 40);
            addVariant(stmt, 7, 4, 2, "SKU-SAMBA-41-B", 2800000L, 2400000L, 35);
            addVariant(stmt, 7, 5, 1, "SKU-SAMBA-42-W", 2800000L, null, 20);

            // Puma Suede (ID=8)
            addVariant(stmt, 8, 3, 3, "SKU-SUEDE-40-R", 2200000L, 1900000L, 18);
            addVariant(stmt, 8, 4, 2, "SKU-SUEDE-41-B", 2200000L, null, 22);

            // New Balance 574 (ID=9)
            addVariant(stmt, 9, 3, 1, "SKU-NB574-40-W", 2600000L, 2200000L, 28);
            addVariant(stmt, 9, 5, 2, "SKU-NB574-42-B", 2600000L, null, 15);

            // Converse CT70 (ID=10)
            addVariant(stmt, 10, 1, 2, "SKU-CT70-38-B", 1800000L, 1500000L, 50);
            addVariant(stmt, 10, 2, 1, "SKU-CT70-39-W", 1800000L, null, 45);
            addVariant(stmt, 10, 3, 3, "SKU-CT70-40-R", 1800000L, null, 30);

            // Vans Old Skool (ID=11)
            addVariant(stmt, 11, 3, 2, "SKU-VOS-40-B", 1600000L, 1350000L, 60);
            addVariant(stmt, 11, 4, 1, "SKU-VOS-41-W", 1600000L, null, 40);

            System.out.println("\n=== IMPORT VARIANT HOAN TAT ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVariant(Statement stmt, int productId, int sizeId, int colorId,
                           String sku, long price, Long salePrice, int stock) {
        try {
            String salePriceStr = salePrice != null ? String.valueOf(salePrice) : "NULL";
            stmt.execute("INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) " +
                "VALUES (" + productId + ", " + sizeId + ", " + colorId + ", '" + sku + "', " +
                price + ", " + salePriceStr + ", " + stock + ", TRUE)");
            System.out.println("OK: Variant " + sku);
        } catch (Exception e) {
            System.out.println("SKIP " + sku + ": " + e.getMessage());
        }
    }
}
