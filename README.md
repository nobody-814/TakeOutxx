# 🍔 美味速达 — 外卖配送平台

基于 Spring Boot + MyBatis + MySQL 的多角色外卖点餐与配送系统后端。

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 3.5 |
| 持久层 | MyBatis（注解式 SQL） |
| 数据库 | MySQL |
| 安全认证 | Spring Security + JWT |
| 构建工具 | Maven |
| 前端 | Vue3（独立仓库，由团队成员负责） |

## 项目结构

```
src/main/java/org/example/
├── controller/          # 接口层
│   ├── UserController        # 用户注册登录、个人信息
│   ├── MerchantController    # 商家入驻、店铺管理
│   ├── CategoryController    # 商品分类增删查
│   ├── ProductController     # 商品上下架、列表
│   ├── CartController        # 购物车增删查
│   ├── OrderController       # 订单创建、支付、状态流转
│   ├── OrderItemController   # 订单明细
│   └── RiderController       # 骑手认证、接单、配送历史
├── service/             # 业务层
│   ├── impl/                 # 业务实现
├── mapper/              # 数据访问层（MyBatis 注解 SQL）
├── domain/              # 实体类
├── config/              # 配置（Spring Security、拦截器、CORS）
├── interceptor/         # 登录拦截器
└── Utils/               # JWT 工具类
```

## 功能模块

### 用户端（顾客）
- 注册登录（手机号/用户名 + 密码，JWT Token 认证）
- 浏览店铺列表、按分类查看商品
- 购物车管理（添加/删除/修改数量）
- 提交订单、订单支付（模拟）
- 查看个人订单历史与详情
- 个人信息编辑（用户名、手机号、收货地址）

### 商家端
- 商家入驻、店铺信息编辑、营业状态切换
- 商品分类管理（名称唯一性校验）
- 商品上架/下架/删除
- 订单管理（查看订单、接单）
- 销售统计（总销售额、订单数）

### 骑手端
- 骑手认证、在线/离线状态切换
- 待接订单池、抢单
- 当前配送订单、手动完成配送
- 配送历史查询

## 订单状态机

```
待支付(0) → 已支付(1) → 商家接单(2) → 配送中(3) → 已完成(4)
                                    ↓
                                已取消(5)
```

每次状态变更均校验前置状态合法性。

## 权限设计

| 接口类型 | 是否需要登录 | 示例 |
|----------|-------------|------|
| 公开接口 | 否 | 店铺列表、分类列表、商品列表 |
| 受保护接口 | 是 | 下单、接单、个人信息修改 |

通过 `LoginInterceptor` 统一拦截，从请求头提取 Token 校验，公开路径在 `WebMvcConfig` 中配置白名单放行。

## 数据库表

| 表名 | 说明 |
|------|------|
| user | 用户（顾客/商家/骑手） |
| merchant | 商家店铺 |
| rider | 骑手信息 |
| category | 商品分类（uk: merchant_id + name） |
| product | 商品 |
| cart | 购物车 |
| order | 订单 |
| order_item | 订单明细 |

## 快速启动

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 步骤
```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS TakeOutxx DEFAULT CHARSET utf8mb4;"

# 2. 修改 application.yml 中的数据库密码

# 3. 启动项目
mvn spring-boot:run

# 4. 访问（前端需另启）
# 后端运行在 http://localhost:8080
```

### 配置文件
`src/main/resources/application.yml`
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/TakeOutxx?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码

jwt:
  secret: 你的密钥
  expiration: 3600000
```

## API 概览

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | POST | `/takeout/user/login` | 登录 |
| 用户 | POST | `/takeout/user/register` | 注册 |
| 用户 | GET | `/takeout/user/profile` | 个人信息 |
| 用户 | POST | `/takeout/user/updateInfo` | 修改信息 |
| 商家 | POST | `/takeout/merchant/create` | 入驻 |
| 商家 | GET | `/takeout/merchant/validList` | 店铺列表（公开） |
| 分类 | POST | `/takeout/category/add` | 添加分类 |
| 分类 | GET | `/takeout/category/list/{merchantId}` | 分类列表（公开） |
| 商品 | POST | `/takeout/product/add` | 添加商品 |
| 商品 | GET | `/takeout/product/list/{merchantId}` | 商品列表（公开） |
| 订单 | POST | `/takeout/order/create` | 创建订单 |
| 订单 | POST | `/takeout/order/pay/{id}` | 支付 |
| 订单 | GET | `/takeout/order/myOrders` | 我的订单 |
| 订单 | GET | `/takeout/order/merchant/stats` | 商家统计 |
| 骑手 | POST | `/takeout/rider/apply` | 骑手认证 |
| 骑手 | POST | `/takeout/rider/takeOrder` | 接单 |
| 骑手 | POST | `/takeout/rider/completeOrder` | 完成配送 |
| 骑手 | GET | `/takeout/rider/orders` | 配送历史 |

## 作者

后端独立开发 — [nobody-814](https://github.com/nobody-814)
