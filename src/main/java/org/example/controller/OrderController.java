package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
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

    @PostMapping("/create")
    public Result create(@RequestBody Order order) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        order.setUserId(user.getId());
        order.setId("ORD" + System.currentTimeMillis());
        order.setStatus(0);
        orderService.createOrder(order);
        return Result.success(order.getId(), "订单创建成功");
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return order == null ? Result.error("订单不存在") : Result.success(order);
    }

    @GetMapping("/myOrders")
    public Result myOrders(@RequestParam(required = false) Integer status) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("未登录");
        if (status != null) {
            return Result.success(orderService.getMyOrders(user.getId(), status));
        }
        return Result.success(orderService.getMyOrders(user.getId()));
    }

    @GetMapping("/merchantOrders")
    public Result merchantOrders(@RequestParam(required = false) Integer status) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("未登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("未开通店铺");
        if (status != null) {
            return Result.success(orderService.getMerchantOrders(merchant.getId(), status));
        }
        return Result.success(orderService.getMerchantOrders(merchant.getId()));
    }

    @GetMapping("/merchant/stats")
    public Result merchantStats() {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
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
    public Result takeOrder(@RequestParam String orderId) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        orderService.riderTakeOrder(orderId, user.getId());
        return Result.success("接单成功");
    }

    @PostMapping("/pay/{id}")
    public Result payOrder(@PathVariable String id) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
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

    @PostMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable String id) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Order order = orderService.getOrderById(id);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(user.getId())) return Result.error("无权取消他人订单");
        if (order.getStatus() >= 4 || order.getStatus() == 5) return Result.error("当前订单状态不可取消");
        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("订单已取消") : Result.error("取消失败");
    }
}