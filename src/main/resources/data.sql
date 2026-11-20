-- Initial Seed Data
INSERT INTO users (email, username, password, full_name, role, provider, active)
VALUES 
('admin@shop.com', 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Quản Trị Viên', 'ROLE_ADMIN', 'LOCAL', TRUE),
('user@shop.com', 'user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Khách Hàng', 'ROLE_USER', 'LOCAL', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO categories (id, name, slug, description, image_url, category_group, active)
VALUES 
(1, 'Mô hình Siêu xe 1:18 & 1:24', 'mo-hinh-sieu-xe', 'Các dòng siêu xe Lamborghini, Ferrari, Porsche, McLaren cao cấp', 'https://example.com/img/supercars.jpg', 'CAR_MODEL', TRUE),
(2, 'Mô hình Xe Cổ & Xe Đua F1', 'mo-hinh-xe-co-f1', 'Mô hình xe đua Công thức 1, xe cổ điển kinh điển', 'https://example.com/img/classic-cars.jpg', 'CAR_MODEL', TRUE),
(3, 'Mô hình Xe Diecast 1:64', 'mo-hinh-xe-1-64', 'Xe mô hình mini bỏ túi Hot Wheels, MiniGT, Tomica', 'https://example.com/img/hotwheels.jpg', 'CAR_MODEL', TRUE),
(4, 'Mô hình Gundam Gunpla', 'mo-hinh-gundam', 'Gundam chính hãng Bandai các cấp độ HG, RG, MG, PG', 'https://example.com/img/gundam.jpg', 'ROBOT_MODEL', TRUE),
(5, 'Mô hình Transformers', 'mo-hinh-transformers', 'Robot biến hình Optimus Prime, Bumblebee, Megatron', 'https://example.com/img/transformers.jpg', 'ROBOT_MODEL', TRUE),
(6, 'Mô hình Mecha & Anime Robot', 'mo-hinh-mecha-anime', 'Evangelion, Mazinger Z, Macross, Super Robot Wars', 'https://example.com/img/mecha.jpg', 'ROBOT_MODEL', TRUE)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, original_price, stock_quantity, scale, material, brand, category_id, status, featured, image_url)
VALUES 
('Mô hình Siêu Xe Lamborghini Aventador SVJ', 'lamborghini-aventador-svj-1-18', 'Mô hình tĩnh tỉ lệ 1:18, mở được full cửa, capo và cốp. Chi tiết nội thất cực nét.', 4500000, 4800000, 10, '1:18', 'Hợp kim Diecast', 'AutoArt', 1, 'IN_STOCK', TRUE, 'https://example.com/img/lambo-svj.jpg'),
('Mô hình Porsche 911 GT3 RS (992)', 'porsche-911-gt3-rs-1-18', 'Bản mô hình cao cấp của Porsche 911 GT3 RS thế hệ mới nhất, sơn màu Guards Red.', 4200000, 4500000, 8, '1:18', 'Hợp kim Diecast', 'Minichamps', 1, 'IN_STOCK', TRUE, 'https://example.com/img/porsche-gt3rs.jpg'),
('Mô hình Xe Đua F1 Red Bull RB19 Max Verstappen', 'red-bull-f1-rb19-verstappen-1-43', 'Chiếc xe vô địch thế giới F1 mùa giải 2023, tỉ lệ 1:43 kèm hộp mica trưng bày.', 1850000, 2000000, 15, '1:43', 'Resin cao cấp', 'Spark', 2, 'IN_STOCK', FALSE, 'https://example.com/img/f1-rb19.jpg'),
('Hộp Xe MiniGT Nissan Skyline GT-R R34 Nismo', 'nissan-gtr-r34-nismo-1-64', 'Xe mô hình tỉ lệ 1:64 gầm kim loại lốp cao su, chi tiết cực kỳ tinh xảo.', 320000, 350000, 50, '1:64', 'Hợp kim Diecast', 'MiniGT', 3, 'IN_STOCK', FALSE, 'https://example.com/img/gtr-r34-164.jpg'),
('Mô hình Gundam MG 1/100 RX-78-2 Ver 3.0', 'gundam-mg-rx-78-2-ver3', 'Gunpla Master Grade chính hãng Bandai, khung xương chuyển động linh hoạt khớp cao cấp.', 980000, 1100000, 25, '1/100 MG', 'Nhựa ABS/PS', 'Bandai Spirits', 4, 'IN_STOCK', TRUE, 'https://example.com/img/gundam-rx78-2.jpg'),
('Mô hình Gundam RG 1/144 Hi-Nu Gundam', 'gundam-rg-hi-nu-gundam', 'Một trong những mẫu Real Grade đẹp và chi tiết nhất của Bandai Spirits.', 1150000, 1250000, 20, '1/144 RG', 'Nhựa ABS/PS', 'Bandai Spirits', 4, 'IN_STOCK', TRUE, 'https://example.com/img/rg-hi-nu.jpg'),
('Mô hình Transformers DLX Optimus Prime Revenge of the Fallen', 'transformers-dlx-optimus-prime', 'Mô hình robot Threezero cao 28cm, hơn 50 khớp cử động, mắt led sáng rực rỡ.', 4950000, 5300000, 5, '28cm', 'Khung kim loại + Nhựa cao cấp', 'Threezero', 5, 'IN_STOCK', TRUE, 'https://example.com/img/optimus-dlx.jpg'),
('Mô hình Robot Evangelion EVA Unit-01 Test Type', 'evangelion-eva-01-threezero', 'Robo-Dou Evangelion Test Type-01 chính hãng Threezero, kèm nhiều phụ kiện vũ khí.', 3500000, 3800000, 7, '25cm', 'Hợp kim + Nhựa ABS', 'Threezero', 6, 'IN_STOCK', FALSE, 'https://example.com/img/eva-01.jpg')
ON CONFLICT (slug) DO NOTHING;
