-- ユーザーテーブルDDL
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` VARCHAR(32) NOT NULL COMMENT 'ユーザーID (KSUID)',
    `mail` VARCHAR(255) NOT NULL UNIQUE COMMENT 'メールアドレス',
    `user_name` VARCHAR(100) NOT NULL COMMENT 'ユーザー名',
    `user_intro` VARCHAR(500) DEFAULT NULL COMMENT '自己紹介',
    `user_pw` VARCHAR(255) DEFAULT NULL COMMENT 'パスワードハッシュ',
    `phone_num` VARCHAR(30) DEFAULT NULL COMMENT '電話番号',
    `profile_img` VARCHAR(500) DEFAULT NULL COMMENT 'プロフィール画像URL',
    `role_id` INT DEFAULT 1 COMMENT '権限ロールID (1: CLIENT, 2: PARTNER, 8: MANAGER, 9: ROOT)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    PRIMARY KEY (`user_id`),
    INDEX `idx_user_mail` (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ユーザーマスタ';
