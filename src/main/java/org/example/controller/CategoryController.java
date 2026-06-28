package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Category;
import org.example.domain.Merchant;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.CategoryService;
import org.example.service.MerchantService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public Result add(@RequestBody Category category) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("当前账号不是商家，请先入驻店铺");
        if (category.getName() == null || category.getName().trim().isEmpty())
            return Result.error("分类名称不能为空");
        category.setMerchantId(merchant.getId());
        if (category.getSort() == null) category.setSort(0);
        category.setId(null);
        try {
            categoryService.addCategory(category);
            return Result.success("添加成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId) {
        return Result.success(categoryService.getByMerchantId(merchantId));
    }
}