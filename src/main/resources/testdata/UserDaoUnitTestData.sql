DELETE FROM users_roles;
DELETE FROM role;
DELETE FROM users;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE role ALTER COLUMN id RESTART WITH 1;

INSERT INTO role (name, display_name) VALUES
('ROLE_ADMIN', '管理者'),
('ROLE_MANAGER', 'マネージャー'),
('ROLE_EMPLOYEE', '社員');

INSERT INTO users (id, username, password, enabled, first_name, last_name, email) VALUES
(1, 'tanaka', '$2a$10$LfRa7F1ewbjv3SkAjzMja.1x/e43wqV685FiMkH72r2FYJROrnToW', 1, '太郎', '田中', 'tanaka.taro@example.com'),
(3, 'suzuki', '$2a$10$MRrUhz6e/xusTOBM29450OijEBSB1kBUfmjcwgYsOB4zucib0R9/6', 1, '一郎', '鈴木', 'suzuki.ichiro@example.com'),
(4, 'kobayashi', '$2a$10$Q7v5NcmBOU.yhlzPv1zOi.ghhuEwmU6ZzZMrFpqD2JZ8xtenMuGr2', 1, 'さくら', '小林', 'kobayashi.sakura@example.com');

INSERT INTO users_roles (user_id, role_id) VALUES
(1, 1), -- Tanaka is admin
(1, 2), -- Tanaka is manager
(1, 3), -- Tanaka is employee
(3, 3), -- Suzuki is employee
(4, 3); -- Kobayashi is employee