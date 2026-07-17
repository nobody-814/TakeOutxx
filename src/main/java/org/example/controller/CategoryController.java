package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Category;
import org.example.domain.Merchant;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.CategoryService;
import org.example.service.MerchantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/takeout/category")
@CrossOrigin
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private MerchantService merchantService;

    @PostMapping("/add")
    public Result add(@RequestBody Category category) {
        User user = SecurityUtil.getCurrentUser();
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

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        User user = SecurityUtil.getCurrentUser();
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("当前账号不是商家");

        Category category = categoryService.getById(id);
        if (category == null) return Result.error("分类不存在");
        if (!category.getMerchantId().equals(merchant.getId()))
            return Result.error("无权删除其他商家的分类");

        int deleted = categoryService.deleteCategory(id, merchant.getId());
        return deleted > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }
}