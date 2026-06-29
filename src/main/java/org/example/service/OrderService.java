package org.example.service;

import org.example.domain.Order;
import java.util.List;

public interface OrderService {

    int createOrder(Order order);

    Order getOrderById(String id);

    List<Order> getMyOrders(Integer userId);
    List<Order> getMyOrders(Integer userId, Integer status);

    List<Order> getMerchantOrders(Integer merchantId);
    List<Order> getMerchantOrders(Integer merchantId, Integer status);

    List<Order> getWaitRiderOrders();

    boolean updateOrderStatus(String id, Integer status);

    boolean riderTakeOrder(String id, Integer riderId);

    String submitOrderFromCart(Integer userId, Integer merchantId, Order orderInfo);

    boolean payOrder(String id);

    List<Order> getRiderCurrentOrders(Integer riderId);

    boolean completeOrder(String id);

    List<Order> getRiderOrders(Integer riderId);
    List<Order> getRiderOrders(Integer riderId, Integer status);

    int getMerchantOrderCount(Integer merchantId);

    java.math.BigDecimal getMerchantTotalSales(Integer merchantId);

    boolean cancelOrder(String id);
}