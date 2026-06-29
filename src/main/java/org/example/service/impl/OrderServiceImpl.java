package org.example.service.impl;

import org.example.domain.Cart;
import org.example.domain.Order;
import org.example.domain.OrderItem;
import org.example.mapper.OrderMapper;
import org.example.mapper.RiderMapper;
import org.example.service.CartService;
import org.example.service.OrderItemService;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RiderMapper riderMapper;

    @Override
    public int createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        return orderMapper.insert(order);
    }

    @Override
    public Order getOrderById(String id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<Order> getMyOrders(Integer userId) {
        return orderMapper.selectByUserId(userId, null);
    }

    @Override
    public List<Order> getMyOrders(Integer userId, Integer status) {
        return orderMapper.selectByUserId(userId, status);
    }

    @Override
    public List<Order> getMerchantOrders(Integer merchantId) {
        return orderMapper.selectByMerchantId(merchantId, null);
    }

    @Override
    public List<Order> getMerchantOrders(Integer merchantId, Integer status) {
        return orderMapper.selectByMerchantId(merchantId, status);
    }

    @Override
    public List<Order> getWaitRiderOrders() {
        return orderMapper.selectWaitRider();
    }

    @Override
    public boolean updateOrderStatus(String id, Integer status) {
        return orderMapper.updateStatus(id, status) > 0;
    }

    @Override
    public boolean riderTakeOrder(String id, Integer riderId) {
        orderMapper.bindRider(id, riderId);
        return orderMapper.updateStatus(id, 3) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrderFromCart(Integer userId, Integer merchantId, Order orderInfo) {
        List<Cart> cartList = cartService.getCartWithProduct(userId, merchantId);
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法下单");
        }
        String orderId = "ORD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        orderInfo.setId(orderId);
        orderInfo.setUserId(userId);
        orderInfo.setMerchantId(merchantId);
        orderInfo.setStatus(0);
        orderInfo.setCreatedAt(LocalDateTime.now());
        if (orderInfo.getDiscountAmount() == null) {
            orderInfo.setDiscountAmount(BigDecimal.ZERO);
        }
        orderMapper.insert(orderInfo);
        for (Cart cart : cartList) {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProduct().getName());
            item.setProductPrice(cart.getProduct().getPrice());
            item.setQuantity(cart.getQuantity());
            BigDecimal itemTotal = cart.getProduct().getPrice()
                    .multiply(new BigDecimal(cart.getQuantity()));
            item.setTotalPrice(itemTotal);
            orderItemService.addOrderItem(item);
        }
        cartService.clearCart(userId, merchantId);
        return orderId;
    }

    @Override
    public boolean payOrder(String id) {
        orderMapper.updateStatus(id, 1);
        return true;
    }

    @Override
    public List<Order> getRiderCurrentOrders(Integer riderId) {
        return orderMapper.selectCurrentByRiderId(riderId);
    }

    @Override
    public boolean completeOrder(String id) {
        Order order = orderMapper.selectById(id);
        if (order != null && order.getRiderId() != null) {
            riderMapper.updateStatus(order.getRiderId(), 1);
        }
        return orderMapper.updateStatus(id, 4) > 0;
    }

    @Override
    public List<Order> getRiderOrders(Integer riderId) {
        return orderMapper.selectByRiderId(riderId, null);
    }

    @Override
    public List<Order> getRiderOrders(Integer riderId, Integer status) {
        return orderMapper.selectByRiderId(riderId, status);
    }

    @Override
    public int getMerchantOrderCount(Integer merchantId) {
        return orderMapper.countByMerchantId(merchantId);
    }

    @Override
    public java.math.BigDecimal getMerchantTotalSales(Integer merchantId) {
        return orderMapper.sumSalesByMerchantId(merchantId);
    }

    @Override
    public boolean cancelOrder(String id) {
        return orderMapper.cancelOrder(id) > 0;
    }
}