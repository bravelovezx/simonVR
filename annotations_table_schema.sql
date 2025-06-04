-- 注释表创建脚本
-- 使用 original 字段作为原文字段

-- 1. 创建完整的注释表（如果表不存在）
CREATE TABLE IF NOT EXISTS annotations (
    annotation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '注释ID',
    user_id INT NOT NULL COMMENT '所属用户ID',
    position_json JSON COMMENT '位置信息JSON：页面位置、元素定位等',
    original TEXT COMMENT '原文内容',
    annotation_content TEXT NOT NULL COMMENT '注释内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at),
    
    CONSTRAINT fk_annotations_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='注释表';

-- 2. 如果表已存在且有 original_text 字段，可以选择删除它
-- ALTER TABLE annotations DROP COLUMN IF EXISTS original_text;

-- 3. 如果需要修改 original 字段类型为 TEXT（支持更长的原文）
ALTER TABLE annotations MODIFY COLUMN original TEXT COMMENT '原文内容';

-- 4. 查看表结构
DESCRIBE annotations; 