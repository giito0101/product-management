DELETE FROM users_roles;
DELETE FROM role;
DELETE FROM log;
DELETE FROM users;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE role ALTER COLUMN id RESTART WITH 1;

INSERT INTO role (name, display_name) VALUES
('ROLE_ADMIN', '管理者'),
('ROLE_MANAGER', 'マネージャー'),
('ROLE_EMPLOYEE', '社員');

INSERT INTO users (username, password, enabled, first_name, last_name, email) VALUES
('tanaka', '$2a$10$LfRa7F1ewbjv3SkAjzMja.1x/e43wqV685FiMkH72r2FYJROrnToW', 1, '太郎', '田中', 'tanaka.taro@example.com'),
('yamada', '$2a$10$d8TdI0c7LYpx5oEeoouVxej1XIwr0hUmWRyOzae8AAXjXAyVp1BG.', 1, '花子', '山田', 'yamada.hanako@example.com'),
('suzuki', '$2a$10$MRrUhz6e/xusTOBM29450OijEBSB1kBUfmjcwgYsOB4zucib0R9/6', 1, '一郎', '鈴木', 'suzuki.ichiro@example.com'),
('kobayashi', '$2a$10$Q7v5NcmBOU.yhlzPv1zOi.ghhuEwmU6ZzZMrFpqD2JZ8xtenMuGr2', 1, 'さくら', '小林', 'kobayashi.sakura@example.com'),
('saito', '$2a$10$ftzCjmhVCTuaGrNU2qpjK.eGdCWTnRpoDfIYhRkAyLtQKw74Etz6m', 1, '健', '斉藤', 'saito.ken@example.com'),
('kato', '$2a$10$kYpZ6ZdxV3P4K1zJabxEiOYZRGZ3O4qUhOOG52H2tPZ27/BpYvY26', 1, '和夫', '加藤', 'kato.kazuo@example.com'), -- password6
('shimizu', '$2a$10$HbWQKNvgplHdjD4GG11A9uMdGfXv7AnPB5ecxSPex/QknIcvhPDRy', 1, '恵美', '清水', 'shimizu.emi@example.com'), -- password7
('nakamura', '$2a$10$Ux5wb3PduB0LU.e/MQ7BWucm5Gp2yws5m.RREZoJmQ1eTGJPlXY3C', 1, '達也', '中村', 'nakamura.tatsuya@example.com'), -- password8
('inoue', '$2a$10$TcmZ17HDa.aNiGJXQmXYnuU1bXsP4r1w1B5eHJHVxyuOR2bK4Z1Ti', 1, '裕子', '井上', 'inoue.yuko@example.com'), -- password9
('fujimoto', '$2a$10$4Adzh5/u6W8.t2mP4hOjqufqRA2aKTKYY3HKYAjGFbSkM.7eMmtXi', 1, '亮', '藤本', 'fujimoto.ryo@example.com'), -- password10
('matsumoto', '$2a$10$5lZ68yCYOqVhJwFvlD0EZ.cLDIuAkFJvo0PA8Juo9nHyoY8IzMeum', 1, '智子', '松本', 'matsumoto.tomoko@example.com'), -- password11
('ando', '$2a$10$T17HJEo7PGotxOZ.GE.KnOz9DjvgHlS/E46P5HK4Y0sCJlOda.rNm', 1, '陽子', '安藤', 'ando.yoko@example.com'), -- password12
('morita', '$2a$10$WfVOROJfE6j71xPVlMVaZuKZoHCKAj9sE24QJ8Ao.Wc0gB9OYwh5a', 1, '浩', '森田', 'morita.hiroshi@example.com'); -- password13

INSERT INTO users_roles (user_id, role_id) VALUES
(1, 1), -- Tanaka is admin
(1, 2), -- Tanaka is manager
(1, 3), -- Tanaka is employee
(2, 2), -- Yamada is manager
(2, 3), -- Yamada is employee
(3, 3), -- Suzuki is employee
(4, 3), -- Kobayashi is employee
(5, 3), -- Saito is employee
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
(13, 3); -- Morita is employee
