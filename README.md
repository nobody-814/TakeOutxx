# 🍔 TakeOutxx — 外卖配送平台

基于 **Spring Boot 3.5 + MyBatis + MySQL + Redis** 的多角色外卖点餐与配送系统后端。

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 3.5.6 |
| 安全认证 | Spring Security + JWT (jjwt 0.11.5) |
| 持久层 | MyBatis 3.0.3 + PageHelper 分页 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis + Redisson 3.23.5（分布式锁） |
| JSON | FastJSON 2.0.21 |
| AI | Spring AI 1.0.0-M6 + DeepSeek |
| 工具 | Lombok、Commons Pool2 |
| 构建 | Maven Wrapper |
| JDK | 17 |

## 项目结构

```
src/main/java/org/example/
├── TakeOutxxApplication.java    # 启动类
├── Common/
│   ├── CacheService.java        # Redis 缓存服务（穿透/击穿/雪崩防护）
│   └── RedisConstant.java       # Redis 键常量
├── config/
│   ├── SecurityConfig.java      # Spring Security 核心配置
│   ├── JwtAuthenticationFilter.java  # JWT 无状态认证过滤器
│   ├── JwtProperties.java       # JWT 配置属性（密钥/过期时间等）
│   ├── RoleRedirectProperties.java   # 角色登录跳转路由
│   ├── RedisConfig.java         # Redis 序列化 & Redisson 配置
│   └── WebMvcConfig.java        # Web MVC 配置
├── controller/                  # 接口层（10 个 Controller）
│   ├── UserController           # 注册/登录/个人信息
│   ├── MerchantController       # 商家入驻/店铺管理
│   ├── CategoryController       # 商品分类
│   ├── ProductController        # 商品上下架
│   ├── CartController           # 购物车
│   ├── OrderController          # 订单/支付/状态流转
│   ├── OrderItemController      # 订单明细
│   ├── RiderController          # 骑手认证/接单/配送
│   ├── ReviewController         # 评价
│   └── AiController             # AI 智能问答（DeepSeek）
├── service/                     # 业务接口层
│   └── impl/                    # 业务实现（对应 9 个 Service）
├── mapper/                      # MyBatis Mapper（9 个）
├── domain/                      # 实体类（10 个）
├── exception/                   # 全局异常处理
└── Utils/
    ├── JwtUtils.java            # JWT 签发/校验工具
    └── SecurityUtil.java        # 获取当前登录用户
```

## 角色体系

系统通过 `user.role` 字段区分三种身份，JWT 中携带角色信息，Spring Security 自动完成权限映射：

| role | 身份 | 角色权限 | 登录后跳转 |
|------|------|----------|-----------|
| 0 | 消费者 | ROLE_USER | /consumer/home |
| 1 | 商家 | ROLE_MERCHANT | /merchant/home |
| 2 | 骑手 | ROLE_RIDER | /rider/home |

## 功能模块

### 用户端（消费者）
- 注册/登录（手机号或用户名 + 密码，BCrypt 加密）
- 用户名智能生成（未填写时自动生成 `phone_xxx`）
- 浏览商家列表（分页 + 搜索 + 仅营业中）、按分类查看商品
- 购物车管理（按商家隔离，增/删/改数量/清空）
- 创建订单、模拟支付
- 订单历史查询、取消未完成订单
- 个人信息编辑（用户名/手机号/地址/头像/密码）

### 商家端
- 商家入驻、店铺信息编辑
- 营业状态切换（未营业/营业中/休息/封禁）
- 商品分类管理（同商家分类名唯一）
- 商品上架/下架/删除
- 订单管理（查看本店订单 + 按状态筛选 + 状态更新）
- 销售统计（订单总数、总销售额）

### 骑手端
- 骑手认证入驻（提交身份证）
- 在线/离线/配送中 状态切换
- 查看待接订单池
- 抢单（抢单后自动变配送中）
- 当前配送订单、完成配送
- 配送历史查询
- 骑手评分

### AI 助手
- 接入 DeepSeek 大模型，提供智能对话/推荐服务

### 评价
- 消费者可对商家、商品、骑手分别评价打分（type 区分）

## 订单状态机

```
待支付(0) ──支付──▶ 已支付待接单(1) ──骑手抢单──▶ 配送中(3) ──送达──▶ 已完成(4)
    │                                       │
    └────────────── 取消(5) ◀───────────────┘
```

状态流转均校验前置状态合法性，防止非法状态跳转。

## 权限设计

采用 **Spring Security + JWT 无状态认证**，不依赖 Session。

