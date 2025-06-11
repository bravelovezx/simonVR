-- 阅读表创建脚本

-- 1. 创建完整的阅读表（如果表不存在）
CREATE TABLE IF NOT EXISTS readings (
    reading_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '阅读记录ID',
    user_id INT NOT NULL COMMENT '所属用户ID',
    source_type VARCHAR(20) NOT NULL COMMENT '来源类型：recommended(推荐) 或 user_input(用户输入)',
    article_title VARCHAR(200) NOT NULL COMMENT '文章标题',
    article_content LONGTEXT NOT NULL COMMENT '文章内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_source_type (source_type),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at),
    INDEX idx_title (article_title),
    
    CONSTRAINT fk_readings_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读表';

-- 2. 添加来源类型的约束（可选，更严格的数据验证）
-- ALTER TABLE readings ADD CONSTRAINT chk_source_type CHECK (source_type IN ('recommended', 'user_input'));

-- 3. 查看表结构
DESCRIBE readings; 