package org.example.service;

import org.example.domain.Order;
import java.util.List;

public interface OrderService {

    // 创建订单
    int createOrder(Order order);

    // 根据ID查询
    Order getOrderById(String id);

    // 我的订单
    List<Order> getMyOrders(Integer userId);

    // 商家订单
    List<Order> getMerchantOrders(Integer merchantId);

    // 待接单列表
    List<Order> getWaitRiderOrders();

    // 修改状态
    boolean updateOrderStatus(String id, Integer status);

    // 骑手接单
    boolean riderTakeOrder(String id, Integer riderId);

    // 从购物车提交订单（核心！）
    String submitOrderFromCart(Integer userId, Integer merchantId, Order orderInfo);
}