| 接口类型 | 示例 | 权限控制方式 |
|----------|------|-------------|
| **公开接口** | 登录、注册、商家列表、商品列表 | `permitAll()` |
| **消费者接口** | 下单、支付、购物车、评价 | `authenticated()`（登录即可） |
| **商家接口** | 开店、管理商品、查看本店订单 | `hasRole("MERCHANT")` |
| **骑手接口** | 入驻、抢单、配送完成 | `hasRole("RIDER")` |

认证流程：
```
请求 → JwtAuthenticationFilter（提取 token 头 → 验签 → 查库 → 注入 SecurityContext）
     → SecurityFilterChain（角色权限校验）
     → Controller → SecurityUtil.getCurrentUser() 获取当前用户
```

## 数据库表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | role(0消费者/1商家/2骑手), status, address |
| `merchant` | 商家表 | shop_name, address, rating, status, user_id(外键) |
| `rider` | 骑手表 | id_card, delivery_scope, status, rating, user_id(外键) |
| `category` | 商品分类 | merchant_id(外键), name(联合唯一), sort |
| `product` | 商品表 | merchant_id, category_id, name, price, status |
| `cart` | 购物车 | user_id, product_id, merchant_id, quantity |
| `order` | 订单表 | user_id, merchant_id, rider_id, status(0-5), 时间节点 |
| `orderitem` | 订单明细 | order_id, product_id, quantity, total_price |
| `review` | 评价表 | order_id, type(1商家/2商品/3骑手), rating, content |

## 缓存架构亮点

`CacheService` 封装了 Redis 通用查询，内置三层防护：

- **缓存穿透**：查不到的 key 缓存空值标记（60s TTL）
- **缓存击穿**：分布式锁（`setIfAbsent`）+ 双重检查 + 锁释放校验
- **缓存雪崩**：TTL 叠加 0~20% 随机偏移量

## 快速启动

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7+
- Maven 3.6+

### 步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS TakeOutxx DEFAULT CHARSET utf8mb4;"

# 2. 导入表结构
mysql -u root -p TakeOutxx < database.sql

# 3. 配置环境变量（IDEA: Run → Edit Configurations → Environment variables）
#    DB_PASSWORD=你的数据库密码
#    JWT_SECRET=你的JWT密钥（至少32字节）
#    AI_API_KEY=你的DeepSeek API密钥

# 4. 启动
mvnw spring-boot:run

# 后端运行在 http://localhost:8080
```

## API 概览

### 用户
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/takeout/user/register` | 注册 | 公开 |
| POST | `/takeout/user/login` | 登录 | 公开 |
| GET | `/takeout/user/profile` | 个人信息 | 登录 |
| POST | `/takeout/user/updateInfo` | 修改信息 | 登录 |
| POST | `/takeout/user/updatePassword` | 修改密码 | 登录 |

### 商家
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/takeout/merchant/create` | 入驻开店 | MERCHANT |
| GET | `/takeout/merchant/validList` | 在营商家列表 | 公开 |
| GET | `/takeout/merchant/detail/{id}` | 商家详情 | 公开 |
| GET | `/takeout/merchant/page` | 分页搜索商家 | 公开 |
| GET | `/takeout/merchant/myShop` | 我的店铺 | MERCHANT |

### 分类 & 商品
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/takeout/category/list/{merchantId}` | 商家分类列表 | 公开 |
| POST | `/takeout/category/add` | 添加分类 | MERCHANT |
| GET | `/takeout/product/list/{merchantId}` | 商品列表 | 公开 |
| GET | `/takeout/product/detail/{id}` | 商品详情 | 公开 |
| POST | `/takeout/product/add` | 添加商品 | MERCHANT |

### 购物车
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/takeout/cart/add` | 加入购物车 | 登录 |
| GET | `/takeout/cart/list/{merchantId}` | 购物车列表 | 登录 |
| POST | `/takeout/cart/update` | 修改数量 | 登录 |
| POST | `/takeout/cart/clear/{merchantId}` | 清空购物车 | 登录 |

### 订单
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/takeout/order/create` | 创建订单 | 登录 |
| POST | `/takeout/order/pay/{id}` | 支付订单 | 登录 |
| GET | `/takeout/order/myOrders` | 我的订单 | 登录 |
| GET | `/takeout/order/merchantOrders` | 本店订单 | MERCHANT |
| GET | `/takeout/order/merchant/stats` | 销售统计 | MERCHANT |
| POST | `/takeout/order/cancel/{id}` | 取消订单 | 登录 |

### 骑手
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/takeout/rider/apply` | 骑手入驻 | 登录 |
| POST | `/takeout/rider/status` | 切换在线状态 | RIDER |
| GET | `/takeout/rider/online/list` | 在线骑手列表 | 公开 |
| POST | `/takeout/rider/takeOrder` | 抢单 | RIDER |
| POST | `/takeout/rider/completeOrder` | 完成配送 | RIDER |
| GET | `/takeout/rider/orders` | 配送历史 | RIDER |
