package org.example.controller;

import org.example.domain.OrderItem;
import org.example.service.OrderItemService;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/orderItem")
@CrossOrigin
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // 添加订单项
    @PostMapping("/add")
    public Result add(@RequestBody OrderItem orderItem) {
        orderItemService.addOrderItem(orderItem);
        return Result.success("订单项添加成功");
    }

    // 根据订单号查询所有商品
    @GetMapping("/list/{orderId}")
    public Result getItems(@PathVariable String orderId) {
        List<OrderItem> list = orderItemService.getItemsByOrderId(orderId);
        return Result.success(list);
    }
}