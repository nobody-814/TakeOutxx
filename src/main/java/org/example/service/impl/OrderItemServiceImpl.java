package org.example.service.impl;

import org.example.domain.OrderItem;
import org.example.domain.Product;
import org.example.mapper.OrderItemMapper;
import org.example.mapper.ProductMapper;
import org.example.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.Common.RedisConstant.HOT_PRODUCTS_KEY;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addOrderItem(OrderItem orderItem) {
        int qty = orderItem.getQuantity() != null ? orderItem.getQuantity() : 1;
        Integer productId = orderItem.getProductId();

        // 先扣库存，带行锁防止超卖
        int decreased = productMapper.decreaseStock(productId, qty);
        if (decreased <= 0) {
            throw new RuntimeException("库存不足");
        }

        // 加销量
        productMapper.increaseSalesByQuantity(productId, qty);

        // 写订单明细
        int result = orderItemMapper.insert(orderItem);

        // 清缓存
        Product product = productMapper.selectById(productId);
        if (product != null) {
            redisTemplate.delete(HOT_PRODUCTS_KEY + product.getMerchantId());
        }

        return result;
    }

    @Override
    public List<OrderItem> getItemsByOrderId(String orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
}