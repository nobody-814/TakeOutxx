package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.Rider;
import org.example.domain.User;
import org.example.service.OrderService;
import org.example.service.RiderService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/takeout/rider")
@CrossOrigin
public class RiderController {

    @Resource
    private RiderService riderService;

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    // 骑手入驻注册
    @PostMapping("/apply")
    public Result applyRider(@RequestBody Rider rider, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        rider.setUserId(user.getId());
        int res = riderService.addRider(rider);
        if (res > 0) {
            return Result.success("骑手入驻成功！账号已升级为骑手，请重新登录或刷新页面。");
        }
        return Result.error("入驻失败，可能已申请过骑手或服务器异常");
    }

    // 查看当前骑手个人信息
    @GetMapping("/info")
    public Result getSelfInfo(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) {
            return Result.error("当前账号不是骑手");
        }
        return Result.success(rider);
    }

    // 切换在线/离线/配送中状态
    @PostMapping("/status")
    public Result updateStatus(@RequestParam Integer status, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) {
            return Result.error("账号非骑手身份");
        }
        boolean flag = riderService.changeStatus(rider.getId(), status);
        return flag ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    // 查询所有在线骑手
    @GetMapping("/online/list")
    public Result getOnlineRiderList() {
        List<Rider> list = riderService.getOnlineRider();
        return Result.success(list);
    }

    // 骑手接单
    @PostMapping("/takeOrder")
    public Result takeOrder(@RequestParam String orderId, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null || rider.getStatus() != 1) {
            return Result.error("仅在线骑手可接单");
        }
        boolean takeSuccess = orderService.riderTakeOrder(orderId, rider.getId());
        if (takeSuccess) {
            riderService.changeStatus(rider.getId(), 2);
            return Result.success("接单成功，请尽快取餐");
        }
        return Result.error("接单失败，订单状态异常");
    }

    
    // 骑手当前配送订单（状态=3）
    @GetMapping("/orders/current")
    public Result getCurrentOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        return Result.success(orderService.getRiderCurrentOrders(rider.getId()));
    }

    // 骑手配送完成（状态 3→4）
    @PostMapping("/completeOrder")
    public Result completeOrder(@RequestParam String orderId, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(rider.getId())) return Result.error("无权操作此订单");
        orderService.completeOrder(orderId);
        return Result.success("配送完成");
    }
    // 骑手配送历史
    @GetMapping("/orders")
    public Result getRiderOrders(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        Rider rider = riderService.getRiderByUserId(user.getId());
        if (rider == null) return Result.error("非骑手身份");
        return Result.success(orderService.getRiderOrders(rider.getId()));
    }

    // 更新骑手评分
    @PostMapping("/rating")
    public Result updateRating(@RequestParam Integer riderId, @RequestParam BigDecimal rating) {
        boolean flag = riderService.changeRating(riderId, rating);
        return flag ? Result.success("评分更新成功") : Result.error("评分更新失败，评分范围0-5");
    }
}