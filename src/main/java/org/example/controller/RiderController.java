package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.Rider;
import org.example.domain.User;
import org.example.service.OrderService;
import org.example.service.RiderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/takeout/rider")
@CrossOrigin
public class RiderController {

    @Resource
    private RiderService riderService;

    @Resource
    private OrderService orderService;

    @PostMapping("/apply")
    public Result applyRider(@RequestBody Rider rider) {
        User user = SecurityUtil.getCurrentUser();
        rider.setUserId(user.getId());
        int res = riderService.addRider(rider);
        return res > 0
                ? Result.success("骑手入驻成功！账号已升级为骑手，请重新登录或刷新页面。")
                : Result.error("入驻失败，可能已申请过骑手或服务器异常");
    }

    @GetMapping("/info")
    public Result getSelfInfo() {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        return rider == null ? Result.error("当前账号不是骑手") : Result.success(rider);
    }

    @PostMapping("/status")
    public Result updateStatus(@RequestParam Integer status) {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("账号非骑手身份");
        boolean flag = riderService.changeStatus(rider.getId(), status);
        return flag ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @GetMapping("/online/list")
    public Result getOnlineRiderList() {
        return Result.success(riderService.getOnlineRider());
    }

    @PostMapping("/takeOrder")
    public Result takeOrder(@RequestParam String orderId) {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null || rider.getStatus() != 1) return Result.error("仅在线骑手可接单");
        boolean takeSuccess = orderService.riderTakeOrder(orderId, rider.getId());
        if (takeSuccess) {
            riderService.changeStatus(rider.getId(), 2);
            return Result.success("接单成功，请尽快取餐");
        }
        return Result.error("接单失败，订单状态异常");
    }

    @GetMapping("/orders/current")
    public Result getCurrentOrders() {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        return Result.success(orderService.getRiderCurrentOrders(rider.getId()));
    }

    @PostMapping("/completeOrder")
    public Result completeOrder(@RequestParam String orderId) {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(rider.getId()))
            return Result.error("无权操作此订单");
        orderService.completeOrder(orderId);
        return Result.success("配送完成");
    }

    @GetMapping("/orders")
    public Result getRiderOrders() {
        User user = SecurityUtil.getCurrentUser();
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        return Result.success(orderService.getRiderOrders(rider.getId()));
    }

    @PostMapping("/rating")
    public Result updateRating(@RequestParam Integer riderId, @RequestParam java.math.BigDecimal rating) {
        boolean flag = riderService.changeRating(riderId, rating);
        return flag ? Result.success("评分更新成功") : Result.error("评分更新失败，评分范围1-5");
    }
}