-- 积累表字段修复脚本
-- 解决 type 字段数据截断问题

-- 1. 检查当前表结构
-- DESCRIBE accumulations;

-- 2. 修改 type 字段长度或使用ENUM
-- 方案一：增加字段长度（推荐）
ALTER TABLE accumulations MODIFY COLUMN type VARCHAR(20) NOT NULL COMMENT '积累类型：word(单词) 或 sentence(句子)';

-- 方案二：使用ENUM类型（更严格）
-- ALTER TABLE accumulations MODIFY COLUMN type ENUM('word', 'sentence') NOT NULL COMMENT '积累类型：word(单词) 或 sentence(句子)';

-- 3. 查看修改后的表结构
DESCRIBE accumulations;

-- 4. 如果表不存在，创建完整的积累表
CREATE TABLE IF NOT EXISTS accumulations (
    accumulation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '积累ID',
    user_id INT NOT NULL COMMENT '所属用户ID',
    type VARCHAR(20) NOT NULL COMMENT '积累类型：word(单词) 或 sentence(句子)',
    content TEXT NOT NULL COMMENT '积累内容',
    meaning TEXT NOT NULL COMMENT '当前语境下含义',
    position_json JSON COMMENT '位置信息JSON：module(模块) + ref_id(关联ID) + 原文位置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '积累时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    
    CONSTRAINT fk_accumulations_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积累表'; 