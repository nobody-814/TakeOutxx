package org.example.service;

import org.example.domain.OrderItem;
import java.util.List;

public interface OrderItemService {
    int addOrderItem(OrderItem orderItem);
    List<OrderItem> getItemsByOrderId(String orderId);
}