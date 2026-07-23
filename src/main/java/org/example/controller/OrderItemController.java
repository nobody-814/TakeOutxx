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

    @PostMapping("/add")
    public Result add(@RequestBody OrderItem orderItem) {
        int result = orderItemService.addOrderItem(orderItem);
        if (result > 0) {
            return Result.success("订单项添加成功");
        } else {
            return Result.error("库存不足");
        }
    }

    @GetMapping("/list/{orderId}")
    public Result getItems(@PathVariable String orderId) {
        List<OrderItem> list = orderItemService.getItemsByOrderId(orderId);
        return Result.success(list);
    }
}