package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
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

    // 1. 骑手入驻注册
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
            // 入驻成功，角色可能已在 Service 层自动升级，提示用户刷新页面
            return Result.success("骑手入驻成功！你的账号已升级为骑手，请重新登录或刷新页面。");
        }
        return Result.error("入驻失败，可能已申请过骑手或服务器异常");
    }

    // 2. 查看当前骑手个人信息
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

    // 3. 切换在线/离线/配送中状态
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

    // 4. 查询所有在线骑手（后台/商家使用）
    @GetMapping("/online/list")
    public Result getOnlineRiderList() {
        List<Rider> list = riderService.getOnlineRider();
        return Result.success(list);
    }

    // 5. 骑手接单（关联订单模块）
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

    // 6. 更新骑手评分
    @PostMapping("/rating")
    public Result updateRating(@RequestParam Integer riderId, @RequestParam BigDecimal rating) {
        boolean flag = riderService.changeRating(riderId, rating);
        return flag ? Result.success("评分更新成功") : Result.error("评分更新失败，评分范围0-5");
    }
}