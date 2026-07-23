package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Merchant;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.OrderService;
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
    private MerchantService merchantService;

    @PostMapping("/create")
    public Result create(@RequestBody Order order) {
        User user = SecurityUtil.getCurrentUser();
        order.setUserId(user.getId());
        order.setId("ORD" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
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
        if (status != null) {
            return Result.success(orderService.getMyOrders(user.getId(), status));
        }
        return Result.success(orderService.getMyOrders(user.getId()));
    }

    @GetMapping("/merchantOrders")
    public Result merchantOrders(@RequestParam(required = false) Integer status) {
        User user = SecurityUtil.getCurrentUser();
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
        boolean success = orderService.riderTakeOrder(orderId, user.getId());
        return success ? Result.success("接单成功") : Result.error("该订单已被其他骑手抢走");
    }

    @PostMapping("/pay/{id}")
    public Result payOrder(@PathVariable String id) {
        User user = SecurityUtil.getCurrentUser();
        Order order = orderService.getOrderById(id);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(user.getId())) return Result.error("无权操作他人订单");
        if (order.getStatus() != 0) return Result.error("订单状态异常");
        boolean paid = orderService.payOrder(id);
        return paid ? Result.success("支付成功") : Result.error("订单状态异常，无法支付");
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam String id, @RequestParam Integer status) {
        try {
            orderService.updateOrderStatus(id, status);
            return Result.success("状态已更新");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable String id) {
        User user = SecurityUtil.getCurrentUser();
        Order order = orderService.getOrderById(id);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(user.getId())) return Result.error("无权取消他人订单");
        if (order.getStatus() >= 4 || order.getStatus() == 5) return Result.error("当前状态不可取消，仅待支付或待接单的订单可取消");
        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("订单已取消") : Result.error("取消失败");
    }
}