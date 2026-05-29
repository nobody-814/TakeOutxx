package org.example.controller;


import org.example.domain.Merchant;
import org.example.domain.Product;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.ProductService;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/takeout/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantService merchantService;

    // ====================== 商家发布商品 ======================
    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) {
            return Result.error("你还不是商家");
        }

        product.setMerchantId(merchant.getId());
        productService.addProduct(product);
        return Result.success("商品发布成功");
    }

    // ====================== 根据ID查询商品 ======================
    @GetMapping("/detail/{id}")
    public Result getProductById(@PathVariable Integer id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在或已下架");
        }
        return Result.success(product);
    }

    // ====================== 获取店铺的所有商品 ======================
    @GetMapping("/list/{merchantId}")
    public Result getListByShop(@PathVariable Integer merchantId) {
        List<Product> list = productService.getByMerchantId(merchantId);
        return Result.success(list);
    }

    // ====================== 按分类获取商品 ======================
    @GetMapping("/listByCategory")
    public Result getListByCategory(
            @RequestParam Integer merchantId,
            @RequestParam Integer categoryId) {
        List<Product> list = productService.getByMerchantAndCategory(merchantId, categoryId);
        return Result.success(list);
    }

    // ====================== 上下架商品 ======================
    @PostMapping("/updateStatus")
    public Result updateStatus(
            @RequestParam Integer id,
            @RequestParam Integer status,
            HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        Product product = productService.getById(id);

        if (!product.getMerchantId().equals(merchant.getId())) {
            return Result.error("无权限操作");
        }

        productService.updateStatus(id, status);
        return Result.success("状态已更新");
    }

    // ====================== 删除商品 ======================
    @PostMapping("/delete/{id}")
    public Result deleteProduct(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        Product product = productService.getById(id);

        if (!product.getMerchantId().equals(merchant.getId())) {
            return Result.error("无权限");
        }

        productService.deleteProduct(id);
        return Result.success("删除成功");
    }
}