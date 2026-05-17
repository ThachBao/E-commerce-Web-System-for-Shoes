-- ==========================================
-- DỮ LIỆU MẪU BỔ SUNG CHO SHOE STORE
-- Chạy script này trong MySQL Workbench hoặc CLI
-- ==========================================

-- 1. Thêm thêm Thương hiệu
INSERT IGNORE INTO Brand (code, name, slug, description, isActive) VALUES
('NB',      'New Balance',  'new-balance',  'Thương hiệu giày chạy bộ nổi tiếng từ Mỹ', TRUE),
('CONVERSE', 'Converse',    'converse',     'Thương hiệu giày vải cổ điển',              TRUE),
('VANS',     'Vans',        'vans',         'Thương hiệu giày trượt ván',                TRUE);

-- 2. Thêm thêm Danh mục
INSERT IGNORE INTO Category (parentId, code, name, slug, isActive) VALUES
(NULL, 'SANDAL',   'Dép & Sandal',    'dep-sandal',      TRUE),
(NULL, 'CASUAL',   'Giày Casual',     'giay-casual',     TRUE),
(1,    'AIRMAX',   'Nike Air Max',    'nike-air-max',    TRUE),
(1,    'DUNK',     'Nike Dunk',       'nike-dunk',       TRUE);

-- 3. Thêm thêm Màu sắc
INSERT IGNORE INTO Color (name, hexCode) VALUES
('Xanh Navy',  '#1B2A4A'),
('Xám',        '#808080'),
('Hồng',       '#FF69B4'),
('Xanh Lá',    '#2ECC71'),
('Nâu',        '#8B4513');

-- 4. Thêm thêm Kích cỡ
INSERT IGNORE INTO Size (name) VALUES ('36'), ('37'), ('43'), ('44');

-- 5. Thêm Sản phẩm mới
INSERT INTO Product (categoryId, brandId, code, name, slug, description, gender, isFeatured, isActive) VALUES
(1, 1, 'NIKE-DUNK-LOW', 'Nike Dunk Low Retro', 'nike-dunk-low-retro',
 'Phiên bản tái hiện kinh điển Nike Dunk Low với phối màu retro đầy phong cách.', 'UNISEX', TRUE, TRUE),

(2, 2, 'ADI-SAMBA', 'Adidas Samba OG', 'adidas-samba-og',
 'Đôi giày huyền thoại từ sân bóng đá, nay trở thành biểu tượng thời trang đường phố.', 'UNISEX', TRUE, TRUE),

(1, 3, 'PUMA-SUEDE', 'Puma Suede Classic XXI', 'puma-suede-classic-xxi',
 'Giày sneaker da lộn mang phong cách hoài cổ nhưng không bao giờ lỗi mốt.', 'UNISEX', FALSE, TRUE),

(2, NULL, 'NB-574', 'New Balance 574', 'new-balance-574',
 'Mẫu giày chạy bộ cổ điển nhất của New Balance, phù hợp mọi hoạt động.', 'UNISEX', TRUE, TRUE),

(1, NULL, 'CONVERSE-CT70', 'Converse Chuck Taylor 70', 'converse-chuck-taylor-70',
 'Phiên bản cao cấp của đôi giày vải huyền thoại Chuck Taylor All Star.', 'UNISEX', FALSE, TRUE),

(1, NULL, 'VANS-OS', 'Vans Old Skool', 'vans-old-skool',
 'Đôi giày trượt ván kinh điển với sọc viền Jazz Stripe đặc trưng.', 'UNISEX', TRUE, TRUE);

-- 6. Thêm Biến thể (Variant) cho các sản phẩm mới
-- Giả sử các Product ID mới lần lượt là 4, 5, 6, 7, 8, 9
-- (tùy thuộc vào dữ liệu hiện có trong DB, bạn có thể cần điều chỉnh ID)

-- Nike Dunk Low Retro
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(4, 3, 1, 'SKU-DUNK-40-W', 3200000, 2800000, 25, TRUE),
(4, 4, 2, 'SKU-DUNK-41-B', 3200000, 2800000, 30, TRUE),
(4, 3, 3, 'SKU-DUNK-40-R', 3200000, NULL,    15, TRUE);

-- Adidas Samba OG
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(5, 3, 1, 'SKU-SAMBA-40-W', 2800000, 2400000, 40, TRUE),
(5, 4, 2, 'SKU-SAMBA-41-B', 2800000, 2400000, 35, TRUE),
(5, 5, 1, 'SKU-SAMBA-42-W', 2800000, NULL,    20, TRUE);

-- Puma Suede Classic
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(6, 3, 3, 'SKU-SUEDE-40-R', 2200000, 1900000, 18, TRUE),
(6, 4, 2, 'SKU-SUEDE-41-B', 2200000, NULL,    22, TRUE);

-- New Balance 574
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(7, 3, 4, 'SKU-NB574-40-NAVY', 2600000, 2200000, 28, TRUE),
(7, 5, 5, 'SKU-NB574-42-GREY', 2600000, NULL,    15, TRUE);

-- Converse Chuck Taylor 70
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(8, 1, 2, 'SKU-CT70-38-B', 1800000, 1500000, 50, TRUE),
(8, 2, 1, 'SKU-CT70-39-W', 1800000, NULL,    45, TRUE),
(8, 3, 3, 'SKU-CT70-40-R', 1800000, NULL,    30, TRUE);

-- Vans Old Skool
INSERT INTO Product_Variant (productId, sizeId, colorId, sku, price, salePrice, stockQuantity, isActive) VALUES
(9, 3, 2, 'SKU-VOS-40-B',  1600000, 1350000, 60, TRUE),
(9, 4, 1, 'SKU-VOS-41-W',  1600000, NULL,    40, TRUE);
