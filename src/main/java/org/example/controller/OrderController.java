package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/order")
@CrossOrigin
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    // ====================== 创建订单 ======================
    @PostMapping("/create")
    public Result create(@RequestBody Order order, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        order.setUserId(user.getId());
        order.setId("ORD" + System.currentTimeMillis());
        order.setStatus(0); // 待支付

        orderService.createOrder(order);
        return Result.success("订单创建成功", order.getId());
    }

    // ====================== 订单详情 ======================
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return order == null ? Result.error("订单不存在") : Result.success(order);
    }

    // ====================== 我的订单 ======================
    @GetMapping("/myOrders")
    public Result myOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("未登录");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        return Result.success(orderService.getMyOrders(user.getId()));
    }

    // ====================== 商家查看订单 ======================
    @GetMapping("/merchantOrders")
    public Result merchantOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("未登录");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        return Result.success(orderService.getMerchantOrders(user.getId()));
    }

    // ====================== 骑手查看待接单 ======================
    @GetMapping("/rider/wait")
    public Result riderWait() {
        return Result.success(orderService.getWaitRiderOrders());
    }

    // ====================== 骑手接单 ======================
    @PostMapping("/rider/take")
    public Result takeOrder(@RequestParam String orderId, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        orderService.riderTakeOrder(orderId, user.getId());
        return Result.success("接单成功");
    }

    // ====================== 修改订单状态 ======================
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam String id, @RequestParam Integer status) {
        orderService.updateOrderStatus(id, status);
        return Result.success("状态已更新");
    }
}