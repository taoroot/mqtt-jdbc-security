/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : activemq

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 05/07/2019 23:07:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for iot_device
-- ----------------------------
DROP TABLE IF EXISTS `iot_device`;
CREATE TABLE `iot_device`  (
  `id` int(11) NOT NULL COMMENT '平台唯一id',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `did` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '硬件唯一标识，一般用imei',
  `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认自动生成，用户可手动编辑',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '通讯密码',
  `update_time` datetime(0) NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '激活码，第三方平台激活以及调用接口使用',
  `state` int(1) NULL DEFAULT 1 COMMENT '0在线，1下线',
  `last_online_time` datetime(0) NULL DEFAULT NULL COMMENT '最后在线',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device
-- ----------------------------
INSERT INTO `iot_device` VALUES (1, 2, '1', '123456', '2019-07-03 09:18:19', NULL, NULL, 1, NULL);
INSERT INTO `iot_device` VALUES (2, 2, '2', '123456', '2019-07-05 11:55:16', '2019-07-05 11:55:16', NULL, 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;
