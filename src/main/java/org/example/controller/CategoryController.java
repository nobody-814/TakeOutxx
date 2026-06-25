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
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null || user.getId() == null) {
            return Result.error("用户不存在");
        }
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) {
            return Result.error("当前账号不是商家，请先入驻店铺");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        category.setMerchantId(merchant.getId());
        if (category.getSort() == null) category.setSort(0);
        category.setId(null); // 确保自增主键
        try {
            categoryService.addCategory(category);
            return Result.success("添加成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    // 根据商家ID查询分类列表（公开接口，无需登录）
    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId) {
        return Result.success(categoryService.getByMerchantId(merchantId));
    }
}