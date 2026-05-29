package org.example.controller;
import org.example.domain.Category;
import org.example.domain.Merchant;
import org.example.domain.User;
import org.example.service.CategoryService;
import org.example.service.MerchantService;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
@RestController
@RequestMapping("/takeout/category")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MerchantService merchantService;

    @PostMapping("/add")
    public Result add(@RequestBody Category category, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        category.setMerchantId(merchant.getId());
        categoryService.addCategory(category);
        return Result.success("添加成功");
    }

    @GetMapping("/list/{merchantId}")
    public Result list(@PathVariable Integer merchantId) {
        return Result.success(categoryService.getByMerchantId(merchantId));
    }
}