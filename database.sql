-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: TakeOutxx
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `merchant_id` int NOT NULL COMMENT '所属商家ID（关联Merchant表）',
  `name` varchar(50) NOT NULL COMMENT '分类名称（如"汉堡"）',
  `sort` int DEFAULT '0' COMMENT '排序（数字越小越靠前）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_category` (`merchant_id`,`name`) COMMENT '同一商家分类名唯一',
  CONSTRAINT `category_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `user_id` int NOT NULL COMMENT '关联User表的商家用户ID（role=1）',
  `shop_name` varchar(100) NOT NULL COMMENT '店铺名称',
  `address` varchar(255) NOT NULL COMMENT '店铺地址（用于配送范围）',
  `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间（如"09:00-22:00"）',
  `avatar` varchar(255) DEFAULT NULL COMMENT '店铺头像',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '店铺评分（0-5分）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '店铺状态：0-未营业，1-营业中，2-休息,3-封禁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_status` (`status`) COMMENT '按营业状态查询商家',
  CONSTRAINT `merchant_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` varchar(50) NOT NULL COMMENT '订单号（如"ORD20231022001"）',
  `user_id` int NOT NULL COMMENT '下单客户ID（关联User表）',
  `merchant_id` int NOT NULL COMMENT '所属商家ID',
  `rider_id` int DEFAULT NULL COMMENT '接单骑手ID（可为NULL，未接单时）',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额（含优惠）',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `address` varchar(255) NOT NULL COMMENT '配送地址',
  `phone` varchar(20) NOT NULL COMMENT '收件人电话',
  `receiver` varchar(50) NOT NULL COMMENT '收件人姓名',
  `status` tinyint NOT NULL COMMENT '订单状态：0-待支付，1-已支付待接单，2-商家已接单，3-骑手已取餐，4-已送达，5-已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `accept_time` datetime DEFAULT NULL COMMENT '商家接单时间',
  `pick_time` datetime DEFAULT NULL COMMENT '骑手取餐时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  PRIMARY KEY (`id`),
  KEY `rider_id` (`rider_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_merchant` (`merchant_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `order_ibfk_3` FOREIGN KEY (`rider_id`) REFERENCES `rider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitem`
--

DROP TABLE IF EXISTS `orderitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitem` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` varchar(50) NOT NULL COMMENT '关联订单号',
  `product_id` int NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称（下单时快照，避免商品改名）',
  `product_price` decimal(10,2) NOT NULL COMMENT '下单时的单价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '小计金额（单价*数量）',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  CONSTRAINT `orderitem_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitem`
--

LOCK TABLES `orderitem` WRITE;
/*!40000 ALTER TABLE `orderitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `merchant_id` int NOT NULL COMMENT '所属商家',
  `category_id` int NOT NULL COMMENT '所属分类',
  `name` varchar(100) NOT NULL COMMENT '商品名称（如"经典牛肉汉堡"）',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价（用于显示折扣）',
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片URL',
  `description` text COMMENT '商品描述',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存（0表示无限库存）',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `sort` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `idx_merchant_status` (`merchant_id`,`status`) COMMENT '查询商家的上架商品',
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON DELETE CASCADE,
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rider`
--

DROP TABLE IF EXISTS `rider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rider` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '骑手ID',
  `user_id` int NOT NULL COMMENT '关联User表的骑手用户ID（role=2）',
  `id_card` varchar(20) NOT NULL COMMENT '身份证号（认证用）',
  `delivery_scope` varchar(255) DEFAULT NULL COMMENT '常配送范围（如"朝阳区建国路"）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '骑手状态：0-离线，1-在线可接单，2-配送中',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '骑手评分',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `id_card` (`id_card`),
  KEY `idx_status` (`status`) COMMENT '查询在线骑手',
  CONSTRAINT `rider_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='骑手信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rider`
--

LOCK TABLES `rider` WRITE;
/*!40000 ALTER TABLE `rider` DISABLE KEYS */;
/*!40000 ALTER TABLE `rider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（显示用）',
  `password` varchar(255) NOT NULL COMMENT '密码哈希（建议加密存储，如bcrypt）',
  `phone_number` varchar(20) NOT NULL COMMENT '手机号（登录/联系用，唯一）',
  `role` tinyint NOT NULL COMMENT '角色：0-客户，1-商家，2-骑手',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用，1-正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone_number` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表（含客户、商家、骑手）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-22 15:31:29
