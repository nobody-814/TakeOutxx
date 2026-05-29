package org.example.service.impl;

import org.example.domain.OrderItem;
import org.example.mapper.OrderItemMapper;
import org.example.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public int addOrderItem(OrderItem orderItem) {
        return orderItemMapper.insert(orderItem);
    }

    @Override
    public List<OrderItem> getItemsByOrderId(String orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
}