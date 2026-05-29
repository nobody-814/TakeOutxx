package org.example.service.impl;

import org.example.domain.Cart;
import org.example.domain.Order;
import org.example.domain.OrderItem;
import org.example.mapper.OrderMapper;
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
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public List<Order> getMerchantOrders(Integer merchantId) {
        return orderMapper.selectByMerchantId(merchantId);
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

    // ====================== 【核心：购物车一键结算】 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrderFromCart(Integer userId, Integer merchantId, Order orderInfo) {

        // 1. 获取购物车列表（带商品信息）
        List<Cart> cartList = cartService.getCartWithProduct(userId, merchantId);
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法下单");
        }

        // 2. 生成唯一订单号
        String orderId = "ORD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // 3. 封装订单信息
        orderInfo.setId(orderId);
        orderInfo.setUserId(userId);
        orderInfo.setMerchantId(merchantId);
        orderInfo.setStatus(0); // 0=待支付
        orderInfo.setCreatedAt(LocalDateTime.now());

        // 优惠默认为0
        if (orderInfo.getDiscountAmount() == null) {
            orderInfo.setDiscountAmount(BigDecimal.ZERO);
        }

        // 4. 插入订单主表
        orderMapper.insert(orderInfo);

        // 5. 批量插入订单项（商品快照）
        for (Cart cart : cartList) {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProduct().getName());
            item.setProductPrice(cart.getProduct().getPrice());
            item.setQuantity(cart.getQuantity());

            // 计算小计
            BigDecimal itemTotal = cart.getProduct().getPrice()
                    .multiply(new BigDecimal(cart.getQuantity()));
            item.setTotalPrice(itemTotal);

            orderItemService.addOrderItem(item);
        }

        // 6. 清空当前店铺购物车
        cartService.clearCart(userId, merchantId);

        // 7. 返回订单号
        return orderId;
    }
}