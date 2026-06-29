package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Order;
import org.example.domain.Result;
import org.example.domain.Review;
import org.example.domain.User;
import org.example.service.OrderService;
import org.example.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/review")
@CrossOrigin
public class ReviewController {

    @Resource private ReviewService reviewService;
    @Resource private OrderService orderService;

    @PostMapping("/submit")
    public Result submit(@RequestBody List<Review> reviews, @RequestParam String orderId) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Order order = orderService.getOrderById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(user.getId())) return Result.error("无权评价");
        if (order.getStatus() != 4) return Result.error("仅已完成订单可评价");
        if (reviewService.hasReviewed(orderId)) return Result.error("该订单已评价");
        reviewService.submitOrderReviews(orderId, user.getId(),
                order.getMerchantId(), order.getRiderId(), reviews);
        return Result.success("评价成功");
    }

    @GetMapping("/merchant/{merchantId}")
    public Result getMerchantReviews(@PathVariable Integer merchantId) {
        return Result.success(reviewService.getByMerchant(merchantId));
    }

    @GetMapping("/product/{productId}")
    public Result getProductReviews(@PathVariable Integer productId) {
        return Result.success(reviewService.getByProduct(productId));
    }

    @GetMapping("/rider/{riderId}")
    public Result getRiderReviews(@PathVariable Integer riderId) {
        return Result.success(reviewService.getByRider(riderId));
    }

    @GetMapping("/order/{orderId}")
    public Result getOrderReviews(@PathVariable String orderId) {
        return Result.success(reviewService.getByOrder(orderId));
    }
}