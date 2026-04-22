-- Drop user first if they exist
-- Now create user with prop privileges
DROP USER if exists 'springdemo'@'%' ;

CREATE USER 'springdemo'@'%' IDENTIFIED BY 'springdemo';

GRANT ALL PRIVILEGES ON * . * TO 'springdemo'@'%';

-- データベース作成（日本語対応）
CREATE DATABASE IF NOT EXISTS `product_management` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `product_management`;

-- Userテーブル作成
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` CHAR(80) NOT NULL,
  `enabled` TINYINT NOT NULL,  
  `first_name` VARCHAR(64) NOT NULL,
  `last_name` VARCHAR(64) NOT NULL,
  `email` VARCHAR(254) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Roleテーブル作成
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `display_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- UserとRoleの中間テーブル作成
DROP TABLE IF EXISTS `users_roles`;
CREATE TABLE `users_roles` (
  `user_id` INT(11) NOT NULL,
  `role_id` INT(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) 
    REFERENCES `users` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role` FOREIGN KEY (`role_id`) 
    REFERENCES `role` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Categoryテーブル作成
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Productテーブル作成
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `category_id` INT NOT NULL,
  `price` INT NOT NULL,
  `stock` INT DEFAULT 0,
  `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
  INDEX (`category_id`),
  INDEX (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- StockHistoryテーブル作成
DROP TABLE IF EXISTS `stock_history`;
CREATE TABLE `stock_history` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `change_quantity` INT NOT NULL,
  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
  INDEX (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Logテーブル作成
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `action` ENUM('LOGIN', 'LOGOUT', 'ADD_PRODUCT', 'UPDATE_PRODUCT', 'DELETE_PRODUCT') NOT NULL,
  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  INDEX (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Roleデータ挿入
-- ROLE_ADMIN
-- ROLE_MANAGER
-- ROLE_EMPLOYEE
INSERT INTO `role` (`name`, `display_name`) VALUES
('ROLE_ADMIN', '管理者'),
('ROLE_MANAGER', 'マネージャー'),
('ROLE_EMPLOYEE', '社員');

-- Userデータ（日本人を想定）
-- tanaka:password1
-- yamada:password2
-- suzuki:password3
-- kobayashi:password4
-- saito:password5
INSERT INTO `users` (`username`, `password`, `enabled`, `first_name`, `last_name`, `email`) VALUES
('tanaka', '$2a$10$LfRa7F1ewbjv3SkAjzMja.1x/e43wqV685FiMkH72r2FYJROrnToW', 1, '太郎', '田中', 'tanaka.taro@example.com'),
('yamada', '$2a$10$d8TdI0c7LYpx5oEeoouVxej1XIwr0hUmWRyOzae8AAXjXAyVp1BG.', 1, '花子', '山田', 'yamada.hanako@example.com'),
('suzuki', '$2a$10$MRrUhz6e/xusTOBM29450OijEBSB1kBUfmjcwgYsOB4zucib0R9/6', 1, '一郎', '鈴木', 'suzuki.ichiro@example.com'),
('kobayashi', '$2a$10$Q7v5NcmBOU.yhlzPv1zOi.ghhuEwmU6ZzZMrFpqD2JZ8xtenMuGr2', 1, 'さくら', '小林', 'kobayashi.sakura@example.com'),
('saito', '$2a$10$ftzCjmhVCTuaGrNU2qpjK.eGdCWTnRpoDfIYhRkAyLtQKw74Etz6m', 1, '健', '斉藤', 'saito.ken@example.com');

INSERT INTO `users` (`username`, `password`, `enabled`, `first_name`, `last_name`, `email`) VALUES
('kato', '$2a$10$kYpZ6ZdxV3P4K1zJabxEiOYZRGZ3O4qUhOOG52H2tPZ27/BpYvY26', 1, '和夫', '加藤', 'kato.kazuo@example.com'), -- password6
('shimizu', '$2a$10$HbWQKNvgplHdjD4GG11A9uMdGfXv7AnPB5ecxSPex/QknIcvhPDRy', 1, '恵美', '清水', 'shimizu.emi@example.com'), -- password7
('nakamura', '$2a$10$Ux5wb3PduB0LU.e/MQ7BWucm5Gp2yws5m.RREZoJmQ1eTGJPlXY3C', 1, '達也', '中村', 'nakamura.tatsuya@example.com'), -- password8
('inoue', '$2a$10$TcmZ17HDa.aNiGJXQmXYnuU1bXsP4r1w1B5eHJHVxyuOR2bK4Z1Ti', 1, '裕子', '井上', 'inoue.yuko@example.com'), -- password9
('fujimoto', '$2a$10$4Adzh5/u6W8.t2mP4hOjqufqRA2aKTKYY3HKYAjGFbSkM.7eMmtXi', 1, '亮', '藤本', 'fujimoto.ryo@example.com'), -- password10
('matsumoto', '$2a$10$5lZ68yCYOqVhJwFvlD0EZ.cLDIuAkFJvo0PA8Juo9nHyoY8IzMeum', 1, '智子', '松本', 'matsumoto.tomoko@example.com'), -- password11
('ando', '$2a$10$T17HJEo7PGotxOZ.GE.KnOz9DjvgHlS/E46P5HK4Y0sCJlOda.rNm', 1, '陽子', '安藤', 'ando.yoko@example.com'), -- password12
('morita', '$2a$10$WfVOROJfE6j71xPVlMVaZuKZoHCKAj9sE24QJ8Ao.Wc0gB9OYwh5a', 1, '浩', '森田', 'morita.hiroshi@example.com'), -- password13
('kudo', '$2a$10$.5jCllDiQOY8Na0PlgbwheqzQIWqCRUPQ9HnL1Fo3iMCO5.z06GE.', 1, '奈々', '工藤', 'kudo.nana@example.com'), -- password14
('oda', '$2a$10$kAmv2bN/SrmjiMi9KnKPveOM4FwNzKoe5zSDrrMMLolFiQai/rx9a', 1, '龍之介', '織田', 'oda.ryunosuke@example.com'), -- password15
('ishida', '$2a$10$z8Nv/PLyTlMdIfWXuE2gfuSOzT1Pi9BRV88egFbgpiGWWAWdGHQXa', 1, '香織', '石田', 'ishida.kaori@example.com'), -- password16
('yamamoto', '$2a$10$AiUQOp0K6uIaH9/F4ecj0OVBdTi3GrIuCOr8R9S6W8GUapOkOiv92', 1, '誠', '山本', 'yamamoto.makoto@example.com'), -- password17
('sakamoto', '$2a$10$xKD.q1JOgAEFEICuP1U1AOJXbZ.pJXRG1HfFT0N1/42pIBl24kXu2', 1, '優', '坂本', 'sakamoto.yu@example.com'), -- password18
('hasegawa', '$2a$10$.AkGmES0IN3W/FjvVnDsK.HQ0gV5ZjNkcyfbmHDqqUjRfzcyecUta', 1, '茂', '長谷川', 'hasegawa.shigeru@example.com'), -- password19
('yamaguchi', '$2a$10$CvQknRMZ9/z6PRJsPNC2WeLvRfFQa8hK.cIsbJ/TNS/95L69npvR2', 1, '舞', '山口', 'yamaguchi.mai@example.com'), -- password20
('hayashi', '$2a$10$68ddMniKb2px9OBI7VtF6ewvTX5r1DLugwdN5nOUc5JiMSNx9nEyq', 1, '進', '林', 'hayashi.susumu@example.com'), -- password21
('fukuda', '$2a$10$FbGnvJrUK/X.i17N1VWd9ewvEGIkDGNiVQ9s/QtN9p46jqLFs7Je6', 1, '陽平', '福田', 'fukuda.yohei@example.com'), -- password22
('maruyama', '$2a$10$LQNVdPlGoG5zxKP8NcxObezizbNO8U9Z15oiQF3WGdpOBLjGpFRsW', 1, '大輔', '丸山', 'maruyama.daisuke@example.com'), -- password23
('takagi', '$2a$10$2UIUVVcQ7UBanFoFhxOcG.a5F3MJoNddSbwxlMvMPlVWCoYMe5jJi', 1, '愛', '高木', 'takagi.ai@example.com'), -- password24
('endo', '$2a$10$yt.Fjz78Q/OLbhP.jPQiKOBH.N.vjeivEIHGyXtPbEYzR22z5sYwa', 1, '美香', '遠藤', 'endo.mika@example.com'), -- password25
('maeda', '$2a$10$PgrEPLk.IshFFWh5PQ2EruSiTTz2tSH7U7Qq76/SJ26oFxrkUjhpq', 1, '明', '前田', 'maeda.akira@example.com'), -- password26
('hirata', '$2a$10$ZNDSrGnEJ6mInQPLxN1ZEeM65ObkjfPL7P57pIRt3plXHNBcfLdqu', 1, '千尋', '平田', 'hirata.chihiro@example.com'), -- password27
('kaneko', '$2a$10$tsXUQAKGHXVVfM7GHoLJSuR7GEvlf.T0Cz/Rlc/fTqOQoBkpYzqe6', 1, '広', '金子', 'kaneko.hiro@example.com'), -- password28
('nishimura', '$2a$10$EwPV95v96YvXYTx/c.jB7uH.oIdtNRvlWn7ZlSPJfFmn46WDtpLpO', 1, '達也', '西村', 'nishimura.tatsuya@example.com'); -- password29


-- Users_Rolesデータ挿入
INSERT INTO `users_roles` (`user_id`, `role_id`) VALUES
(1, 1), -- Tanaka is admin
(1, 2), -- Tanaka is manager
(1, 3), -- Tanaka is employee
(2, 2), -- Yamada is manager
(2, 3), -- Yamada is employee
(3, 3), -- Suzuki is employee
(4, 3), -- Kobayashi is employee
(5, 3); -- Saito is employee

INSERT INTO users_roles (user_id, role_id) VALUES
(6, 3), -- Kato is employee
(7, 3), -- Shimizu is employee
(8, 2), -- Nakamura is manager
(8, 3), -- Nakamura is employee
(9, 2), -- Inoue is manager
(9, 3), -- Inoue is employee
(10, 3), -- Fujimoto is employee
(11, 1), -- Matsumoto is admin
(11, 2), -- Matsumoto is manager
(11, 3), -- Matsumoto is employee
(12, 3), -- Ando is employee
(13, 2), -- Morita is manager
(13, 3), -- Morita is employee
(14, 3), -- Kudo is employee
(15, 3), -- Oda is employee
(16, 1), -- Ishida is admin
(16, 2), -- Ishida is manager
(16, 3), -- Ishida is employee
(17, 3), -- Yamamoto is employee
(18, 3), -- Sakamoto is employee
(19, 2), -- Hasegawa is manager
(19, 3), -- Hasegawa is employee
(20, 3), -- Yamaguchi is employee
(21, 3), -- Hayashi is employee
(22, 2), -- Fukuda is manager
(22, 3), -- Fukuda is employee
(23, 3), -- Maruyama is employee
(24, 1), -- Takagi is admin
(24, 2), -- Takagi is manager
(24, 3), -- Takagi is employee
(25, 3), -- Endo is employee
(26, 2), -- Maeda is manager
(26, 3), -- Maeda is employee
(27, 3), -- Hirata is employee
(28, 2), -- Kaneko is manager
(28, 3), -- Kaneko is employee
(29, 3); -- Nishimura is employee

-- Categoryデータ挿入
INSERT INTO `category` (`name`) VALUES
('家電'),
('本'),
('衣類'),
('家具'),
('おもちゃ');

-- Productデータ挿入
INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `last_updated`) VALUES
('冷蔵庫', 1, 50000, 10, '2025-01-01 08:00:00'),
('洗濯機', 1, 30000, 8, '2025-01-02 09:30:00'),
('電子レンジ', 1, 15000, 12, '2025-01-03 10:45:00'),
('小説', 2, 1200, 50, '2025-01-04 11:15:00'),
('辞典', 2, 3500, 20, '2025-01-05 14:20:00'),
('Tシャツ', 3, 1500, 30, '2025-01-06 16:10:00'),
('ジーンズ', 3, 4000, 25, '2025-01-07 17:05:00'),
('ソファ', 4, 80000, 5, '2025-01-08 18:30:00'),
('ラジコンカー', 5, 5000, 15, '2025-01-09 10:10:00');

-- Logデータ挿入
INSERT INTO `log` (`user_id`, `action`, `timestamp`) VALUES
(1, 'login', '2024-01-01 08:00:00'),
(1, 'add_product', '2024-01-01 09:00:00'),
(2, 'update_product', '2024-01-02 10:00:00'),
(2, 'logout', '2024-01-02 11:00:00'),
(3, 'login', '2024-01-03 07:00:00'),
(3, 'delete_product', '2024-01-03 08:00:00'),
(4, 'login', '2024-01-04 06:30:00'),
(4, 'add_product', '2024-01-04 07:30:00'),
(5, 'login', '2024-01-05 08:15:00'),
(5, 'logout', '2024-01-05 09:00:00');

-- StockHistoryデータ挿入
-- Stock_historyデータ挿入
INSERT INTO `stock_history` (`product_id`, `change_quantity`, `timestamp`) VALUES
-- 冷蔵庫 (product_id = 1)
(1, 0, '2024-12-01 09:00:00'),   -- 初期在庫
(1, 5, '2024-12-05 10:00:00'),   -- 入庫 +5
(1, -2, '2024-12-10 11:00:00'),  -- 出庫 -2
(1, 7, '2024-12-15 12:00:00'),   -- 入庫 +7
-- 洗濯機 (product_id = 2)
(2, 0, '2024-12-02 09:30:00'),   -- 初期在庫
(2, 3, '2024-12-06 10:30:00'),   -- 入庫 +3
(2, -1, '2024-12-11 11:30:00'),  -- 出庫 -1
(2, 6, '2024-12-16 12:30:00'),   -- 入庫 +6
-- 電子レンジ (product_id = 3)
(3, 0, '2024-12-03 10:45:00'),   -- 初期在庫
(3, 8, '2024-12-07 11:45:00'),   -- 入庫 +8
(3, -3, '2024-12-12 12:45:00'),  -- 出庫 -3
(3, 7, '2024-12-17 13:45:00'),   -- 入庫 +7
-- 小説 (product_id = 4)
(4, 0, '2024-12-04 11:15:00'),   -- 初期在庫
(4, 20, '2024-12-08 12:15:00'),  -- 入庫 +20
(4, -5, '2024-12-13 13:15:00'),  -- 出庫 -5
(4, 35, '2024-12-18 14:15:00'),  -- 入庫 +35
-- 辞典 (product_id = 5)
(5, 0, '2024-12-05 14:20:00'),   -- 初期在庫
(5, 10, '2024-12-09 15:20:00'),  -- 入庫 +10
(5, -3, '2024-12-14 16:20:00'),  -- 出庫 -3
(5, 13, '2024-12-19 17:20:00'),  -- 入庫 +13
-- Tシャツ (product_id = 6)
(6, 0, '2024-12-06 16:10:00'),   -- 初期在庫
(6, 15, '2024-12-10 17:10:00'),  -- 入庫 +15
(6, -5, '2024-12-15 18:10:00'),  -- 出庫 -5
(6, 20, '2024-12-20 19:10:00'),  -- 入庫 +20
-- ジーンズ (product_id = 7)
(7, 0, '2024-12-07 17:05:00'),   -- 初期在庫
(7, 10, '2024-12-11 18:05:00'),  -- 入庫 +10
(7, -3, '2024-12-16 19:05:00'),  -- 出庫 -3
(7, 18, '2024-12-21 20:05:00'),  -- 入庫 +18
-- ソファ (product_id = 8)
(8, 0, '2024-12-08 18:30:00'),   -- 初期在庫
(8, 2, '2024-12-12 19:30:00'),   -- 入庫 +2
(8, -1, '2024-12-17 20:30:00'),  -- 出庫 -1
(8, 4, '2024-12-22 21:30:00'),   -- 入庫 +4
-- ラジコンカー (product_id = 9)
(9, 0, '2024-12-09 10:10:00'),   -- 初期在庫
(9, 5, '2024-12-13 11:10:00'),   -- 入庫 +5
(9, -2, '2024-12-18 12:10:00'),  -- 出庫 -2
(9, 12, '2024-12-23 13:10:00');  -- 入庫 +12
