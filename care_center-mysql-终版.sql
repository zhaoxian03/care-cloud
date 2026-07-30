/*
 Navicat Premium Dump SQL

 Source Server         : tbl
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : care_center

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 10/07/2026 18:28:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `role_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '角色级别：super_admin-超级管理员，admin-普通管理员，caregiver-管家',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者ID（仅super_admin可创建）',
  `create_date` date NOT NULL COMMENT '创建日期',
  `create_time` time NOT NULL COMMENT '创建时间',
  `update_date` date NOT NULL COMMENT '更新日期',
  `update_time` time NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role_level`(`role_level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'Admin1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000001', 'super_admin', 1, NULL, '2026-06-04', '15:13:24', '2026-06-16', '10:53:23');
INSERT INTO `admin` VALUES (2, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800000000', 'super_admin', 1, NULL, '2026-06-04', '15:13:55', '2026-06-16', '10:53:23');
INSERT INTO `admin` VALUES (3, 'admin01', '$2a$10$u0DT0NoO8dVevXkA0UOmseq7492.xCLO5n/OB/eg3vYLOKnBGTT5u', '贤', '14785203690', 'admin', 1, 1, '2026-06-11', '14:27:52', '2026-06-16', '10:53:23');
INSERT INTO `admin` VALUES (4, 'caregiver1', '$2a$10$VN5xQYauWcByRqRKvjHhx.tetRkxu42XcBIOeise2INLjNs2G9P32', 'aaa批发大户', '13900000002', 'caregiver', 1, 2, '2026-06-24', '15:10:28', '2026-06-24', '15:10:28');
INSERT INTO `admin` VALUES (5, 'xian01', '$2a$10$myFfN8whypKiCIGTVVo/Nu53ReYl9z/9SpvpujB0hDmoCQ/d1AGfe', '贤111', '14785236956', 'caregiver', 1, 2, '2026-06-25', '10:12:50', '2026-06-25', '10:12:50');
INSERT INTO `admin` VALUES (6, 'aabb', '$2a$10$5ulDlOGqBYu0GQusVrUJf.pXZLY0qx9d8coeknlCUqN0FnnPD6Xhy', '管家2', '14585693586', 'caregiver', 1, 2, '2026-06-25', '10:18:53', '2026-06-25', '10:18:53');
INSERT INTO `admin` VALUES (7, 'bbcc', '$2a$10$iH5a.5rzLvVI4V2lkXTKTOaHXlc/N3DgLAjx160tyoJde3oVlfrLy', 'zhaozhixian', '14785962578', 'caregiver', 1, 2, '2026-06-25', '10:55:44', '2026-06-25', '10:55:44');
INSERT INTO `admin` VALUES (8, 'aabbcc', '$2a$10$LfeV6mWvuafUy/0wTXxH4O03bxuVLVFAlTbgt7ds7gaKdffTN1PNy', 'xian02', '14785258589', 'admin', 1, 2, '2026-06-30', '23:39:30', '2026-06-30', '23:39:30');
INSERT INTO `admin` VALUES (9, 'admin02', '$2a$10$e.hpEjTZOaWen6Dh2B8wOOzepEZl1szjVwepLOyJxTY5Dx13KT6.S', '测试人员03', '14758963658', 'admin', 1, 2, '2026-07-03', '20:59:13', '2026-07-03', '20:59:13');
INSERT INTO `admin` VALUES (10, 'admin05', '$2a$10$fcYwP0yMFRVkrzFlSIw2JemsdTToTuiFFHc.ywn6pwbxR5nvDG9gq', '测试管家06', '14859568695', 'caregiver', 1, 2, '2026-07-04', '15:29:13', '2026-07-04', '15:29:13');
INSERT INTO `admin` VALUES (11, '123456', '$2a$10$G.2XxY9zcTMg3ivP6wlkAe69//wAWGrdfGbfE.0EQ0u4xxgAn4.MK', 'aabc', '14859653695', 'caregiver', 1, 2, '2026-07-05', '15:58:46', '2026-07-05', '15:58:46');
INSERT INTO `admin` VALUES (12, 'aabbccdd', '$2a$10$h3BAvSu0fwjhgebcJQLTaOAOiP1GcSP4oMbG0V4/LNYyzb3hTMBje', '测试管理员11', '14895695865', 'admin', 1, 2, '2026-07-10', '18:05:17', '2026-07-10', '18:05:17');

-- ----------------------------
-- Table structure for admin_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_role`(`admin_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role
-- ----------------------------
INSERT INTO `admin_role` VALUES (31, 1, 1);
INSERT INTO `admin_role` VALUES (29, 2, 1);
INSERT INTO `admin_role` VALUES (3, 3, 2);
INSERT INTO `admin_role` VALUES (9, 4, 2);
INSERT INTO `admin_role` VALUES (8, 4, 3);
INSERT INTO `admin_role` VALUES (10, 5, 2);
INSERT INTO `admin_role` VALUES (35, 6, 6);
INSERT INTO `admin_role` VALUES (7, 7, 3);
INSERT INTO `admin_role` VALUES (19, 8, 5);
INSERT INTO `admin_role` VALUES (22, 9, 5);
INSERT INTO `admin_role` VALUES (23, 10, 5);
INSERT INTO `admin_role` VALUES (24, 11, 5);
INSERT INTO `admin_role` VALUES (36, 12, 6);

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `bed_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '床号',
  `floor` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼层',
  `orientation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '朝向',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0空闲 1占用',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0正常 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_number`(`room_number` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '床位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, 'A101', '01', '1', '南', 1, '这是B楼', 0);
INSERT INTO `bed` VALUES (2, 'A101', '02', '1', '北', 0, NULL, 1);
INSERT INTO `bed` VALUES (3, 'A102', '01', '1', '南', 1, NULL, 0);
INSERT INTO `bed` VALUES (4, 'A102', '02', '1', '北', 1, NULL, 0);
INSERT INTO `bed` VALUES (5, 'A201', '01', '2', '南', 1, NULL, 0);
INSERT INTO `bed` VALUES (6, 'A201', '02', '2', '北', 1, NULL, 0);
INSERT INTO `bed` VALUES (7, 'A202', '01', '2', '南', 1, NULL, 0);
INSERT INTO `bed` VALUES (8, 'A202', '02', '2', '北', 0, NULL, 0);
INSERT INTO `bed` VALUES (9, 'B101', '01', '1', '东', 1, NULL, 0);
INSERT INTO `bed` VALUES (10, 'B101', '02', '1', '西', 0, NULL, 0);
INSERT INTO `bed` VALUES (11, 'B102', '01', '1', '东', 0, NULL, 0);
INSERT INTO `bed` VALUES (12, 'B102', '02', '1', '西', 0, NULL, 0);
INSERT INTO `bed` VALUES (13, 'B201', '01', '2', '东', 0, NULL, 0);
INSERT INTO `bed` VALUES (14, 'B201', '02', '2', '西', 0, NULL, 0);
INSERT INTO `bed` VALUES (15, 'B202', '01', '2', '东', 0, NULL, 0);
INSERT INTO `bed` VALUES (16, 'B202', '02', '2', '西', 0, NULL, 0);
INSERT INTO `bed` VALUES (17, 'A10111', '01', '1', '南', 0, '111', 1);
INSERT INTO `bed` VALUES (18, 'AA101A', '02', '1', '东', 0, '1111111', 1);
INSERT INTO `bed` VALUES (19, 'A104s', '02', '1', '北', 0, '这是一个床', 1);
INSERT INTO `bed` VALUES (20, 'AAA101', '01', '1', '北', 0, '测试床位06000', 0);
INSERT INTO `bed` VALUES (21, 'AAA101', '02', '1', '', 0, '测试二', 1);
INSERT INTO `bed` VALUES (22, 'A103', '06', '1', '北', 0, '11', 0);
INSERT INTO `bed` VALUES (23, 'AA102', '02', '1', '南', 0, '11', 0);

-- ----------------------------
-- Table structure for care_item
-- ----------------------------
DROP TABLE IF EXISTS `care_item`;
CREATE TABLE `care_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `default_duration_minutes` int NULL DEFAULT NULL COMMENT '预计耗时（分钟）',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_item_name_deleted`(`item_name` ASC, `is_deleted` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of care_item
-- ----------------------------
INSERT INTO `care_item` VALUES (1, '翻身拍背', 10, 1, 0);
INSERT INTO `care_item` VALUES (2, '协助洗澡', 30, 1, 0);
INSERT INTO `care_item` VALUES (3, '测量血压', 5, 1, 0);
INSERT INTO `care_item` VALUES (4, '测量血糖', 5, 1, 0);
INSERT INTO `care_item` VALUES (5, '协助进食', 20, 0, 0);
INSERT INTO `care_item` VALUES (6, '康复训练', 45, 1, 0);
INSERT INTO `care_item` VALUES (7, '外出陪同', 30, 1, 0);
INSERT INTO `care_item` VALUES (8, '假设这是一个护理项目', 31, 0, 1);
INSERT INTO `care_item` VALUES (9, '护理项目二', 30, 1, 1);
INSERT INTO `care_item` VALUES (10, '回家探亲', 31, 1, 0);
INSERT INTO `care_item` VALUES (11, '测试护理03', 30, 0, 1);
INSERT INTO `care_item` VALUES (12, '测试护理记录08', 30, 0, 0);
INSERT INTO `care_item` VALUES (13, '测试护理记录09', 30, 1, 1);
INSERT INTO `care_item` VALUES (14, '测试护理项目06', 30, 1, 0);
INSERT INTO `care_item` VALUES (15, '测试护理项目11', 30, 1, 1);

-- ----------------------------
-- Table structure for care_level
-- ----------------------------
DROP TABLE IF EXISTS `care_level`;
CREATE TABLE `care_level`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '级别名称',
  `price` decimal(10, 2) NOT NULL COMMENT '每日费用',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level_name`(`level_name` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理级别表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of care_level
-- ----------------------------
INSERT INTO `care_level` VALUES (1, '一级护理', 80.00, '基本生活照料', 1, 0);
INSERT INTO `care_level` VALUES (2, '二级护理', 150.00, '半自理护理', 1, 0);
INSERT INTO `care_level` VALUES (3, '三级护理', 220.00, '全护理', 1, 0);
INSERT INTO `care_level` VALUES (4, '10086级护理', 100.00, '假设这是一个护理等级', 1, 0);
INSERT INTO `care_level` VALUES (5, 'T100级护理', 100.01, '这是一段话', 0, 1);
INSERT INTO `care_level` VALUES (6, '1111', 1.00, '1111级护理', 1, 0);

-- ----------------------------
-- Table structure for care_level_item
-- ----------------------------
DROP TABLE IF EXISTS `care_level_item`;
CREATE TABLE `care_level_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `care_level_id` int NOT NULL COMMENT '护理项目ID',
  `care_item_id` int NOT NULL COMMENT '护理级别ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '执行顺序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level_item`(`care_level_id` ASC, `care_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理级别与项目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of care_level_item
-- ----------------------------
INSERT INTO `care_level_item` VALUES (1, 1, 3, 1);
INSERT INTO `care_level_item` VALUES (2, 1, 1, 2);
INSERT INTO `care_level_item` VALUES (3, 2, 3, 1);
INSERT INTO `care_level_item` VALUES (4, 2, 1, 2);
INSERT INTO `care_level_item` VALUES (5, 2, 5, 3);
INSERT INTO `care_level_item` VALUES (6, 2, 4, 4);
INSERT INTO `care_level_item` VALUES (7, 3, 3, 1);
INSERT INTO `care_level_item` VALUES (8, 3, 1, 2);
INSERT INTO `care_level_item` VALUES (9, 3, 5, 3);
INSERT INTO `care_level_item` VALUES (10, 3, 4, 4);
INSERT INTO `care_level_item` VALUES (11, 3, 2, 5);
INSERT INTO `care_level_item` VALUES (12, 3, 6, 6);
INSERT INTO `care_level_item` VALUES (16, 4, 1, 1);
INSERT INTO `care_level_item` VALUES (17, 4, 7, 2);

-- ----------------------------
-- Table structure for care_record
-- ----------------------------
DROP TABLE IF EXISTS `care_record`;
CREATE TABLE `care_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NULL DEFAULT NULL COMMENT '客户ID',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '护理员ID（管理员）',
  `care_item_id` int NOT NULL COMMENT '护理项目ID',
  `record_date` date NOT NULL COMMENT '执行日期',
  `record_time` time NOT NULL COMMENT '执行时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待执行，1-执行中，2-已完成',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of care_record
-- ----------------------------
INSERT INTO `care_record` VALUES (1, 1, 2, 1, '2026-06-15', '21:19:44', 1, '这是张三', 0);
INSERT INTO `care_record` VALUES (2, 2, 2, 2, '2026-06-15', '21:35:14', 2, '这是李四的', 0);
INSERT INTO `care_record` VALUES (3, 3, 2, 3, '2026-06-16', '11:23:23', 2, '测量血压1111', 1);
INSERT INTO `care_record` VALUES (4, 3, 2, 2, '2026-06-16', '20:34:12', 0, '我是王五，我要测血压', 1);
INSERT INTO `care_record` VALUES (5, 3, 2, 3, '2026-06-16', '20:34:43', 0, '我是王五，我要测血压1111', 1);
INSERT INTO `care_record` VALUES (6, 1, 2, 1, '2026-06-16', '21:01:55', 2, '我张三是一条咸鱼，帮我翻身', 0);
INSERT INTO `care_record` VALUES (7, 1, 2, 7, '2026-06-16', '21:13:05', 1, '我张三要出去玩', 0);
INSERT INTO `care_record` VALUES (8, 1, 2, 2, '2026-07-03', '10:27:52', 0, '帮三哥起身', 0);
INSERT INTO `care_record` VALUES (9, 1, 3, 2, '2026-07-03', '11:02:00', 1, '111', 0);
INSERT INTO `care_record` VALUES (10, 14, 5, 4, '2026-07-04', '15:25:45', 0, '测试', 0);
INSERT INTO `care_record` VALUES (11, 2, 11, 4, '2026-07-05', '15:59:41', 1, '测试护理05', 0);
INSERT INTO `care_record` VALUES (12, 1, 9, 6, '2026-07-10', '17:30:40', 1, '测试护理', 0);
INSERT INTO `care_record` VALUES (13, 1, 9, 10, '2026-07-10', '18:03:47', 0, '11', 0);

-- ----------------------------
-- Table structure for caregiver_relation
-- ----------------------------
DROP TABLE IF EXISTS `caregiver_relation`;
CREATE TABLE `caregiver_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NULL DEFAULT NULL COMMENT '老人ID（客户）',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '管家ID（管理员）',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_date` date NOT NULL COMMENT '创建日期',
  `create_time` time NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康管家关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of caregiver_relation
-- ----------------------------
INSERT INTO `caregiver_relation` VALUES (1, 1, 4, 1, '2026-06-24', '15:15:10');
INSERT INTO `caregiver_relation` VALUES (2, 3, 5, 0, '2026-06-25', '10:54:50');
INSERT INTO `caregiver_relation` VALUES (3, 3, 4, 0, '2026-06-25', '10:54:54');
INSERT INTO `caregiver_relation` VALUES (4, 3, 6, 1, '2026-06-25', '10:54:57');
INSERT INTO `caregiver_relation` VALUES (5, 3, 7, 0, '2026-06-25', '10:55:53');
INSERT INTO `caregiver_relation` VALUES (6, 1, 4, 1, '2026-06-25', '11:12:23');
INSERT INTO `caregiver_relation` VALUES (7, 1, 6, 0, '2026-06-25', '11:20:25');
INSERT INTO `caregiver_relation` VALUES (8, 1, 5, 0, '2026-06-25', '15:03:11');
INSERT INTO `caregiver_relation` VALUES (9, 6, 7, 0, '2026-07-01', '21:01:39');
INSERT INTO `caregiver_relation` VALUES (10, 2, 7, 0, '2026-07-01', '21:01:52');
INSERT INTO `caregiver_relation` VALUES (11, 2, 4, 0, '2026-07-01', '21:01:55');
INSERT INTO `caregiver_relation` VALUES (12, 13, 7, 0, '2026-07-05', '15:58:13');
INSERT INTO `caregiver_relation` VALUES (13, 13, 11, 0, '2026-07-05', '15:59:01');
INSERT INTO `caregiver_relation` VALUES (14, 16, 10, 0, '2026-07-07', '22:29:03');
INSERT INTO `caregiver_relation` VALUES (15, 16, 5, 1, '2026-07-07', '22:29:07');
INSERT INTO `caregiver_relation` VALUES (16, 16, 4, 0, '2026-07-07', '22:29:11');
INSERT INTO `caregiver_relation` VALUES (17, 17, 11, 0, '2026-07-10', '17:26:06');
INSERT INTO `caregiver_relation` VALUES (18, 17, 5, 0, '2026-07-10', '17:57:18');
INSERT INTO `caregiver_relation` VALUES (19, 17, 7, 0, '2026-07-10', '17:57:22');

-- ----------------------------
-- Table structure for check_in_record
-- ----------------------------
DROP TABLE IF EXISTS `check_in_record`;
CREATE TABLE `check_in_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `customer_id` bigint NULL DEFAULT NULL COMMENT '客户ID',
  `bed_id` bigint NOT NULL COMMENT '床位ID',
  `care_level_id` int NOT NULL COMMENT '护理级别ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date NULL DEFAULT NULL COMMENT '退住日期',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0入住中 1已退住 2外出中',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_date` date NOT NULL COMMENT '创建日期',
  `create_time` time NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bed_id`(`bed_id` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入住记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check_in_record
-- ----------------------------
INSERT INTO `check_in_record` VALUES (1, 1, 1, 1, '2026-06-11', '2026-06-11', 1, 0, '2026-06-11', '15:23:34');
INSERT INTO `check_in_record` VALUES (2, 2, 1, 1, '2026-06-11', '2026-06-11', 1, 0, '2026-06-11', '19:56:44');
INSERT INTO `check_in_record` VALUES (3, 2, 2, 2, '2026-06-01', '2026-06-11', 1, 0, '2026-06-11', '19:57:18');
INSERT INTO `check_in_record` VALUES (4, 1, 1, 1, '2026-06-03', NULL, 0, 0, '2026-06-11', '21:04:12');
INSERT INTO `check_in_record` VALUES (5, 2, 2, 1, '2026-06-08', '2026-06-16', 1, 0, '2026-06-11', '21:04:24');
INSERT INTO `check_in_record` VALUES (6, 2, 4, 2, '2026-06-08', NULL, 0, 0, '2026-06-16', '11:05:54');
INSERT INTO `check_in_record` VALUES (7, 3, 7, 3, '2026-06-19', '2026-06-25', 1, 0, '2026-06-25', '10:21:01');
INSERT INTO `check_in_record` VALUES (8, 14, 3, 1, '2026-07-03', NULL, 0, 0, '2026-07-03', '10:49:24');
INSERT INTO `check_in_record` VALUES (9, 3, 6, 2, '2026-07-01', '2026-07-04', 1, 0, '2026-07-03', '11:22:42');
INSERT INTO `check_in_record` VALUES (10, 13, 5, 2, '2026-06-30', '2026-07-07', 1, 0, '2026-07-04', '15:17:03');
INSERT INTO `check_in_record` VALUES (11, 7, 6, 1, '2026-07-03', NULL, 0, 0, '2026-07-04', '15:26:03');
INSERT INTO `check_in_record` VALUES (12, 3, 7, 3, '2026-07-01', NULL, 0, 0, '2026-07-07', '09:45:38');
INSERT INTO `check_in_record` VALUES (13, 11, 9, 1, '2026-07-01', NULL, 0, 0, '2026-07-07', '22:31:02');
INSERT INTO `check_in_record` VALUES (14, 17, 5, 1, '2026-07-02', NULL, 0, 0, '2026-07-10', '17:59:28');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号（唯一标识）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BCrypt加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '男' COMMENT '性别（男/女）',
  `emergency_contact` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人手机',
  `emergency_relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '与紧急联系人关系',
  `self_care_ability` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '自理' COMMENT '自理能力：自理/介助/介护',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
  `create_date` date NOT NULL COMMENT '创建日期',
  `create_time` time NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_real_name`(`real_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, '15896369565', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '张三', 70, '男', '14859653692', '儿子', '自理', NULL, 1, 0, '2026-06-04', '15:17:26');
INSERT INTO `customer` VALUES (2, '14256953285', '$2a$10$r7wGF2Kb18AyV/Qi6S7WC.hyheQOMAJ3orTvgUITmFUoxJZQskId6', '李四222', 71, '女', '14784896984', '子女', '介助', 'care-center/20260701222709737372523.jpg', 1, 0, '2026-06-05', '09:11:48');
INSERT INTO `customer` VALUES (3, '14859653586', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '王五11', 70, '男', '14756893256', '女儿', '自理', 'care-center/20260630145646885887913.webp', 1, 0, '2026-06-11', '21:02:29');
INSERT INTO `customer` VALUES (4, '14785479563', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '贤', 63, '男', '', '', '自理', NULL, 1, 1, '2026-06-11', '21:05:00');
INSERT INTO `customer` VALUES (5, '14785956985', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '贤01', 55, '男', '15879563584', '本人', '自理', 'care-center/20260630151802166628728.jpg', 1, 0, '2026-06-30', '15:18:04');
INSERT INTO `customer` VALUES (6, '15100000000', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '小明', 55, '男', NULL, NULL, '自理', NULL, 1, 0, '2026-06-30', '15:59:36');
INSERT INTO `customer` VALUES (7, '15879563584', '$2a$10$BwcuZtESeRpA3Rgme9VDF.uQ5SJZyKc6rV/pCE7hz5HVcw6Ft2qrK', '小红', 20, '男', NULL, NULL, '自理', 'care-center/20260630162713177561650.jpg', 1, 0, '2026-06-30', '16:11:45');
INSERT INTO `customer` VALUES (8, '14585965965', NULL, '小王', 21, '男', '14758269536', '儿子', '自理', 'care-center/20260630231703565902633.jpg', 0, 1, '2026-06-30', '23:17:06');
INSERT INTO `customer` VALUES (9, '14759562584', NULL, '测试人员02', 22, '男', '14598653695', '子女', '自理', '', 1, 1, '2026-07-01', '00:04:57');
INSERT INTO `customer` VALUES (10, '14589569596', NULL, '测试客户02', 22, '女', '', '', '介助', '', 1, 1, '2026-07-01', '14:12:59');
INSERT INTO `customer` VALUES (11, '14758963596', NULL, '测试客户03', 21, '男', '', '', '介助', 'care-center/20260701143646029883121.png', 0, 0, '2026-07-01', '14:36:22');
INSERT INTO `customer` VALUES (12, '13800138000', '$2a$10$CXmFSvR6lj5/GJnpRvaE7.eFgcQbWm3G6N3Sd4tcPJ7wwrMXdvEXe', '???', 75, '?', '13800001111', '??', '自理', NULL, 0, 0, '2026-07-01', '20:52:13');
INSERT INTO `customer` VALUES (13, '18598629536', '$2a$10$pEcWnQGdIoFGbcb5RnLruOh1DL6EhhEL4p9wgIi1Jkgc8OzPzO.Ne', '测试老人012', 65, '男', NULL, NULL, '自理', '', 1, 0, '2026-07-02', '22:25:32');
INSERT INTO `customer` VALUES (14, '14785964582', NULL, '李五', 65, '男', '', '', '自理', '', 1, 0, '2026-07-03', '10:48:53');
INSERT INTO `customer` VALUES (15, '15895695836', NULL, '测试老人04', 66, '男', '14858956958', '子女', '介助', 'care-center/20260630145646885887913.webp', 1, 1, '2026-07-04', '15:07:30');
INSERT INTO `customer` VALUES (16, '14859568694', '$10$pEcWnQGdIoFGbcb5RnLruOh1DL6EhhEL4p9wgIi1Jkgc8OzPzO.Ne', '测试老人08', 66, '男', '14785956865', '儿子', '自理', 'care-center/20260630151802166628728.jpg', 0, 0, '2026-07-07', '22:28:31');
INSERT INTO `customer` VALUES (17, '18596853952', '$2a$10$VaoyMXWDLJx3iwtBAm5ZJ.3IJOjudETH8q2gkqVFvzMtVE4Ud0jfS', '测试老人11', 74, '女', '17858963596', '女儿', '自理', 'care-center/20260630162713177561650.jpg', 1, 0, '2026-07-07', '22:42:05');
INSERT INTO `customer` VALUES (18, '14858956956', '$2a$10$pSU1HeiUemegIN0tzsekxOHbz/UdZYr0YVMUonhyjZCPkWOOvAW0q', '测试客户10', 70, '男', '', '', '自理', '', 1, 1, '2026-07-10', '17:57:49');

-- ----------------------------
-- Table structure for customer_service_subscription
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_subscription`;
CREATE TABLE `customer_service_subscription`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID，关联customer表',
  `service_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务类型，枚举：CARE_LEVEL/BED/CARE_ITEM/MEAL_PLAN/CAREGIVER',
  `service_id` bigint NOT NULL COMMENT '服务主键ID（对应各服务表的ID）',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务名称（冗余字段，方便列表展示，避免多表JOIN）',
  `start_date` date NOT NULL COMMENT '订阅开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '订阅到期日期（NULL表示长期有效）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '订阅状态：ACTIVE/EXPIRED/CANCELLED',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '订阅价格（预留字段，为后续费用结算准备）',
  `create_date` date NOT NULL COMMENT '创建日期',
  `create_time` time NOT NULL COMMENT '创建时间',
  `update_date` date NULL DEFAULT NULL COMMENT '最后修改日期',
  `update_time` time NULL DEFAULT NULL COMMENT '最后修改时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标志，0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_end_date`(`end_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户服务订阅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_subscription
-- ----------------------------
INSERT INTO `customer_service_subscription` VALUES (1, 1, 'CARE_LEVEL', 2, '二级护理', '2026-06-25', '2027-01-01', 'ACTIVE', NULL, '2026-06-25', '15:44:11', '2026-06-25', '19:59:53', 0);
INSERT INTO `customer_service_subscription` VALUES (2, 1, 'CARE_LEVEL', 3, '三级护理', '2026-06-22', '2026-07-02', 'CANCELLED', NULL, '2026-06-25', '15:45:09', '2026-06-25', '15:45:52', 1);
INSERT INTO `customer_service_subscription` VALUES (3, 1, 'CARE_ITEM', 1, '????', '2026-06-25', '2026-07-25', 'CANCELLED', NULL, '2026-06-25', '19:59:20', '2026-06-25', '20:06:57', 1);
INSERT INTO `customer_service_subscription` VALUES (4, 1, 'CARE_ITEM', 3, '测量血压', '2026-06-30', '2026-07-10', 'ACTIVE', NULL, '2026-06-25', '20:07:48', NULL, NULL, 0);
INSERT INTO `customer_service_subscription` VALUES (5, 3, 'CARE_ITEM', 7, '外出陪同', '2026-06-15', '2026-06-01', 'ACTIVE', NULL, '2026-06-25', '20:08:14', NULL, NULL, 0);

-- ----------------------------
-- Table structure for customer_subscription
-- ----------------------------
DROP TABLE IF EXISTS `customer_subscription`;
CREATE TABLE `customer_subscription`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `catalog_id` bigint NOT NULL COMMENT '服务产品ID',
  `start_date` date NOT NULL COMMENT '订阅开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '订阅到期日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '订阅状态：ACTIVE/EXPIRED/CANCELLED',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订阅时价格快照',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `create_time` time NULL DEFAULT NULL COMMENT '创建时间',
  `update_date` date NULL DEFAULT NULL COMMENT '修改日期',
  `update_time` time NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_catalog_id`(`catalog_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户订阅记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_subscription
-- ----------------------------
INSERT INTO `customer_subscription` VALUES (1, 3, 10, '2026-06-09', '2026-06-26', 'CANCELLED', 80.00, '2026-06-25', '21:12:02', '2026-06-26', '09:21:40', 0);
INSERT INTO `customer_subscription` VALUES (2, 1, 10, '2026-06-03', '2026-06-27', 'ACTIVE', 80.00, '2026-06-26', '09:21:19', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (3, 3, 2, '2026-06-09', '2026-07-04', 'ACTIVE', 100.00, '2026-06-26', '09:22:39', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (4, 2, 10, '2026-07-15', '2027-10-28', 'CANCELLED', 160.00, '2026-07-01', '21:03:07', '2026-07-01', '21:52:49', 0);
INSERT INTO `customer_subscription` VALUES (5, 12, 10, '2026-07-01', '2027-04-01', 'CANCELLED', 480.00, '2026-07-01', '22:39:39', '2026-07-01', '22:39:49', 0);
INSERT INTO `customer_subscription` VALUES (6, 2, 3, '2026-06-13', '2026-09-17', 'ACTIVE', 600.00, '2026-07-01', '22:56:48', '2026-07-02', '10:04:38', 0);
INSERT INTO `customer_subscription` VALUES (7, 2, 5, '2026-07-02', '2026-08-02', 'CANCELLED', 900.00, '2026-07-02', '08:59:36', '2026-07-02', '11:34:25', 0);
INSERT INTO `customer_subscription` VALUES (8, 2, 4, '2026-07-02', '2026-10-21', 'ACTIVE', 50.00, '2026-07-02', '08:59:59', '2026-07-02', '09:24:47', 0);
INSERT INTO `customer_subscription` VALUES (9, 2, 1, '2026-07-02', '2026-10-02', 'CANCELLED', 0.00, '2026-07-02', '09:05:48', '2026-07-02', '09:05:57', 0);
INSERT INTO `customer_subscription` VALUES (10, 2, 1, '2026-07-02', '2026-08-02', 'CANCELLED', 0.00, '2026-07-02', '09:07:16', '2026-07-02', '09:19:31', 0);
INSERT INTO `customer_subscription` VALUES (11, 2, 12, '2026-07-02', NULL, 'ACTIVE', 20.01, '2026-07-02', '09:11:21', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (12, 12, 10, '2026-08-01', '2027-12-02', 'ACTIVE', 240.00, '2026-07-02', '09:18:00', '2026-07-04', '15:19:20', 0);
INSERT INTO `customer_subscription` VALUES (13, 2, 2, '2026-07-02', '2026-08-02', 'ACTIVE', 100.00, '2026-07-02', '10:12:05', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (14, 2, 7, '2026-07-02', '2026-10-02', 'ACTIVE', 50.00, '2026-07-02', '10:23:51', '2026-07-02', '10:25:02', 0);
INSERT INTO `customer_subscription` VALUES (15, 2, 10, '2026-07-02', '2026-09-02', 'ACTIVE', 80.00, '2026-07-02', '10:43:27', '2026-07-02', '10:43:56', 0);
INSERT INTO `customer_subscription` VALUES (16, 2, 5, '2026-07-02', '2026-08-02', 'CANCELLED', 90.00, '2026-07-02', '11:34:29', '2026-07-02', '11:34:58', 0);
INSERT INTO `customer_subscription` VALUES (17, 3, 3, '2026-07-02', '2026-08-02', 'ACTIVE', 30.00, '2026-07-02', '20:08:27', '2026-07-02', '20:09:33', 0);
INSERT INTO `customer_subscription` VALUES (18, 3, 12, '2026-07-02', NULL, 'ACTIVE', 20.01, '2026-07-02', '20:21:24', '2026-07-02', '20:22:25', 0);
INSERT INTO `customer_subscription` VALUES (19, 2, 5, '2026-07-03', '2026-09-03', 'ACTIVE', 90.00, '2026-07-03', '11:09:39', '2026-07-03', '11:09:55', 0);
INSERT INTO `customer_subscription` VALUES (20, 6, 12, '2026-07-15', NULL, 'ACTIVE', 20.01, '2026-07-04', '15:19:54', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (21, 7, 6, '2026-07-07', '2027-01-04', 'ACTIVE', 10.00, '2026-07-04', '15:20:09', '2026-07-04', '15:49:59', 0);
INSERT INTO `customer_subscription` VALUES (22, 6, 3, '2026-06-30', '2026-07-08', 'ACTIVE', 30.00, '2026-07-04', '15:20:34', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (23, 7, 9, '2026-07-24', '2026-07-18', 'ACTIVE', 80.01, '2026-07-04', '15:21:26', '2026-07-04', '15:48:51', 0);
INSERT INTO `customer_subscription` VALUES (24, 6, 10, '2026-07-25', NULL, 'ACTIVE', 80.00, '2026-07-04', '15:21:48', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (25, 13, 3, '2026-07-05', '2026-08-05', 'CANCELLED', 30.00, '2026-07-04', '15:36:14', '2026-07-04', '16:08:41', 0);
INSERT INTO `customer_subscription` VALUES (26, 13, 2, '2026-07-04', '2027-01-04', 'ACTIVE', 100.00, '2026-07-04', '15:38:57', '2026-07-04', '15:39:56', 0);
INSERT INTO `customer_subscription` VALUES (27, 7, 4, '2026-07-04', '2026-07-11', 'CANCELLED', 50.00, '2026-07-04', '15:41:57', '2026-07-04', '16:13:50', 0);
INSERT INTO `customer_subscription` VALUES (28, 13, 4, '2026-07-04', '2026-07-11', 'CANCELLED', 50.00, '2026-07-04', '21:23:12', '2026-07-04', '21:57:42', 0);
INSERT INTO `customer_subscription` VALUES (29, 13, 7, '2026-07-04', '2026-12-04', 'ACTIVE', 100.00, '2026-07-04', '21:25:34', '2026-07-04', '21:25:52', 0);
INSERT INTO `customer_subscription` VALUES (30, 13, 10, '2026-07-04', '2026-09-04', 'ACTIVE', 80.00, '2026-07-04', '22:42:13', '2026-07-04', '22:43:31', 0);
INSERT INTO `customer_subscription` VALUES (31, 2, 1, '2026-07-06', NULL, 'CANCELLED', 0.00, '2026-07-06', '11:09:56', '2026-07-06', '11:13:36', 0);
INSERT INTO `customer_subscription` VALUES (32, 2, 37, '2026-07-06', '2026-07-07', 'CANCELLED', 0.00, '2026-07-06', '11:11:13', '2026-07-06', '11:13:35', 0);
INSERT INTO `customer_subscription` VALUES (33, 2, 1, '2026-07-06', NULL, 'CANCELLED', 0.00, '2026-07-06', '11:11:42', '2026-07-06', '11:13:34', 0);
INSERT INTO `customer_subscription` VALUES (34, 2, 1, '2026-07-06', NULL, 'CANCELLED', 0.01, '2026-07-06', '11:12:38', '2026-07-06', '11:13:32', 0);
INSERT INTO `customer_subscription` VALUES (35, 2, 1, '2026-07-06', NULL, 'CANCELLED', 0.01, '2026-07-06', '11:14:01', '2026-07-06', '11:45:59', 0);
INSERT INTO `customer_subscription` VALUES (36, 2, 18, '2026-07-06', '2026-07-13', 'ACTIVE', 10.00, '2026-07-06', '11:14:49', '2026-07-06', '11:15:19', 0);
INSERT INTO `customer_subscription` VALUES (37, 2, 37, '2026-07-06', '2026-07-08', 'ACTIVE', 0.02, '2026-07-06', '11:17:44', '2026-07-06', '11:18:14', 0);
INSERT INTO `customer_subscription` VALUES (38, 2, 1, '2026-07-06', NULL, 'ACTIVE', 0.01, '2026-07-06', '11:20:20', '2026-07-06', '11:20:47', 0);
INSERT INTO `customer_subscription` VALUES (39, 16, 12, '2026-07-08', '2026-07-17', 'CANCELLED', 20.01, '2026-07-07', '22:29:21', '2026-07-07', '22:32:34', 0);
INSERT INTO `customer_subscription` VALUES (40, 16, 29, '2026-07-08', '2026-07-17', 'CANCELLED', 15.00, '2026-07-07', '22:29:34', '2026-07-07', '22:32:31', 0);
INSERT INTO `customer_subscription` VALUES (41, 17, 1, '2026-07-07', NULL, 'ACTIVE', 0.01, '2026-07-07', '22:47:58', '2026-07-07', '22:48:29', 0);
INSERT INTO `customer_subscription` VALUES (42, 2, 17, '2026-07-09', '2026-10-09', 'ACTIVE', 100.00, '2026-07-09', '08:58:23', '2026-07-09', '08:59:06', 0);
INSERT INTO `customer_subscription` VALUES (43, 17, 17, '2026-07-01', '2026-07-16', 'ACTIVE', 50.00, '2026-07-10', '17:26:16', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (44, 17, 41, '2026-07-10', '2026-07-18', 'ACTIVE', 40.00, '2026-07-10', '18:00:55', NULL, NULL, 0);
INSERT INTO `customer_subscription` VALUES (45, 2, 13, '2026-07-10', '2026-08-10', 'CANCELLED', 30.00, '2026-07-10', '18:12:02', '2026-07-10', '18:12:31', 0);
INSERT INTO `customer_subscription` VALUES (46, 2, 26, '2026-07-10', '2026-08-10', 'ACTIVE', 100.00, '2026-07-10', '18:12:46', '2026-07-10', '18:13:15', 0);

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类（主食/热菜/凉菜/汤/水果等）',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name_deleted`(`name` ASC, `is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES (1, '凉菜012', '凉菜', 1, 0);
INSERT INTO `dish` VALUES (2, '米饭', '主食', 1, 0);
INSERT INTO `dish` VALUES (3, '紫菜蛋花汤', '汤', 1, 0);
INSERT INTO `dish` VALUES (4, '苹果汁', '饮品', 1, 0);
INSERT INTO `dish` VALUES (5, '红烧火腿', '热菜', 1, 0);
INSERT INTO `dish` VALUES (6, '香蕉', '水果', 1, 0);
INSERT INTO `dish` VALUES (7, '手打牛丸', '其他', 0, 0);
INSERT INTO `dish` VALUES (8, '馒头', '主食', 1, 0);
INSERT INTO `dish` VALUES (9, '测试菜品05', '其他', 0, 1);
INSERT INTO `dish` VALUES (10, '测试菜品07', '其他', 1, 0);
INSERT INTO `dish` VALUES (11, '测试菜品11', '其他', 1, 0);

-- ----------------------------
-- Table structure for file_record
-- ----------------------------
DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件MD5',
  `size` bigint NOT NULL COMMENT '文件大小',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件类型',
  `object_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'MinIO对象名',
  `bucket` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属bucket',
  `create_date` date NULL DEFAULT NULL,
  `create_time` time NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_md5_size_type`(`md5` ASC, `size` ASC, `content_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_record
-- ----------------------------
INSERT INTO `file_record` VALUES (1, '6ada74f1fc971b947f1838f56f10b970', 33700, 'image/jpeg', '20260629232503590741432.jpg', 'care-center', '2026-06-29', '23:25:04');
INSERT INTO `file_record` VALUES (2, 'a102a068cbfdf75c0754773504314d5c', 37524, 'image/jpeg', '20260629233008985544531.jpg', 'care-center', '2026-06-29', '23:30:09');
INSERT INTO `file_record` VALUES (3, 'a877a012a3f51ea71ea1fa7e6ebf4152', 19311, 'image/jpeg', '20260629235332496953578.jpg', 'care-center', '2026-06-29', '23:53:33');
INSERT INTO `file_record` VALUES (4, 'f89217aa81606c33b2058e27d806b20b', 8490, 'image/webp', '20260630145646885887913.webp', 'care-center', '2026-06-30', '14:56:47');
INSERT INTO `file_record` VALUES (5, '2eb74cb28e8ce8f282c76728fc5a4cb8', 10392, 'image/jpeg', '20260630151802166628728.jpg', 'care-center', '2026-06-30', '15:18:03');
INSERT INTO `file_record` VALUES (6, '122cdad7fcf2a38c5c944d1da52a7127', 637935, 'image/png', '20260630161544901599773.png', 'care-center', '2026-06-30', '16:15:45');
INSERT INTO `file_record` VALUES (7, '27ae6662bae2652647faf7fdda45df1a', 84985, 'image/jpeg', '20260630162713177561650.jpg', 'care-center', '2026-06-30', '16:27:14');
INSERT INTO `file_record` VALUES (8, '6e6fbfa06c9c59d4cc1f200a2324e9dd', 773214, 'image/jpeg', '20260630231703565902633.jpg', 'care-center', '2026-06-30', '23:17:04');
INSERT INTO `file_record` VALUES (9, '614c7e3ed12723b49a851da8937c4772', 1031268, 'image/png', '20260701143646029883121.png', 'care-center', '2026-07-01', '14:36:47');
INSERT INTO `file_record` VALUES (10, '7847ac413975d05d5519b3ab1f493a37', 1039647, 'image/jpeg', '20260701222420624462709.jpg', 'care-center', '2026-07-01', '22:24:21');
INSERT INTO `file_record` VALUES (11, '403177c879c66846d43443c3d2d87608', 115648, 'image/jpeg', '20260701222632589960865.jpg', 'care-center', '2026-07-01', '22:26:33');
INSERT INTO `file_record` VALUES (12, '74e2160d5ee83204da2a01b5e482157d', 99445, 'image/jpeg', '20260701222709737372523.jpg', 'care-center', '2026-07-01', '22:27:10');

-- ----------------------------
-- Table structure for meal_custom
-- ----------------------------
DROP TABLE IF EXISTS `meal_custom`;
CREATE TABLE `meal_custom`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NULL DEFAULT NULL COMMENT '客户ID',
  `meal_date` date NOT NULL COMMENT '日期',
  `meal_type` tinyint NOT NULL COMMENT '1早餐 2午餐 3晚餐',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `create_time` time NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-停用',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_date_type`(`customer_id` ASC, `meal_date` ASC, `meal_type` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '膳食定制表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meal_custom
-- ----------------------------
INSERT INTO `meal_custom` VALUES (1, 1, '2026-06-18', 2, '2026-06-18', '15:56:03', 0, 0);
INSERT INTO `meal_custom` VALUES (2, 2, '2026-06-23', 2, '2026-06-22', '11:46:31', 1, 0);
INSERT INTO `meal_custom` VALUES (3, 2, '2026-06-25', 3, '2026-06-22', '11:46:52', 0, 0);
INSERT INTO `meal_custom` VALUES (4, 1, '2026-06-11', 2, '2026-06-23', '19:53:55', 0, 0);
INSERT INTO `meal_custom` VALUES (5, 1, '2026-06-24', 1, '2026-06-23', '20:13:54', 0, 0);
INSERT INTO `meal_custom` VALUES (6, 1, '2026-06-24', 2, '2026-06-23', '20:13:54', 0, 0);
INSERT INTO `meal_custom` VALUES (7, 1, '2026-06-24', 3, '2026-06-23', '20:13:54', 0, 0);
INSERT INTO `meal_custom` VALUES (8, 2, '2026-06-26', 3, '2026-06-23', '20:55:37', 0, 0);
INSERT INTO `meal_custom` VALUES (9, 1, '2026-06-30', 1, '2026-06-23', '21:25:49', 1, 0);
INSERT INTO `meal_custom` VALUES (10, 1, '2026-06-30', 2, '2026-06-23', '21:25:49', 0, 0);
INSERT INTO `meal_custom` VALUES (11, 1, '2026-06-30', 3, '2026-06-23', '21:25:49', 0, 1);
INSERT INTO `meal_custom` VALUES (12, 2, '2026-06-24', 1, '2026-06-23', '21:45:02', 0, 0);
INSERT INTO `meal_custom` VALUES (13, 2, '2026-06-24', 2, '2026-06-23', '21:45:02', 0, 0);
INSERT INTO `meal_custom` VALUES (14, 2, '2026-06-24', 3, '2026-06-23', '21:45:03', 0, 0);
INSERT INTO `meal_custom` VALUES (15, 1, '2026-07-01', 1, '2026-06-23', '21:59:36', 0, 0);
INSERT INTO `meal_custom` VALUES (16, 1, '2026-07-01', 2, '2026-06-23', '21:59:36', 0, 0);
INSERT INTO `meal_custom` VALUES (18, 1, '2026-07-01', 3, '2026-06-23', '22:16:09', 0, 0);
INSERT INTO `meal_custom` VALUES (19, 2, '2026-06-30', 2, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (20, 2, '2026-07-02', 3, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (21, 2, '2026-07-03', 3, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (22, 2, '2026-07-01', 1, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (23, 2, '2026-07-01', 2, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (24, 2, '2026-07-01', 3, '2026-06-24', '10:15:02', 0, 0);
INSERT INTO `meal_custom` VALUES (25, 2, '2026-07-02', 1, '2026-07-01', '21:04:00', 0, 0);
INSERT INTO `meal_custom` VALUES (26, 2, '2026-07-03', 2, '2026-07-01', '21:14:30', 0, 0);
INSERT INTO `meal_custom` VALUES (27, 12, '2026-07-02', 1, '2026-07-01', '21:24:07', 0, 0);
INSERT INTO `meal_custom` VALUES (28, 12, '2026-07-02', 2, '2026-07-01', '21:24:07', 0, 0);
INSERT INTO `meal_custom` VALUES (30, 2, '2026-07-02', 2, '2026-07-01', '21:26:59', 0, 0);
INSERT INTO `meal_custom` VALUES (34, 2, '2026-06-30', 1, '2026-07-01', '21:28:19', 0, 0);
INSERT INTO `meal_custom` VALUES (37, 12, '2026-07-01', 1, '2026-07-01', '21:42:10', 0, 0);
INSERT INTO `meal_custom` VALUES (38, 12, '2026-07-01', 2, '2026-07-01', '21:44:04', 0, 0);
INSERT INTO `meal_custom` VALUES (39, 2, '2026-07-05', 1, '2026-07-01', '21:51:41', 0, 0);
INSERT INTO `meal_custom` VALUES (40, 2, '2026-07-05', 2, '2026-07-01', '21:52:14', 0, 0);
INSERT INTO `meal_custom` VALUES (41, 2, '2026-07-05', 3, '2026-07-01', '21:52:14', 0, 0);
INSERT INTO `meal_custom` VALUES (42, 2, '2026-07-04', 1, '2026-07-01', '22:06:34', 0, 0);
INSERT INTO `meal_custom` VALUES (43, 12, '2026-07-04', 1, '2026-07-02', '17:53:50', 0, 0);
INSERT INTO `meal_custom` VALUES (44, 3, '2026-07-02', 1, '2026-07-02', '20:15:49', 0, 0);
INSERT INTO `meal_custom` VALUES (45, 3, '2026-07-02', 3, '2026-07-02', '20:15:49', 0, 0);
INSERT INTO `meal_custom` VALUES (46, 3, '2026-07-02', 2, '2026-07-02', '20:16:11', 0, 0);
INSERT INTO `meal_custom` VALUES (47, 1, '2026-07-07', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (48, 1, '2026-07-07', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (49, 1, '2026-07-08', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (50, 1, '2026-07-08', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (51, 1, '2026-07-08', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (52, 2, '2026-07-07', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (53, 2, '2026-07-09', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (54, 2, '2026-07-10', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (55, 2, '2026-07-08', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (56, 2, '2026-07-08', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (57, 2, '2026-07-08', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (58, 2, '2026-07-09', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (59, 2, '2026-07-10', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (60, 12, '2026-07-09', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (61, 12, '2026-07-09', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (62, 2, '2026-07-09', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (63, 2, '2026-07-07', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (64, 12, '2026-07-08', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (65, 12, '2026-07-08', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (66, 2, '2026-07-12', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (67, 2, '2026-07-12', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (68, 2, '2026-07-12', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (69, 2, '2026-07-11', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (70, 12, '2026-07-11', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (71, 3, '2026-07-09', 1, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (72, 3, '2026-07-09', 3, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (73, 3, '2026-07-09', 2, '2026-07-04', '15:23:10', 0, 0);
INSERT INTO `meal_custom` VALUES (74, 7, '2026-07-05', 1, '2026-07-04', '15:23:37', 0, 0);
INSERT INTO `meal_custom` VALUES (75, 14, '2026-07-08', 1, '2026-07-07', '22:44:30', 0, 0);
INSERT INTO `meal_custom` VALUES (76, 14, '2026-07-08', 2, '2026-07-07', '22:44:30', 0, 0);
INSERT INTO `meal_custom` VALUES (77, 14, '2026-07-08', 3, '2026-07-07', '22:44:30', 0, 0);
INSERT INTO `meal_custom` VALUES (78, 2, '2026-07-11', 2, '2026-07-09', '08:57:43', 0, 0);
INSERT INTO `meal_custom` VALUES (79, 2, '2026-07-11', 3, '2026-07-09', '08:57:43', 0, 0);
INSERT INTO `meal_custom` VALUES (80, 12, '2026-07-11', 2, '2026-07-10', '17:30:00', 0, 0);
INSERT INTO `meal_custom` VALUES (81, 2, '2026-07-10', 1, '2026-07-10', '17:34:21', 0, 0);

-- ----------------------------
-- Table structure for meal_custom_dish
-- ----------------------------
DROP TABLE IF EXISTS `meal_custom_dish`;
CREATE TABLE `meal_custom_dish`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `meal_custom_id` bigint NOT NULL COMMENT '膳食记录ID',
  `dish_id` int NOT NULL COMMENT '菜品ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meal_dish`(`meal_custom_id` ASC, `dish_id` ASC) USING BTREE,
  INDEX `idx_meal_custom_id`(`meal_custom_id` ASC) USING BTREE,
  INDEX `idx_dish_id`(`dish_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 221 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '膳食记录与菜品关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meal_custom_dish
-- ----------------------------
INSERT INTO `meal_custom_dish` VALUES (1, 1, 1);
INSERT INTO `meal_custom_dish` VALUES (10, 2, 1);
INSERT INTO `meal_custom_dish` VALUES (9, 3, 1);
INSERT INTO `meal_custom_dish` VALUES (4, 4, 1);
INSERT INTO `meal_custom_dish` VALUES (12, 5, 3);
INSERT INTO `meal_custom_dish` VALUES (11, 5, 5);
INSERT INTO `meal_custom_dish` VALUES (7, 6, 1);
INSERT INTO `meal_custom_dish` VALUES (8, 7, 1);
INSERT INTO `meal_custom_dish` VALUES (13, 8, 1);
INSERT INTO `meal_custom_dish` VALUES (14, 9, 1);
INSERT INTO `meal_custom_dish` VALUES (15, 9, 2);
INSERT INTO `meal_custom_dish` VALUES (16, 10, 1);
INSERT INTO `meal_custom_dish` VALUES (17, 10, 2);
INSERT INTO `meal_custom_dish` VALUES (22, 12, 1);
INSERT INTO `meal_custom_dish` VALUES (20, 12, 2);
INSERT INTO `meal_custom_dish` VALUES (23, 12, 3);
INSERT INTO `meal_custom_dish` VALUES (21, 12, 8);
INSERT INTO `meal_custom_dish` VALUES (26, 13, 1);
INSERT INTO `meal_custom_dish` VALUES (24, 13, 2);
INSERT INTO `meal_custom_dish` VALUES (27, 13, 3);
INSERT INTO `meal_custom_dish` VALUES (25, 13, 8);
INSERT INTO `meal_custom_dish` VALUES (30, 14, 1);
INSERT INTO `meal_custom_dish` VALUES (28, 14, 2);
INSERT INTO `meal_custom_dish` VALUES (31, 14, 3);
INSERT INTO `meal_custom_dish` VALUES (29, 14, 8);
INSERT INTO `meal_custom_dish` VALUES (32, 15, 1);
INSERT INTO `meal_custom_dish` VALUES (33, 15, 3);
INSERT INTO `meal_custom_dish` VALUES (34, 16, 2);
INSERT INTO `meal_custom_dish` VALUES (35, 16, 4);
INSERT INTO `meal_custom_dish` VALUES (40, 18, 1);
INSERT INTO `meal_custom_dish` VALUES (41, 18, 2);
INSERT INTO `meal_custom_dish` VALUES (42, 19, 1);
INSERT INTO `meal_custom_dish` VALUES (43, 20, 1);
INSERT INTO `meal_custom_dish` VALUES (80, 21, 1);
INSERT INTO `meal_custom_dish` VALUES (81, 21, 2);
INSERT INTO `meal_custom_dish` VALUES (82, 21, 5);
INSERT INTO `meal_custom_dish` VALUES (45, 22, 1);
INSERT INTO `meal_custom_dish` VALUES (46, 22, 2);
INSERT INTO `meal_custom_dish` VALUES (47, 22, 3);
INSERT INTO `meal_custom_dish` VALUES (48, 22, 8);
INSERT INTO `meal_custom_dish` VALUES (49, 23, 1);
INSERT INTO `meal_custom_dish` VALUES (50, 23, 2);
INSERT INTO `meal_custom_dish` VALUES (51, 23, 3);
INSERT INTO `meal_custom_dish` VALUES (52, 23, 8);
INSERT INTO `meal_custom_dish` VALUES (53, 24, 1);
INSERT INTO `meal_custom_dish` VALUES (54, 24, 2);
INSERT INTO `meal_custom_dish` VALUES (55, 24, 3);
INSERT INTO `meal_custom_dish` VALUES (56, 24, 8);
INSERT INTO `meal_custom_dish` VALUES (58, 25, 4);
INSERT INTO `meal_custom_dish` VALUES (57, 25, 6);
INSERT INTO `meal_custom_dish` VALUES (59, 26, 1);
INSERT INTO `meal_custom_dish` VALUES (61, 26, 2);
INSERT INTO `meal_custom_dish` VALUES (60, 26, 5);
INSERT INTO `meal_custom_dish` VALUES (62, 27, 1);
INSERT INTO `meal_custom_dish` VALUES (63, 27, 2);
INSERT INTO `meal_custom_dish` VALUES (64, 28, 1);
INSERT INTO `meal_custom_dish` VALUES (65, 28, 2);
INSERT INTO `meal_custom_dish` VALUES (66, 30, 3);
INSERT INTO `meal_custom_dish` VALUES (67, 30, 7);
INSERT INTO `meal_custom_dish` VALUES (68, 34, 2);
INSERT INTO `meal_custom_dish` VALUES (78, 37, 2);
INSERT INTO `meal_custom_dish` VALUES (79, 37, 3);
INSERT INTO `meal_custom_dish` VALUES (74, 38, 1);
INSERT INTO `meal_custom_dish` VALUES (75, 38, 2);
INSERT INTO `meal_custom_dish` VALUES (76, 38, 3);
INSERT INTO `meal_custom_dish` VALUES (103, 39, 3);
INSERT INTO `meal_custom_dish` VALUES (99, 40, 3);
INSERT INTO `meal_custom_dish` VALUES (97, 40, 4);
INSERT INTO `meal_custom_dish` VALUES (98, 40, 8);
INSERT INTO `meal_custom_dish` VALUES (102, 41, 3);
INSERT INTO `meal_custom_dish` VALUES (100, 41, 4);
INSERT INTO `meal_custom_dish` VALUES (101, 41, 8);
INSERT INTO `meal_custom_dish` VALUES (104, 42, 2);
INSERT INTO `meal_custom_dish` VALUES (105, 43, 3);
INSERT INTO `meal_custom_dish` VALUES (106, 43, 6);
INSERT INTO `meal_custom_dish` VALUES (107, 44, 2);
INSERT INTO `meal_custom_dish` VALUES (108, 44, 5);
INSERT INTO `meal_custom_dish` VALUES (109, 44, 8);
INSERT INTO `meal_custom_dish` VALUES (113, 45, 2);
INSERT INTO `meal_custom_dish` VALUES (116, 45, 3);
INSERT INTO `meal_custom_dish` VALUES (114, 45, 5);
INSERT INTO `meal_custom_dish` VALUES (115, 45, 8);
INSERT INTO `meal_custom_dish` VALUES (117, 46, 3);
INSERT INTO `meal_custom_dish` VALUES (118, 46, 4);
INSERT INTO `meal_custom_dish` VALUES (119, 46, 6);
INSERT INTO `meal_custom_dish` VALUES (120, 47, 1);
INSERT INTO `meal_custom_dish` VALUES (121, 47, 2);
INSERT INTO `meal_custom_dish` VALUES (122, 48, 1);
INSERT INTO `meal_custom_dish` VALUES (123, 48, 2);
INSERT INTO `meal_custom_dish` VALUES (124, 49, 1);
INSERT INTO `meal_custom_dish` VALUES (125, 49, 3);
INSERT INTO `meal_custom_dish` VALUES (126, 50, 2);
INSERT INTO `meal_custom_dish` VALUES (127, 50, 4);
INSERT INTO `meal_custom_dish` VALUES (128, 51, 1);
INSERT INTO `meal_custom_dish` VALUES (129, 51, 2);
INSERT INTO `meal_custom_dish` VALUES (130, 52, 1);
INSERT INTO `meal_custom_dish` VALUES (131, 53, 1);
INSERT INTO `meal_custom_dish` VALUES (206, 54, 1);
INSERT INTO `meal_custom_dish` VALUES (207, 54, 2);
INSERT INTO `meal_custom_dish` VALUES (209, 54, 3);
INSERT INTO `meal_custom_dish` VALUES (208, 54, 5);
INSERT INTO `meal_custom_dish` VALUES (135, 55, 1);
INSERT INTO `meal_custom_dish` VALUES (136, 55, 2);
INSERT INTO `meal_custom_dish` VALUES (137, 55, 3);
INSERT INTO `meal_custom_dish` VALUES (138, 55, 8);
INSERT INTO `meal_custom_dish` VALUES (139, 56, 1);
INSERT INTO `meal_custom_dish` VALUES (140, 56, 2);
INSERT INTO `meal_custom_dish` VALUES (141, 56, 3);
INSERT INTO `meal_custom_dish` VALUES (142, 56, 8);
INSERT INTO `meal_custom_dish` VALUES (143, 57, 1);
INSERT INTO `meal_custom_dish` VALUES (144, 57, 2);
INSERT INTO `meal_custom_dish` VALUES (145, 57, 3);
INSERT INTO `meal_custom_dish` VALUES (146, 57, 8);
INSERT INTO `meal_custom_dish` VALUES (147, 58, 4);
INSERT INTO `meal_custom_dish` VALUES (148, 58, 6);
INSERT INTO `meal_custom_dish` VALUES (202, 59, 1);
INSERT INTO `meal_custom_dish` VALUES (203, 59, 2);
INSERT INTO `meal_custom_dish` VALUES (205, 59, 3);
INSERT INTO `meal_custom_dish` VALUES (204, 59, 5);
INSERT INTO `meal_custom_dish` VALUES (152, 60, 1);
INSERT INTO `meal_custom_dish` VALUES (153, 60, 2);
INSERT INTO `meal_custom_dish` VALUES (154, 61, 1);
INSERT INTO `meal_custom_dish` VALUES (155, 61, 2);
INSERT INTO `meal_custom_dish` VALUES (156, 62, 3);
INSERT INTO `meal_custom_dish` VALUES (157, 62, 7);
INSERT INTO `meal_custom_dish` VALUES (158, 63, 2);
INSERT INTO `meal_custom_dish` VALUES (159, 64, 2);
INSERT INTO `meal_custom_dish` VALUES (160, 64, 3);
INSERT INTO `meal_custom_dish` VALUES (161, 65, 1);
INSERT INTO `meal_custom_dish` VALUES (162, 65, 2);
INSERT INTO `meal_custom_dish` VALUES (163, 65, 3);
INSERT INTO `meal_custom_dish` VALUES (211, 66, 2);
INSERT INTO `meal_custom_dish` VALUES (210, 66, 3);
INSERT INTO `meal_custom_dish` VALUES (165, 67, 3);
INSERT INTO `meal_custom_dish` VALUES (166, 67, 4);
INSERT INTO `meal_custom_dish` VALUES (167, 67, 8);
INSERT INTO `meal_custom_dish` VALUES (168, 68, 3);
INSERT INTO `meal_custom_dish` VALUES (169, 68, 4);
INSERT INTO `meal_custom_dish` VALUES (170, 68, 8);
INSERT INTO `meal_custom_dish` VALUES (212, 69, 2);
INSERT INTO `meal_custom_dish` VALUES (213, 69, 4);
INSERT INTO `meal_custom_dish` VALUES (214, 69, 6);
INSERT INTO `meal_custom_dish` VALUES (172, 70, 3);
INSERT INTO `meal_custom_dish` VALUES (173, 70, 6);
INSERT INTO `meal_custom_dish` VALUES (174, 71, 2);
INSERT INTO `meal_custom_dish` VALUES (175, 71, 5);
INSERT INTO `meal_custom_dish` VALUES (176, 71, 8);
INSERT INTO `meal_custom_dish` VALUES (177, 72, 2);
INSERT INTO `meal_custom_dish` VALUES (178, 72, 3);
INSERT INTO `meal_custom_dish` VALUES (179, 72, 5);
INSERT INTO `meal_custom_dish` VALUES (180, 72, 8);
INSERT INTO `meal_custom_dish` VALUES (181, 73, 3);
INSERT INTO `meal_custom_dish` VALUES (182, 73, 4);
INSERT INTO `meal_custom_dish` VALUES (183, 73, 6);
INSERT INTO `meal_custom_dish` VALUES (186, 74, 3);
INSERT INTO `meal_custom_dish` VALUES (185, 74, 7);
INSERT INTO `meal_custom_dish` VALUES (187, 75, 1);
INSERT INTO `meal_custom_dish` VALUES (188, 76, 2);
INSERT INTO `meal_custom_dish` VALUES (189, 77, 8);
INSERT INTO `meal_custom_dish` VALUES (215, 78, 2);
INSERT INTO `meal_custom_dish` VALUES (216, 78, 4);
INSERT INTO `meal_custom_dish` VALUES (217, 78, 6);
INSERT INTO `meal_custom_dish` VALUES (218, 79, 2);
INSERT INTO `meal_custom_dish` VALUES (219, 79, 4);
INSERT INTO `meal_custom_dish` VALUES (220, 79, 6);
INSERT INTO `meal_custom_dish` VALUES (196, 80, 5);
INSERT INTO `meal_custom_dish` VALUES (197, 80, 6);
INSERT INTO `meal_custom_dish` VALUES (198, 81, 1);
INSERT INTO `meal_custom_dish` VALUES (199, 81, 2);
INSERT INTO `meal_custom_dish` VALUES (201, 81, 3);
INSERT INTO `meal_custom_dish` VALUES (200, 81, 5);

-- ----------------------------
-- Table structure for out_record
-- ----------------------------
DROP TABLE IF EXISTS `out_record`;
CREATE TABLE `out_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NULL DEFAULT NULL COMMENT '客户ID',
  `out_date` date NOT NULL COMMENT '外出日期',
  `out_time` time NOT NULL COMMENT '外出时间',
  `expected_back_date` date NULL DEFAULT NULL COMMENT '预计返回日期',
  `expected_back_time` time NULL DEFAULT NULL COMMENT '预计返回时间',
  `actual_back_date` date NULL DEFAULT NULL COMMENT '实际返回日期',
  `actual_back_time` time NULL DEFAULT NULL COMMENT '实际返回时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '0外出中 1已返回 2超时',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外出原因',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0存在 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_out_date`(`out_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '外出登记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of out_record
-- ----------------------------
INSERT INTO `out_record` VALUES (3, 2, '2026-06-11', '22:17:18', '2026-06-13', '00:00:00', '2026-06-12', '09:26:07', 1, '回家', 1);
INSERT INTO `out_record` VALUES (4, 1, '2026-06-12', '10:05:51', '2026-06-20', '02:02:01', '2026-06-12', '10:44:58', 1, '回家', 0);
INSERT INTO `out_record` VALUES (5, 2, '2026-06-15', '20:58:44', '2026-06-15', '20:58:18', '2026-06-15', '20:58:57', 1, '吃饭', 0);
INSERT INTO `out_record` VALUES (6, 3, '2026-07-03', '11:23:35', '2026-07-07', '11:23:09', '2026-07-04', '15:18:12', 1, '11', 0);
INSERT INTO `out_record` VALUES (7, 1, '2026-07-04', '15:12:25', '2026-07-07', '15:12:13', '2026-07-07', '22:32:08', 1, '111', 0);
INSERT INTO `out_record` VALUES (8, 11, '2026-07-07', '22:31:22', '2026-07-07', '22:19:09', '2026-07-10', '17:27:55', 1, '11', 0);
INSERT INTO `out_record` VALUES (9, 17, '2026-07-10', '18:00:03', '2026-07-10', '17:59:52', '2026-07-10', '18:00:10', 1, '11', 0);

-- ----------------------------
-- Table structure for payment_order
-- ----------------------------
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商户订单号（= 支付宝 out_trade_no）',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题/商品名称',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '关联业务记录ID',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务类型：SUBSCRIPTION/RENEW',
  `duration` int NULL DEFAULT NULL COMMENT '续约数量（仅RENEW类型使用）',
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额（元）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED/EXPIRED',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `create_time` time NULL DEFAULT NULL COMMENT '创建时间',
  `pay_time` time NULL DEFAULT NULL COMMENT '支付时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_order
-- ----------------------------
INSERT INTO `payment_order` VALUES (1, '202607021041092911604446', 2, '健康档案 - 续订', 14, 'RENEW', 1, 50.00, 'EXPIRED', '2026-07-02', '10:41:09', NULL, 0);
INSERT INTO `payment_order` VALUES (2, '202607021041292630461874', 2, '健康档案 - 续订', 14, 'RENEW', 1, 50.00, 'EXPIRED', '2026-07-02', '10:41:29', NULL, 0);
INSERT INTO `payment_order` VALUES (3, '202607021043268169770685', 2, '书法班 - 订阅', 15, 'SUBSCRIPTION', NULL, 80.00, 'SUCCESS', '2026-07-02', '10:43:27', '10:43:56', 0);
INSERT INTO `payment_order` VALUES (4, '202607021044483141198524', 2, '书法班 - 续订', 15, 'RENEW', 1, 80.00, 'SUCCESS', '2026-07-02', '10:44:48', '10:45:17', 0);
INSERT INTO `payment_order` VALUES (5, '202607021047029003475918', 2, '常规体检 - 续订', 6, 'RENEW', 2, 60.00, 'EXPIRED', '2026-07-02', '10:47:03', NULL, 0);
INSERT INTO `payment_order` VALUES (6, '202607021047300108420991', 2, '常规体检 - 续订', 6, 'RENEW', 1, 30.00, 'EXPIRED', '2026-07-02', '10:47:30', NULL, 0);
INSERT INTO `payment_order` VALUES (7, '202607021047476802883326', 2, '常规体检 - 续订', 6, 'RENEW', 1, 30.00, 'SUCCESS', '2026-07-02', '10:47:48', NULL, 0);
INSERT INTO `payment_order` VALUES (8, '202607021134289934843714', 2, '定制营养餐 - 订阅', 16, 'SUBSCRIPTION', NULL, 90.00, 'SUCCESS', '2026-07-02', '11:34:29', '11:34:58', 0);
INSERT INTO `payment_order` VALUES (9, '202607022008268425292109', 3, '常规体检 - 订阅', 17, 'SUBSCRIPTION', NULL, 30.00, 'SUCCESS', '2026-07-02', '20:08:27', '20:09:33', 0);
INSERT INTO `payment_order` VALUES (10, '202607022021240449954062', 3, '生活服务06 - 订阅', 18, 'SUBSCRIPTION', NULL, 20.01, 'SUCCESS', '2026-07-02', '20:21:24', NULL, 0);
INSERT INTO `payment_order` VALUES (11, '202607031102311726236891', 2, '定制营养餐 - 续订', 16, 'RENEW', 1, 90.00, 'SUCCESS', '2026-07-03', '11:02:31', NULL, 0);
INSERT INTO `payment_order` VALUES (12, '202607031103343841483383', 2, '定制营养餐 - 续订', 16, 'RENEW', 1, 90.00, 'SUCCESS', '2026-07-03', '11:03:34', NULL, 0);
INSERT INTO `payment_order` VALUES (13, '202607031109391128471587', 2, '定制营养餐 - 订阅', 19, 'SUBSCRIPTION', NULL, 90.00, 'SUCCESS', '2026-07-03', '11:09:39', NULL, 0);
INSERT INTO `payment_order` VALUES (14, '202607031110098384534991', 2, '定制营养餐 - 续订', 19, 'RENEW', 1, 90.00, 'SUCCESS', '2026-07-03', '11:10:10', NULL, 0);
INSERT INTO `payment_order` VALUES (15, '202607031127498517523330', 2, '定制营养餐 - 续订', 19, 'RENEW', 1, 90.00, 'SUCCESS', '2026-07-03', '11:27:50', NULL, 0);
INSERT INTO `payment_order` VALUES (16, '202607041536139195090626', 13, '常规体检 - 订阅', 25, 'SUBSCRIPTION', NULL, 30.00, 'EXPIRED', '2026-07-04', '15:36:14', NULL, 0);
INSERT INTO `payment_order` VALUES (17, '202607041538571191598279', 13, '衣物洗涤 - 订阅', 26, 'SUBSCRIPTION', NULL, 100.00, 'SUCCESS', '2026-07-04', '15:38:57', NULL, 0);
INSERT INTO `payment_order` VALUES (18, '202607041540045737723871', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'EXPIRED', '2026-07-04', '15:40:05', NULL, 0);
INSERT INTO `payment_order` VALUES (19, '202607041541574467407821', 7, '慢病管理 - 订阅', 27, 'SUBSCRIPTION', NULL, 50.00, 'EXPIRED', '2026-07-04', '15:41:57', NULL, 0);
INSERT INTO `payment_order` VALUES (20, '202607041548130570923263', 7, '手工课堂 - 续订', 23, 'RENEW', 1, 80.01, 'EXPIRED', '2026-07-04', '15:48:13', NULL, 0);
INSERT INTO `payment_order` VALUES (21, '202607041548169581357068', 7, '手工课堂 - 续订', 23, 'RENEW', 1, 80.01, 'SUCCESS', '2026-07-04', '15:48:17', '15:48:51', 0);
INSERT INTO `payment_order` VALUES (22, '202607041549018852113759', 7, '特殊饮食 - 续订', 21, 'RENEW', 2, 20.00, 'SUCCESS', '2026-07-04', '15:49:02', NULL, 0);
INSERT INTO `payment_order` VALUES (23, '202607041550070793405018', 7, '特殊饮食 - 续订', 21, 'RENEW', 1, 10.00, 'SUCCESS', '2026-07-04', '15:50:07', NULL, 0);
INSERT INTO `payment_order` VALUES (24, '202607041617349801945967', 7, '手工课堂 - 续订', 23, 'RENEW', 1, 80.01, 'SUCCESS', '2026-07-04', '16:17:35', '16:17:53', 0);
INSERT INTO `payment_order` VALUES (25, '202607041618312795555315', 7, '特殊饮食 - 续订', 21, 'RENEW', 2, 20.00, 'SUCCESS', '2026-07-04', '16:18:31', '16:18:45', 0);
INSERT INTO `payment_order` VALUES (26, '202607041626496020205484', 7, '特殊饮食 - 续订', 21, 'RENEW', 1, 10.00, 'SUCCESS', '2026-07-04', '16:26:50', '16:27:16', 0);
INSERT INTO `payment_order` VALUES (27, '202607042123116319561363', 13, '慢病管理 - 订阅', 28, 'SUBSCRIPTION', NULL, 50.00, 'EXPIRED', '2026-07-04', '21:23:12', NULL, 0);
INSERT INTO `payment_order` VALUES (28, '202607042125343965984523', 13, '健康档案 - 订阅', 29, 'SUBSCRIPTION', NULL, 50.00, 'SUCCESS', '2026-07-04', '21:25:34', '21:25:52', 0);
INSERT INTO `payment_order` VALUES (29, '202607042207556870458516', 13, '健康档案 - 续订', 29, 'RENEW', 1, 50.00, 'SUCCESS', '2026-07-04', '22:07:56', '22:08:09', 0);
INSERT INTO `payment_order` VALUES (30, '202607042234209504239327', 13, '健康档案 - 续订', 29, 'RENEW', 1, 50.00, 'SUCCESS', '2026-07-04', '22:34:21', '22:34:44', 0);
INSERT INTO `payment_order` VALUES (31, '202607042242134644800958', 13, '书法班 - 订阅', 30, 'SUBSCRIPTION', NULL, 80.00, 'SUCCESS', '2026-07-04', '22:42:13', '22:43:31', 0);
INSERT INTO `payment_order` VALUES (32, '202607042249037229262556', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'SUCCESS', '2026-07-04', '22:49:04', '22:49:51', 0);
INSERT INTO `payment_order` VALUES (33, '202607042253287933698754', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'SUCCESS', '2026-07-04', '22:53:29', '22:53:57', 0);
INSERT INTO `payment_order` VALUES (34, '202607042302366489776314', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'SUCCESS', '2026-07-04', '23:02:37', '23:02:52', 0);
INSERT INTO `payment_order` VALUES (35, '202607042311198225893995', 13, '书法班 - 续订', 30, 'RENEW', 1, 80.00, 'SUCCESS', '2026-07-04', '23:11:20', '23:12:12', 0);
INSERT INTO `payment_order` VALUES (36, '202607042316498094563915', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'SUCCESS', '2026-07-04', '23:16:50', '23:17:24', 0);
INSERT INTO `payment_order` VALUES (37, '202607042319477026955138', 13, '衣物洗涤 - 续订', 26, 'RENEW', 1, 100.00, 'SUCCESS', '2026-07-04', '23:19:48', '23:20:03', 0);
INSERT INTO `payment_order` VALUES (38, '202607051454362277900143', 13, '健康档案 - 续订', 29, 'RENEW', 2, 100.00, 'SUCCESS', '2026-07-05', '14:54:36', NULL, 0);
INSERT INTO `payment_order` VALUES (39, '202607061109561893962612', 2, '房间清洁 - 订阅', 31, 'SUBSCRIPTION', NULL, 0.00, 'SUCCESS', '2026-07-06', '11:09:56', NULL, 0);
INSERT INTO `payment_order` VALUES (40, '202607061111127700710941', 2, '棋牌活动 - 订阅', 32, 'SUBSCRIPTION', NULL, 0.00, 'SUCCESS', '2026-07-06', '11:11:13', NULL, 0);
INSERT INTO `payment_order` VALUES (41, '202607061111419085401548', 2, '房间清洁 - 订阅', 33, 'SUBSCRIPTION', NULL, 0.00, 'SUCCESS', '2026-07-06', '11:11:42', NULL, 0);
INSERT INTO `payment_order` VALUES (42, '202607061112384498109653', 2, '房间清洁 - 订阅', 34, 'SUBSCRIPTION', NULL, 0.01, 'SUCCESS', '2026-07-06', '11:12:38', NULL, 0);
INSERT INTO `payment_order` VALUES (43, '202607061114013800733042', 2, '房间清洁 - 订阅', 35, 'SUBSCRIPTION', NULL, 0.01, 'EXPIRED', '2026-07-06', '11:14:01', NULL, 0);
INSERT INTO `payment_order` VALUES (44, '202607061114485422642014', 2, '更换床单被褥 - 订阅', 36, 'SUBSCRIPTION', NULL, 10.00, 'SUCCESS', '2026-07-06', '11:14:49', '11:15:19', 0);
INSERT INTO `payment_order` VALUES (45, '202607061117439595537915', 2, '棋牌活动 - 订阅', 37, 'SUBSCRIPTION', NULL, 0.02, 'SUCCESS', '2026-07-06', '11:17:44', '11:18:14', 0);
INSERT INTO `payment_order` VALUES (46, '202607061120198922382054', 2, '房间清洁 - 订阅', 38, 'SUBSCRIPTION', NULL, 0.01, 'SUCCESS', '2026-07-06', '11:20:20', '11:20:47', 0);
INSERT INTO `payment_order` VALUES (47, '202607072247581605268707', 17, '房间清洁 - 订阅', 41, 'SUBSCRIPTION', NULL, 0.01, 'SUCCESS', '2026-07-07', '22:47:58', '22:48:29', 0);
INSERT INTO `payment_order` VALUES (48, '202607090858229019178011', 2, '理发修剪指甲 - 订阅', 42, 'SUBSCRIPTION', NULL, 50.00, 'SUCCESS', '2026-07-09', '08:58:23', '08:59:06', 0);
INSERT INTO `payment_order` VALUES (49, '202607101735155980377094', 2, '理发修剪指甲 - 续订', 42, 'RENEW', 2, 100.00, 'SUCCESS', '2026-07-10', '17:35:16', '17:35:56', 0);
INSERT INTO `payment_order` VALUES (50, '202607101809400648050886', 2, '理发修剪指甲 - 续订', 42, 'RENEW', 1, 50.00, 'PENDING', '2026-07-10', '18:09:40', NULL, 0);
INSERT INTO `payment_order` VALUES (51, '202607101810352952499039', 2, '理发修剪指甲 - 续订', 42, 'RENEW', 1, 50.00, 'PENDING', '2026-07-10', '18:10:35', NULL, 0);
INSERT INTO `payment_order` VALUES (52, '202607101812015773728365', 2, '个人卫生护理 - 订阅', 45, 'SUBSCRIPTION', NULL, 30.00, 'SUCCESS', '2026-07-10', '18:12:02', '18:12:31', 0);
INSERT INTO `payment_order` VALUES (53, '202607101812463767531637', 2, '高血压低盐餐 - 订阅', 46, 'SUBSCRIPTION', NULL, 100.00, 'SUCCESS', '2026-07-10', '18:12:46', '18:13:15', 0);

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：MENU-菜单 / BUTTON-按钮',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父权限ID',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由路径（type=MENU时）',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `back_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问网址路径',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_date` date NOT NULL,
  `create_time` time NOT NULL,
  `update_date` date NOT NULL,
  `update_time` time NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, '系统首页', 'dashboard:view', 'MENU', NULL, '/dashboard', 'Odometer', '/api/dashboard/**', 1, '2026-06-30', '23:34:06', '2026-06-30', '21:00:05');
INSERT INTO `permission` VALUES (2, '客户管理', 'customer:view', 'MENU', NULL, '', 'User', NULL, 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (3, '服务产品管理', 'service:view', 'MENU', NULL, '', 'Goods', NULL, 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (4, '膳食管理', 'meal:view', 'MENU', NULL, '', 'ForkSpoon', NULL, 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (5, '护理管理', 'care:view', 'MENU', NULL, '', 'FirstAidKit', NULL, 5, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (6, '管家管理', 'caregiver:view', 'MENU', 2, '/caregiver', 'UserFilled', NULL, 6, '2026-06-30', '23:34:06', '2026-07-05', '21:00:05');
INSERT INTO `permission` VALUES (7, '统计报表', 'report:view', 'MENU', 8, '/report', 'DataBoard', NULL, 7, '2026-06-30', '23:34:06', '2026-07-05', '21:00:05');
INSERT INTO `permission` VALUES (8, '系统管理', 'system:view', 'MENU', NULL, '', 'Setting', NULL, 8, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (9, '客户列表', 'customer:list', 'MENU', 2, '/customer', NULL, 'GET:/api/customer/page', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (10, '床位管理', 'bed:view', 'MENU', 2, '/customer/bed', NULL, 'GET:/api/bed/**', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (11, '入住管理', 'checkin:view', 'MENU', 2, '/customer/checkin', NULL, 'GET:/api/checkin/**', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (12, '外出管理', 'outrecord:view', 'MENU', 2, '/customer/outrecord', NULL, 'GET:/api/outrecord/**', 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (13, '服务订阅', 'subscription:view', 'MENU', 2, '/customer/subscription', NULL, 'GET:/api/customer-subscription/**', 5, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (14, '创建客户', 'customer:create', 'BUTTON', 9, NULL, NULL, 'POST:/api/customer', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (15, '编辑客户', 'customer:edit', 'BUTTON', 9, NULL, NULL, 'PUT:/api/customer/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (16, '删除客户', 'customer:delete', 'BUTTON', 9, NULL, NULL, 'DELETE:/api/customer/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (17, '启用/禁用客户', 'customer:status', 'BUTTON', 9, NULL, NULL, 'PUT:/api/customer/*/status', 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (18, '创建床位', 'bed:create', 'BUTTON', 10, NULL, NULL, 'POST:/api/bed', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (19, '编辑床位', 'bed:edit', 'BUTTON', 10, NULL, NULL, 'PUT:/api/bed/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (20, '删除床位', 'bed:delete', 'BUTTON', 10, NULL, NULL, 'DELETE:/api/bed/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (21, '办理入住', 'checkin:create', 'BUTTON', 11, NULL, NULL, 'POST:/api/checkin', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (22, '办理退住', 'checkin:checkout', 'BUTTON', 11, NULL, NULL, 'PUT:/api/checkout/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (23, '创建外出', 'outrecord:create', 'BUTTON', 12, NULL, NULL, 'POST:/api/outrecord', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (24, '服务分类', 'category:view', 'MENU', 3, '/service/category', NULL, 'GET:/api/service-category/**', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (25, '服务目录', 'catalog:view', 'MENU', 3, '/service/catalog', NULL, 'GET:/api/service-catalog/**', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (26, '创建分类', 'category:create', 'BUTTON', 24, NULL, NULL, 'POST:/api/service-category', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (27, '编辑分类', 'category:edit', 'BUTTON', 24, NULL, NULL, 'PUT:/api/service-category/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (28, '删除分类', 'category:delete', 'BUTTON', 24, NULL, NULL, 'DELETE:/api/service-category/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (29, '创建目录', 'catalog:create', 'BUTTON', 25, NULL, NULL, 'POST:/api/service-catalog', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (30, '编辑目录', 'catalog:edit', 'BUTTON', 25, NULL, NULL, 'PUT:/api/service-catalog/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (31, '删除目录', 'catalog:delete', 'BUTTON', 25, NULL, NULL, 'DELETE:/api/service-catalog/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (32, '膳食日历', 'meal:calendar', 'MENU', 4, '/meal', NULL, 'GET:/api/meal/**', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (33, '菜品管理', 'dish:view', 'MENU', 4, '/meal/dish', NULL, 'GET:/api/dish/**', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (34, '创建菜品', 'dish:create', 'BUTTON', 33, NULL, NULL, 'POST:/api/dish', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (35, '编辑菜品', 'dish:edit', 'BUTTON', 33, NULL, NULL, 'PUT:/api/dish/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (36, '删除菜品', 'dish:delete', 'BUTTON', 33, NULL, NULL, 'DELETE:/api/dish/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (37, '护理记录', 'care:record', 'MENU', 5, '/care/record', NULL, 'GET:/api/care/record/**', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (38, '护理等级', 'care:level', 'MENU', 5, '/care/level', NULL, 'GET:/api/carelevel/**', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (39, '护理项目', 'care:item', 'MENU', 5, '/care/item', NULL, 'GET:/api/care/item/**', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (40, '创建护理记录', 'care:record:create', 'BUTTON', 37, NULL, NULL, 'POST:/api/care/record', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (41, '创建等级', 'care:level:create', 'BUTTON', 38, NULL, NULL, 'POST:/api/carelevel', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (42, '编辑等级', 'care:level:edit', 'BUTTON', 38, NULL, NULL, 'PUT:/api/carelevel/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (43, '删除等级', 'care:level:delete', 'BUTTON', 38, NULL, NULL, 'DELETE:/api/carelevel/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (44, '创建项目', 'care:item:create', 'BUTTON', 39, NULL, NULL, 'POST:/api/care/item', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (45, '编辑项目', 'care:item:edit', 'BUTTON', 39, NULL, NULL, 'PUT:/api/care/item/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (46, '删除项目', 'care:item:delete', 'BUTTON', 39, NULL, NULL, 'DELETE:/api/care/item/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (47, '创建管家', 'caregiver:create', 'BUTTON', 6, NULL, NULL, 'POST:/api/admin/caregiver', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (48, '编辑管家', 'caregiver:edit', 'BUTTON', 6, NULL, NULL, 'PUT:/api/caregiver/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (49, '启用/禁用管家', 'caregiver:status', 'BUTTON', 6, NULL, NULL, 'PUT:/api/caregiver/*/status', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (50, '删除管家', 'caregiver:delete', 'BUTTON', 6, NULL, NULL, 'DELETE:/api/caregiver/*', 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (51, '管理员管理', 'admin:view', 'MENU', 8, '/system/admin', NULL, 'GET:/api/admin/**', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (52, '角色管理', 'role:view', 'MENU', 8, '/system/role', NULL, 'GET:/api/roles/**', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (53, '创建管理员', 'admin:create', 'BUTTON', 51, NULL, NULL, 'POST:/api/admin', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (54, '编辑管理员', 'admin:edit', 'BUTTON', 51, NULL, NULL, 'PUT:/api/admin/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (55, '启用/禁用管理员', 'admin:status', 'BUTTON', 51, NULL, NULL, 'PUT:/api/admin/*/status', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (56, '删除管理员', 'admin:delete', 'BUTTON', 51, NULL, NULL, 'DELETE:/api/admin/*', 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (57, '创建角色', 'role:create', 'BUTTON', 52, NULL, NULL, 'POST:/api/roles', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (58, '编辑角色', 'role:edit', 'BUTTON', 52, NULL, NULL, 'PUT:/api/roles/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (59, '删除角色', 'role:delete', 'BUTTON', 52, NULL, NULL, 'DELETE:/api/roles/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (60, '分配权限', 'role:assign', 'BUTTON', 52, NULL, NULL, 'PUT:/api/roles/*/permissions', 4, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (61, '权限管理', 'permission:view', 'MENU', 8, '/system/permission', NULL, 'GET:/api/permissions/**', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (62, '创建权限', 'permission:create', 'BUTTON', 61, NULL, NULL, 'POST:/api/permissions', 1, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (63, '编辑权限', 'permission:edit', 'BUTTON', 61, NULL, NULL, 'PUT:/api/permissions/*', 2, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (64, '删除权限', 'permission:delete', 'BUTTON', 61, NULL, NULL, 'DELETE:/api/permissions/*', 3, '2026-06-30', '23:34:06', '2026-07-01', '21:00:05');
INSERT INTO `permission` VALUES (68, '测试权限06', 'app:view', 'MENU', NULL, NULL, NULL, '/app/user', 1, '2026-07-10', '17:32:48', '2026-07-10', '17:32:48');
INSERT INTO `permission` VALUES (69, '测试权限06-01', 'app:view:app', 'MENU', 68, NULL, NULL, '/app/user/list', 0, '2026-07-10', '18:07:01', '2026-07-10', '18:07:01');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `is_system` tinyint NOT NULL DEFAULT 0 COMMENT '是否系统预置（1=不可删除）',
  `is_disabled` tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用(1=禁用,0=启用)',
  `create_date` date NOT NULL,
  `create_time` time NOT NULL,
  `update_date` date NOT NULL,
  `update_time` time NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', 'super_admin', '系统预置超级管理员，拥有全部权限', 1, 0, '2026-06-30', '23:34:06', '2026-06-30', '21:00:05');
INSERT INTO `role` VALUES (2, '管理员', 'admin', '普通管理员，权限可分配', 1, 0, '2026-06-30', '23:34:06', '2026-06-30', '21:00:05');
INSERT INTO `role` VALUES (3, '健康管家', 'caregiver', '健康管家，权限可分配', 1, 0, '2026-06-30', '23:34:06', '2026-06-30', '21:00:05');
INSERT INTO `role` VALUES (5, '测试角色02', 'aaa02', '测试角色02', 0, 0, '2026-06-30', '23:38:33', '2026-06-30', '21:00:05');
INSERT INTO `role` VALUES (6, '测试角色06', 'aabbcc', '测试', 0, 0, '2026-07-07', '22:49:44', '2026-07-07', '22:49:44');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 632 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (543, 1, 1);
INSERT INTO `role_permission` VALUES (544, 1, 2);
INSERT INTO `role_permission` VALUES (565, 1, 3);
INSERT INTO `role_permission` VALUES (574, 1, 4);
INSERT INTO `role_permission` VALUES (580, 1, 5);
INSERT INTO `role_permission` VALUES (560, 1, 6);
INSERT INTO `role_permission` VALUES (606, 1, 7);
INSERT INTO `role_permission` VALUES (591, 1, 8);
INSERT INTO `role_permission` VALUES (545, 1, 9);
INSERT INTO `role_permission` VALUES (550, 1, 10);
INSERT INTO `role_permission` VALUES (554, 1, 11);
INSERT INTO `role_permission` VALUES (557, 1, 12);
INSERT INTO `role_permission` VALUES (559, 1, 13);
INSERT INTO `role_permission` VALUES (546, 1, 14);
INSERT INTO `role_permission` VALUES (547, 1, 15);
INSERT INTO `role_permission` VALUES (548, 1, 16);
INSERT INTO `role_permission` VALUES (549, 1, 17);
INSERT INTO `role_permission` VALUES (551, 1, 18);
INSERT INTO `role_permission` VALUES (552, 1, 19);
INSERT INTO `role_permission` VALUES (553, 1, 20);
INSERT INTO `role_permission` VALUES (555, 1, 21);
INSERT INTO `role_permission` VALUES (556, 1, 22);
INSERT INTO `role_permission` VALUES (558, 1, 23);
INSERT INTO `role_permission` VALUES (566, 1, 24);
INSERT INTO `role_permission` VALUES (570, 1, 25);
INSERT INTO `role_permission` VALUES (567, 1, 26);
INSERT INTO `role_permission` VALUES (568, 1, 27);
INSERT INTO `role_permission` VALUES (569, 1, 28);
INSERT INTO `role_permission` VALUES (571, 1, 29);
INSERT INTO `role_permission` VALUES (572, 1, 30);
INSERT INTO `role_permission` VALUES (573, 1, 31);
INSERT INTO `role_permission` VALUES (575, 1, 32);
INSERT INTO `role_permission` VALUES (576, 1, 33);
INSERT INTO `role_permission` VALUES (577, 1, 34);
INSERT INTO `role_permission` VALUES (578, 1, 35);
INSERT INTO `role_permission` VALUES (579, 1, 36);
INSERT INTO `role_permission` VALUES (581, 1, 37);
INSERT INTO `role_permission` VALUES (583, 1, 38);
INSERT INTO `role_permission` VALUES (587, 1, 39);
INSERT INTO `role_permission` VALUES (582, 1, 40);
INSERT INTO `role_permission` VALUES (584, 1, 41);
INSERT INTO `role_permission` VALUES (585, 1, 42);
INSERT INTO `role_permission` VALUES (586, 1, 43);
INSERT INTO `role_permission` VALUES (588, 1, 44);
INSERT INTO `role_permission` VALUES (589, 1, 45);
INSERT INTO `role_permission` VALUES (590, 1, 46);
INSERT INTO `role_permission` VALUES (561, 1, 47);
INSERT INTO `role_permission` VALUES (562, 1, 48);
INSERT INTO `role_permission` VALUES (563, 1, 49);
INSERT INTO `role_permission` VALUES (564, 1, 50);
INSERT INTO `role_permission` VALUES (592, 1, 51);
INSERT INTO `role_permission` VALUES (597, 1, 52);
INSERT INTO `role_permission` VALUES (593, 1, 53);
INSERT INTO `role_permission` VALUES (594, 1, 54);
INSERT INTO `role_permission` VALUES (595, 1, 55);
INSERT INTO `role_permission` VALUES (596, 1, 56);
INSERT INTO `role_permission` VALUES (598, 1, 57);
INSERT INTO `role_permission` VALUES (599, 1, 58);
INSERT INTO `role_permission` VALUES (600, 1, 59);
INSERT INTO `role_permission` VALUES (601, 1, 60);
INSERT INTO `role_permission` VALUES (602, 1, 61);
INSERT INTO `role_permission` VALUES (603, 1, 62);
INSERT INTO `role_permission` VALUES (604, 1, 63);
INSERT INTO `role_permission` VALUES (605, 1, 64);
INSERT INTO `role_permission` VALUES (435, 2, 1);
INSERT INTO `role_permission` VALUES (436, 2, 2);
INSERT INTO `role_permission` VALUES (457, 2, 3);
INSERT INTO `role_permission` VALUES (466, 2, 4);
INSERT INTO `role_permission` VALUES (472, 2, 5);
INSERT INTO `role_permission` VALUES (452, 2, 6);
INSERT INTO `role_permission` VALUES (484, 2, 7);
INSERT INTO `role_permission` VALUES (483, 2, 8);
INSERT INTO `role_permission` VALUES (437, 2, 9);
INSERT INTO `role_permission` VALUES (442, 2, 10);
INSERT INTO `role_permission` VALUES (446, 2, 11);
INSERT INTO `role_permission` VALUES (449, 2, 12);
INSERT INTO `role_permission` VALUES (451, 2, 13);
INSERT INTO `role_permission` VALUES (438, 2, 14);
INSERT INTO `role_permission` VALUES (439, 2, 15);
INSERT INTO `role_permission` VALUES (440, 2, 16);
INSERT INTO `role_permission` VALUES (441, 2, 17);
INSERT INTO `role_permission` VALUES (443, 2, 18);
INSERT INTO `role_permission` VALUES (444, 2, 19);
INSERT INTO `role_permission` VALUES (445, 2, 20);
INSERT INTO `role_permission` VALUES (447, 2, 21);
INSERT INTO `role_permission` VALUES (448, 2, 22);
INSERT INTO `role_permission` VALUES (450, 2, 23);
INSERT INTO `role_permission` VALUES (458, 2, 24);
INSERT INTO `role_permission` VALUES (462, 2, 25);
INSERT INTO `role_permission` VALUES (459, 2, 26);
INSERT INTO `role_permission` VALUES (460, 2, 27);
INSERT INTO `role_permission` VALUES (461, 2, 28);
INSERT INTO `role_permission` VALUES (463, 2, 29);
INSERT INTO `role_permission` VALUES (464, 2, 30);
INSERT INTO `role_permission` VALUES (465, 2, 31);
INSERT INTO `role_permission` VALUES (467, 2, 32);
INSERT INTO `role_permission` VALUES (468, 2, 33);
INSERT INTO `role_permission` VALUES (469, 2, 34);
INSERT INTO `role_permission` VALUES (470, 2, 35);
INSERT INTO `role_permission` VALUES (471, 2, 36);
INSERT INTO `role_permission` VALUES (473, 2, 37);
INSERT INTO `role_permission` VALUES (475, 2, 38);
INSERT INTO `role_permission` VALUES (479, 2, 39);
INSERT INTO `role_permission` VALUES (474, 2, 40);
INSERT INTO `role_permission` VALUES (476, 2, 41);
INSERT INTO `role_permission` VALUES (477, 2, 42);
INSERT INTO `role_permission` VALUES (478, 2, 43);
INSERT INTO `role_permission` VALUES (480, 2, 44);
INSERT INTO `role_permission` VALUES (481, 2, 45);
INSERT INTO `role_permission` VALUES (482, 2, 46);
INSERT INTO `role_permission` VALUES (453, 2, 47);
INSERT INTO `role_permission` VALUES (454, 2, 48);
INSERT INTO `role_permission` VALUES (455, 2, 49);
INSERT INTO `role_permission` VALUES (456, 2, 50);
INSERT INTO `role_permission` VALUES (410, 3, 1);
INSERT INTO `role_permission` VALUES (411, 3, 2);
INSERT INTO `role_permission` VALUES (416, 3, 3);
INSERT INTO `role_permission` VALUES (423, 3, 5);
INSERT INTO `role_permission` VALUES (434, 3, 7);
INSERT INTO `role_permission` VALUES (412, 3, 9);
INSERT INTO `role_permission` VALUES (413, 3, 14);
INSERT INTO `role_permission` VALUES (414, 3, 16);
INSERT INTO `role_permission` VALUES (415, 3, 17);
INSERT INTO `role_permission` VALUES (417, 3, 24);
INSERT INTO `role_permission` VALUES (421, 3, 25);
INSERT INTO `role_permission` VALUES (418, 3, 26);
INSERT INTO `role_permission` VALUES (419, 3, 27);
INSERT INTO `role_permission` VALUES (420, 3, 28);
INSERT INTO `role_permission` VALUES (422, 3, 30);
INSERT INTO `role_permission` VALUES (424, 3, 37);
INSERT INTO `role_permission` VALUES (426, 3, 38);
INSERT INTO `role_permission` VALUES (430, 3, 39);
INSERT INTO `role_permission` VALUES (425, 3, 40);
INSERT INTO `role_permission` VALUES (427, 3, 41);
INSERT INTO `role_permission` VALUES (428, 3, 42);
INSERT INTO `role_permission` VALUES (429, 3, 43);
INSERT INTO `role_permission` VALUES (431, 3, 44);
INSERT INTO `role_permission` VALUES (432, 3, 45);
INSERT INTO `role_permission` VALUES (433, 3, 46);
INSERT INTO `role_permission` VALUES (611, 5, 1);
INSERT INTO `role_permission` VALUES (612, 5, 2);
INSERT INTO `role_permission` VALUES (616, 5, 3);
INSERT INTO `role_permission` VALUES (619, 5, 4);
INSERT INTO `role_permission` VALUES (621, 5, 7);
INSERT INTO `role_permission` VALUES (620, 5, 8);
INSERT INTO `role_permission` VALUES (613, 5, 9);
INSERT INTO `role_permission` VALUES (614, 5, 14);
INSERT INTO `role_permission` VALUES (615, 5, 15);
INSERT INTO `role_permission` VALUES (617, 5, 24);
INSERT INTO `role_permission` VALUES (618, 5, 25);
INSERT INTO `role_permission` VALUES (622, 6, 2);
INSERT INTO `role_permission` VALUES (629, 6, 3);
INSERT INTO `role_permission` VALUES (628, 6, 6);
INSERT INTO `role_permission` VALUES (623, 6, 9);
INSERT INTO `role_permission` VALUES (624, 6, 10);
INSERT INTO `role_permission` VALUES (625, 6, 11);
INSERT INTO `role_permission` VALUES (626, 6, 12);
INSERT INTO `role_permission` VALUES (627, 6, 13);
INSERT INTO `role_permission` VALUES (630, 6, 25);
INSERT INTO `role_permission` VALUES (631, 6, 30);

-- ----------------------------
-- Table structure for service_catalog
-- ----------------------------
DROP TABLE IF EXISTS `service_catalog`;
CREATE TABLE `service_catalog`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务描述',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '定价',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'month' COMMENT '计价单位：once/day/week/month/year/long',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '是否上架 1-上架 0-下架',
  `create_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `create_time` time NULL DEFAULT NULL COMMENT '创建时间',
  `update_date` date NULL DEFAULT NULL COMMENT '修改日期',
  `update_time` time NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务产品目录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_catalog
-- ----------------------------
INSERT INTO `service_catalog` VALUES (1, 1, '房间清洁', '每周一次深度清洁', 0.01, 'long', 1, NULL, NULL, '2026-06-25', '21:16:47', 0);
INSERT INTO `service_catalog` VALUES (2, 1, '衣物洗涤', '每周两次衣物清洗', 100.00, 'month', 1, NULL, NULL, '2026-07-01', '22:02:49', 0);
INSERT INTO `service_catalog` VALUES (3, 2, '常规体检', '每月一次常规健康检查', 30.00, 'month', 1, NULL, NULL, '2026-07-02', '10:11:32', 0);
INSERT INTO `service_catalog` VALUES (4, 2, '慢病管理', '高血压/糖尿病等慢性病跟踪管理', 50.00, 'week', 1, NULL, NULL, '2026-07-02', '09:24:21', 0);
INSERT INTO `service_catalog` VALUES (5, 3, '定制营养餐', '根据个人健康状况定制每日三餐', 90.00, 'month', 1, NULL, NULL, '2026-07-02', '10:11:26', 0);
INSERT INTO `service_catalog` VALUES (6, 3, '特殊饮食', '流食/软食等特殊饮食需求', 10.00, 'month', 1, NULL, NULL, '2026-07-02', '10:11:20', 0);
INSERT INTO `service_catalog` VALUES (7, 4, '健康档案', '建立并维护个人电子健康档案', 50.00, 'month', 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `service_catalog` VALUES (8, 4, '心理咨询', '每周一次专业心理咨询服务', 40.00, 'day', 1, NULL, NULL, '2026-07-01', '00:01:32', 0);
INSERT INTO `service_catalog` VALUES (9, 5, '手工课堂', '每周一次手工兴趣活动', 80.01, 'week', 1, NULL, NULL, '2026-06-26', '09:21:07', 0);
INSERT INTO `service_catalog` VALUES (10, 5, '书法班', '每周一次书法教学活动', 80.00, 'month', 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `service_catalog` VALUES (11, 5, '生活照料0200', '假设这是一个服务02', 100.01, 'day', 1, '2026-06-26', '09:06:17', '2026-06-26', '09:06:59', 1);
INSERT INTO `service_catalog` VALUES (12, 1, '生活服务06', '111', 20.01, 'once', 1, '2026-06-26', '09:20:31', '2026-06-26', '09:20:48', 0);
INSERT INTO `service_catalog` VALUES (13, 1, '个人卫生护理', '协助洗漱、口腔清洁、面部清洁等', 30.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (14, 1, '翻身拍背服务', '定时翻身、拍背促进血液循环', 15.00, 'day', 0, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (15, 1, '协助行走/移动', '辅助老人行走、轮椅转移等', 20.00, 'day', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (16, 1, '沐浴服务', '专业助浴，包含洗头、擦身', 40.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (17, 1, '理发修剪指甲', '每月一次理发及指甲修剪', 50.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (18, 1, '更换床单被褥', '定期更换清洗床上用品', 10.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (19, 2, '康复理疗', '物理治疗、关节活动训练', 60.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (20, 2, '用药管理', '按时发药、用药提醒与记录', 30.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (21, 2, '血糖监测', '每日空腹及餐后血糖检测', 10.00, 'day', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (22, 2, '压疮护理', '压疮预防与伤口换药处理', 50.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (23, 2, '鼻饲护理', '鼻饲管置管与喂养护理', 40.00, 'day', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (24, 2, '导尿管护理', '导尿管维护与尿袋更换', 30.00, 'day', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (25, 3, '糖尿病定制餐', '低糖、控糖营养套餐（每日三餐）', 120.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (26, 3, '高血压低盐餐', '低盐、低脂健康膳食', 100.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (27, 3, '流质饮食', '全流质或半流质食物', 80.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (28, 3, '碎食/软食', '软烂易咀嚼食物', 80.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (29, 3, '营养滋补汤', '药膳或高蛋白汤品（每份）', 15.00, 'once', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (30, 3, '节日特色餐', '传统节日定制套餐（每份）', 20.00, 'once', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (31, 4, '健康知识讲座', '定期开展老年健康科普讲座', 0.00, 'once', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (32, 4, '心理疏导咨询', '一对一心理咨询与情绪支持', 60.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (33, 4, '认知训练', '记忆力、注意力等认知功能训练', 50.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (34, 4, '音乐疗法', '通过音乐放松身心、改善情绪', 40.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (35, 4, '芳香疗法', '使用天然精油舒缓压力', 30.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (36, 4, '睡眠管理指导', '改善睡眠习惯与环境调整', 20.00, 'month', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (37, 5, '棋牌活动', '象棋、围棋、扑克等休闲娱乐', 0.01, 'day', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (38, 5, '歌唱兴趣小组', '合唱、独唱及声乐交流', 20.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (39, 5, '电影欣赏', '每周放映经典影片', 10.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (40, 5, '园艺种植活动', '盆栽、花草种植与养护', 30.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (41, 5, '太极养生课', '太极拳、八段锦教学', 40.00, 'week', 1, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (42, 5, '手工编织课', '编织、刺绣等手工技艺', 50.00, 'week', 0, '2026-07-01', '08:00:00', '2026-07-04', '10:00:00', 0);
INSERT INTO `service_catalog` VALUES (43, 4, '健康管理测试05', '', 10.00, 'month', 1, '2026-07-06', '21:03:12', NULL, NULL, 0);
INSERT INTO `service_catalog` VALUES (44, 8, '测试服务077', '', 0.01, 'month', 1, '2026-07-07', '22:43:33', '2026-07-07', '22:43:47', 1);
INSERT INTO `service_catalog` VALUES (45, 7, '测试服务06', '测试服务', 11.00, 'month', 1, '2026-07-10', '17:29:25', NULL, NULL, 0);
INSERT INTO `service_catalog` VALUES (46, 7, '测试服务项目11', '', 100.00, 'year', 0, '2026-07-10', '18:02:05', '2026-07-10', '18:02:17', 1);

-- ----------------------------
-- Table structure for service_category
-- ----------------------------
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_category
-- ----------------------------
INSERT INTO `service_category` VALUES (1, '生活照料', 1, 0);
INSERT INTO `service_category` VALUES (2, '医疗护理', 2, 0);
INSERT INTO `service_category` VALUES (3, '膳食服务', 3, 0);
INSERT INTO `service_category` VALUES (4, '健康管理', 4, 0);
INSERT INTO `service_category` VALUES (5, '文娱活动', 5, 0);
INSERT INTO `service_category` VALUES (6, '服务类型06', 6, 1);
INSERT INTO `service_category` VALUES (7, '测试服务目录05', 6, 0);
INSERT INTO `service_category` VALUES (8, '测试目录07', 7, 1);

SET FOREIGN_KEY_CHECKS = 1;
