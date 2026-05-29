package org.example.controller;

import org.example.domain.Cart;
import org.example.domain.User;
import org.example.service.CartService;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/takeout/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    // 添加购物车
    @PostMapping("/add")
    public Result add(@RequestBody Cart cart, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return Result.error("请先登录");
        cart.setUserId(user.getId());
        cartService.addCart(cart);
        return Result.success("加入购物车成功");
    }

    // 获取购物车列表（带商品）
    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        List<Cart> list = cartService.getCartWithProduct(user.getId(), merchantId);
        return Result.success(list);
    }

    // 修改数量
    @PostMapping("/update")
    public Result update(@RequestParam Integer id, @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return Result.success("更新成功");
    }

    // 删除
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return Result.success("删除成功");
    }

    // 清空
    @PostMapping("/clear/{merchantId}")
    public Result clear(@PathVariable Integer merchantId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        cartService.clearCart(user.getId(), merchantId);
        return Result.success("清空成功");
    }
}