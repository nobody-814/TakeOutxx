package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Cart;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/takeout/cart")
@CrossOrigin
public class CartController {

    @Resource
    private CartService cartService;

    @PostMapping("/add")
    public Result add(@RequestBody Cart cart) {
        User user = SecurityUtil.getCurrentUser();
        cart.setUserId(user.getId());
        cartService.addCart(cart);
        return Result.success("加入购物车成功");
    }

    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId) {
        User user = SecurityUtil.getCurrentUser();
        return Result.success(cartService.getCartWithProduct(user.getId(), merchantId));
    }

    @PostMapping("/update")
    public Result update(@RequestParam Integer id, @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return Result.success("更新成功");
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        User user = SecurityUtil.getCurrentUser();
        cartService.deleteCart(id);
        return Result.success("删除成功");
    }

    @PostMapping("/clear/{merchantId}")
    public Result clear(@PathVariable Integer merchantId) {
        User user = SecurityUtil.getCurrentUser();
        cartService.clearCart(user.getId(), merchantId);
        return Result.success("清空成功");
    }
}