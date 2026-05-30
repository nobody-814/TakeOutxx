package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Category;
import org.example.domain.Merchant;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.CategoryService;
import org.example.service.MerchantService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/category")
@CrossOrigin
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private MerchantService merchantService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    // 添加分类（需要登录）
    @PostMapping("/add")
    public Result add(@RequestBody Category category, @RequestHeader("token") String token) {
        // 1. 校验 token
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }

        // 2. 从 token 拿用户名
        String username = jwtUtils.getUsernameFromToken(token);

        // 3. 使用专用方法获取用户（不校验密码）
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 4. 获取商家信息
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) {
            return Result.error("当前账号不是商家，无法添加分类");
        }

        category.setMerchantId(merchant.getId());

        // 5. 执行添加
        categoryService.addCategory(category);
        return Result.success("添加成功");
    }

    // 根据商家ID查询分类列表（公开接口，无需登录）
    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId) {
        return Result.success(categoryService.getByMerchantId(merchantId));
    }
}