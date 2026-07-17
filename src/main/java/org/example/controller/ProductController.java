package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
import org.example.domain.Merchant;
import org.example.domain.Product;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.example.service.impl.ProductServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/takeout/product")
@CrossOrigin
public class ProductController {

    @Resource private ProductService productService;
    @Resource private ProductServiceImpl productServiceImpl;
    @Resource private MerchantService merchantService;
    @Resource private UserService userService;

    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("你还没有开通店铺，请先入驻");
        if (product.getName() == null || product.getName().trim().isEmpty()) return Result.error("商品名称不能为空");
        if (product.getPrice() == null) return Result.error("价格不能为空");
        product.setMerchantId(merchant.getId());
        product.setId(null);
        try {
            productService.addProduct(product);
            productServiceImpl.evictProductCache(merchant.getId());
            return Result.success("商品发布成功");
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    
    @PutMapping("/update")
    public Result updateProduct(@RequestBody Product product) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无权限");
        Product existing = productService.getById(product.getId());
        if (existing == null) return Result.error("商品不存在");
        if (!existing.getMerchantId().equals(merchant.getId())) return Result.error("无权操作此商品");
        if (product.getName() == null || product.getName().trim().isEmpty()) return Result.error("商品名称不能为空");
        if (product.getPrice() == null) return Result.error("价格不能为空");
        product.setMerchantId(merchant.getId());
        try {
            productService.updateProduct(product);
            productServiceImpl.evictProductCache(merchant.getId());
            productServiceImpl.evictProductDetailCache(product.getId());
            return Result.success("商品更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result getProductById(@PathVariable Integer id) {
        Product product = productService.getById(id);
        return product == null ? Result.error("商品不存在或已下架") : Result.success(product);
    }

    @GetMapping("/list/{merchantId}")
    public Result getListByShop(@PathVariable Integer merchantId) {
        return Result.success(productService.getByMerchantId(merchantId));
    }

    @GetMapping("/manageList/{merchantId}")
    public Result getManageList(@PathVariable Integer merchantId) {
        return Result.success(productService.getAllByMerchantId(merchantId));
    }

    @GetMapping("/listByCategory")
    public Result getListByCategory(@RequestParam Integer merchantId, @RequestParam Integer categoryId) {
        return Result.success(productService.getByMerchantAndCategory(merchantId, categoryId));
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无权限");
        Product product = productService.getById(id);
        if (product == null) return Result.error("商品不存在");
        if (!product.getMerchantId().equals(merchant.getId())) return Result.error("无权操作");
        productService.updateStatus(id, status);
        productServiceImpl.evictProductCache(merchant.getId());
        productServiceImpl.evictProductDetailCache(id);
        return Result.success("状态已更新");
    }

    @PostMapping("/delete/{id}")
    public Result deleteProduct(@PathVariable Integer id) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无权限");
        Product product = productService.getById(id);
        if (product == null) return Result.error("商品不存在");
        if (!product.getMerchantId().equals(merchant.getId())) return Result.error("无权操作");
        productService.deleteProduct(id);
        productServiceImpl.evictProductCache(merchant.getId());
        productServiceImpl.evictProductDetailCache(id);
        return Result.success("删除成功");
    }
}