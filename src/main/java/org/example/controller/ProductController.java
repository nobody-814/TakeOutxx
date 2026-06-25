package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Merchant;
import org.example.domain.Product;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/product")
@CrossOrigin
public class ProductController {

    @Resource
    private ProductService productService;

    @Resource
    private MerchantService merchantService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null || user.getId() == null) return Result.error("用户不存在");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("你还没有开通店铺，请先入驻");
        if (product.getName() == null || product.getName().trim().isEmpty()) return Result.error("商品名称不能为空");
        if (product.getPrice() == null) return Result.error("价格不能为空");
        product.setMerchantId(merchant.getId());
        product.setId(null);
        try {
            productService.addProduct(product);
            return Result.success("商品发布成功");
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result getProductById(@PathVariable Integer id) {
        Product product = productService.getById(id);
        return product == null ? Result.error("商品不存在或已下架") : Result.success(product);
    }

    @GetMapping("/list/{merchantId}")
    public Result getListByShop(@PathVariable Integer merchantId) {
        List<Product> list = productService.getByMerchantId(merchantId);
        return Result.success(list);
    }

    @GetMapping("/listByCategory")
    public Result getListByCategory(@RequestParam Integer merchantId, @RequestParam Integer categoryId) {
        List<Product> list = productService.getByMerchantAndCategory(merchantId, categoryId);
        return Result.success(list);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer id, @RequestParam Integer status, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无权限");
        Product product = productService.getById(id);
        if (product == null) return Result.error("商品不存在");
        if (!product.getMerchantId().equals(merchant.getId())) return Result.error("无权操作");
        productService.updateStatus(id, status);
        return Result.success("状态已更新");
    }

    @PostMapping("/delete/{id}")
    public Result deleteProduct(@PathVariable Integer id, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) return Result.error("请先登录");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) return Result.error("用户不存在");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无权限");
        Product product = productService.getById(id);
        if (product == null) return Result.error("商品不存在");
        if (!product.getMerchantId().equals(merchant.getId())) return Result.error("无权操作");
        productService.deleteProduct(id);
        return Result.success("删除成功");
    }
}
