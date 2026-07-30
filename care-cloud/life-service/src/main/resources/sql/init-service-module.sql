-- =====================================================
-- 服务产品中心 + 订阅关系管理 初始化脚本
-- 运行前请确认已在 life-service 数据库执行
-- =====================================================

-- 1. 服务分类表
CREATE TABLE IF NOT EXISTS `service_category` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`       VARCHAR(100) NOT NULL                COMMENT '分类名称',
  `sort`       INT          NOT NULL DEFAULT 0      COMMENT '排序号',
  `is_deleted` TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- 2. 服务产品目录表
CREATE TABLE IF NOT EXISTS `service_catalog` (
  `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `category_id` BIGINT         NOT NULL                COMMENT '分类ID',
  `name`        VARCHAR(200)   NOT NULL                COMMENT '服务名称',
  `description` VARCHAR(500)   DEFAULT NULL            COMMENT '服务描述',
  `price`       DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '定价',
   `unit`        VARCHAR(20)    NOT NULL DEFAULT 'month' COMMENT '计价单位：once/day/week/month/year/long',
  `is_active`   TINYINT        NOT NULL DEFAULT 1       COMMENT '是否上架 1-上架 0-下架',
  `create_date` DATE           DEFAULT NULL            COMMENT '创建日期',
  `create_time` TIME           DEFAULT NULL            COMMENT '创建时间',
  `update_date` DATE           DEFAULT NULL            COMMENT '修改日期',
  `update_time` TIME           DEFAULT NULL            COMMENT '修改时间',
  `is_deleted`  TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务产品目录表';

-- 3. 客户订阅记录表
CREATE TABLE IF NOT EXISTS `customer_subscription` (
  `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT         NOT NULL                COMMENT '客户ID',
  `catalog_id`  BIGINT         NOT NULL                COMMENT '服务产品ID',
  `start_date`  DATE           NOT NULL                COMMENT '订阅开始日期',
  `end_date`    DATE           DEFAULT NULL            COMMENT '订阅到期日期',
  `status`      VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE' COMMENT '订阅状态：ACTIVE/EXPIRED/CANCELLED',
  `price`       DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '订阅时价格快照',
  `create_date` DATE           DEFAULT NULL            COMMENT '创建日期',
  `create_time` TIME           DEFAULT NULL            COMMENT '创建时间',
  `update_date` DATE           DEFAULT NULL            COMMENT '修改日期',
  `update_time` TIME           DEFAULT NULL            COMMENT '修改时间',
  `is_deleted`  TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_catalog_id` (`catalog_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户订阅记录表';

-- =====================================================
-- 种子数据
-- =====================================================

-- 服务分类
INSERT INTO `service_category` (`id`, `name`, `sort`) VALUES
(1, '生活照料', 1),
(2, '医疗护理', 2),
(3, '膳食服务', 3),
(4, '健康管理', 4),
(5, '文娱活动', 5)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 服务产品目录
INSERT INTO `service_catalog` (`id`, `category_id`, `name`, `description`, `price`, `unit`, `is_active`) VALUES
(1, 1, '房间清洁', '每周一次深度清洁', 200.00, 'month', 1),
(2, 1, '衣物洗涤', '每周两次衣物清洗', 100.00, 'month', 1),
(3, 2, '常规体检', '每月一次常规健康检查', 300.00, 'month', 1),
(4, 2, '慢病管理', '高血压/糖尿病等慢性病跟踪管理', 500.00, 'month', 1),
(5, 3, '定制营养餐', '根据个人健康状况定制每日三餐', 900.00, 'month', 1),
(6, 3, '特殊饮食', '流食/软食等特殊饮食需求', 1200.00, 'month', 1),
(7, 4, '健康档案', '建立并维护个人电子健康档案', 50.00, 'month', 1),
(8, 4, '心理咨询', '每周一次专业心理咨询服务', 400.00, 'month', 1),
(9, 5, '手工课堂', '每周一次手工兴趣活动', 80.00, 'month', 1),
(10, 5, '书法班', '每周一次书法教学活动', 80.00, 'month', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- =====================================================
-- 4. 支付订单表
-- =====================================================
CREATE TABLE IF NOT EXISTS `payment_order` (
  `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no`     VARCHAR(64)    NOT NULL                COMMENT '商户订单号（= 支付宝 out_trade_no）',
  `customer_id`  BIGINT         NOT NULL                COMMENT '客户ID',
  `subject`      VARCHAR(200)   DEFAULT NULL            COMMENT '订单标题/商品名称',
  `biz_id`       BIGINT         DEFAULT NULL            COMMENT '关联业务记录ID',
  `biz_type`     VARCHAR(20)    DEFAULT NULL            COMMENT '关联业务类型：SUBSCRIPTION/RENEW',
  `duration`     INT            DEFAULT NULL            COMMENT '续约数量（仅RENEW类型使用）',
  `total_amount` DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '订单金额（元）',
  `status`       VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED',
  `create_date`  DATE           DEFAULT NULL            COMMENT '创建日期',
  `create_time`  TIME           DEFAULT NULL            COMMENT '创建时间',
  `pay_time`     TIME           DEFAULT NULL            COMMENT '支付时间',
  `is_deleted`   TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
