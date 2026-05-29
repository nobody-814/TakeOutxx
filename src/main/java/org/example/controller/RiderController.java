package org.example.controller;

import org.example.domain.Rider;
import org.example.domain.User;
import org.example.service.OrderService;
import org.example.service.RiderService;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/takeout/rider")
@CrossOrigin
public class RiderController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private OrderService orderService;

    // 1. 骑手入驻注册
    @PostMapping("/apply")
    public Result applyRider(@RequestBody Rider rider, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        // 绑定当前登录用户ID
        rider.setUserId(loginUser.getId());
        int res = riderService.addRider(rider);
        if (res > 0) {
            return Result.success("骑手入驻成功");
        }
        return Result.error("入驻失败");
    }

    // 2. 查看当前骑手个人信息
    @GetMapping("/info")
    public Result getSelfInfo(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        Rider rider = riderService.getRiderByUserId(loginUser.getId());
        if (rider == null) {
            return Result.error("当前账号不是骑手");
        }
        return Result.success(rider);
    }

    // 3. 切换在线/离线/配送中状态
    @PostMapping("/status")
    public Result updateStatus(@RequestParam Integer status, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        Rider rider = riderService.getRiderByUserId(loginUser.getId());
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
    public Result takeOrder(@RequestParam String orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        Rider rider = riderService.getRiderByUserId(loginUser.getId());
        if (rider == null || rider.getStatus() != 1) {
            return Result.error("仅在线骑手可接单");
        }
        // 调用订单服务完成接单绑定
        boolean takeSuccess = orderService.riderTakeOrder(orderId, rider.getId());
        if (takeSuccess) {
            // 接单后自动改为配送中
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