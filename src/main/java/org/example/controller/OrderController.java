package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Merchant;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/takeout/order")
@CrossOrigin
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private MerchantService merchantService;

    @Resource
    private JwtUtils jwtUtils;

    // 创建订单
    @PostMapping("/create")
    public Result create(@RequestBody Order order, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        order.setUserId(user.getId());
        order.setId("ORD" + System.currentTimeMillis());
        order.setStatus(0);
        orderService.createOrder(order);
        return Result.success("订单创建成功", order.getId());
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return order == null ? Result.error("订单不存在") : Result.success(order);
    }

    @GetMapping("/myOrders")
    public Result myOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("未登录");
        User user = userService.getUserByUsername(jwtUtils.getUsernameFromToken(token));
        if (user == null) return Result.error("用户不存在");
        return Result.success(orderService.getMyOrders(user.getId()));
    }

    @GetMapping("/merchantOrders")
    public Result merchantOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("未登录");
        User user = userService.getUserByUsername(jwtUtils.getUsernameFromToken(token));
        if (user == null) return Result.error("用户不存在");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("未开通店铺");
        return Result.success(orderService.getMerchantOrders(merchant.getId()));
    }

    // 商家统计数据（订单数 + 总销售额）
    @GetMapping("/merchant/stats")
    public Result merchantStats(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        User user = userService.getUserByUsername(jwtUtils.getUsernameFromToken(token));
        if (user == null) return Result.error("用户不存在");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("未开通店铺");
        Map<String, Object> stats = new HashMap<>();
        stats.put("orderCount", orderService.getMerchantOrderCount(merchant.getId()));
        stats.put("totalSales", orderService.getMerchantTotalSales(merchant.getId()));
        return Result.success(stats);
    }

    @GetMapping("/rider/wait")
    public Result riderWait() {
        return Result.success(orderService.getWaitRiderOrders());
    }

    @PostMapping("/rider/take")
    public Result takeOrder(@RequestParam String orderId, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        User user = userService.getUserByUsername(jwtUtils.getUsernameFromToken(token));
        if (user == null) return Result.error("用户不存在");
        orderService.riderTakeOrder(orderId, user.getId());
        return Result.success("接单成功");
    }

    // 支付（模拟自动支付 0→1）
    @PostMapping("/pay/{id}")
    public Result payOrder(@PathVariable String id, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        Order order = orderService.getOrderById(id);
        if (order == null) return Result.error("订单不存在");
        if (order.getStatus() != 0) return Result.error("订单状态异常");
        orderService.payOrder(id);
        return Result.success("支付成功");
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam String id, @RequestParam Integer status) {
        orderService.updateOrderStatus(id, status);
        return Result.success("状态已更新");
    }
}