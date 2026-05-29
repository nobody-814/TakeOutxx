package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.domain.Merchant;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/merchant")
@CrossOrigin
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    // ====================== 1. 商家入驻（创建店铺） ======================
    @PostMapping("/create")
    public Result createMerchant(@RequestBody Merchant merchant, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Integer userId = loginUser.getId().intValue();
        int code = merchantService.createMerchant(merchant, userId.longValue());

        if (code == -1) {
            return Result.error("用户不存在");
        } else if (code == -2) {
            return Result.error("你已经创建过店铺，不可重复入驻");
        } else if (code == -3) {
            return Result.error("当前角色不是商家，无法创建店铺");
        } else if (code > 0) {
            return Result.success("商家入驻成功！");
        } else {
            return Result.error("入驻失败，服务器异常");
        }
    }

    // ====================== 2. 根据ID查询店铺详情 ======================
    @GetMapping("/detail/{id}")
    public Result getMerchantById(@PathVariable Integer id) {
        Merchant merchant = merchantService.getMerchantById(id.longValue());
        if (merchant == null) {
            return Result.error("店铺不存在或已被封禁");
        }
        return Result.success(merchant);
    }

    // ====================== 3. 获取当前登录用户的店铺 ======================
    @GetMapping("/myShop")
    public Result getMyShop(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");

        Integer userId = user.getId().intValue();
        Merchant merchant = merchantService.getMerchantByUserId(userId.longValue());
        if (merchant == null) return Result.error("你还不是商家");

        return Result.success(merchant);
    }

    // ====================== 4. 获取所有有效店铺（用户端浏览） ======================
    @GetMapping("/validList")
    public Result getAllValidMerchants() {
        List<Merchant> list = merchantService.getAllValidMerchants();
        return Result.success(list);
    }

    // ====================== 5. 修改店铺营业状态 ======================
    @PostMapping("/updateStatus")
    public Result updateMerchantStatus(@RequestParam Integer status, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");

        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无店铺信息");

        boolean success = merchantService.updateMerchantStatus(merchant.getId().longValue(), status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    // ====================== 6. 修改店铺信息 ======================
    @PostMapping("/updateInfo")
    public Result updateMerchantInfo(@RequestBody Merchant merchant, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return Result.error("未登录");

        Merchant myShop = merchantService.getMerchantByUserId(user.getId().longValue());
        if (myShop == null) return Result.error("无店铺信息");

        merchant.setId(myShop.getId());
        boolean success = merchantService.updateMerchantInfo(merchant);
        return success ? Result.success("店铺信息更新成功") : Result.error("更新失败");
    }

    // ====================== 7. 搜索店铺 ======================
    @GetMapping("/search")
    public Result searchMerchants(@RequestParam String keyword) {
        List<Merchant> list = merchantService.searchMerchants(keyword);
        return Result.success(list);
    }

    // ====================== 8. 分页查询店铺 ======================
    @GetMapping("/page")
    public Result getMerchantsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Merchant> list = merchantService.getMerchantsByPage(pageNum, pageSize);
        return Result.success(list);
    }

    // ====================== 9. 更新店铺评分 ======================
    @PostMapping("/updateRating")
    public Result updateMerchantRating(@RequestParam Integer id, @RequestParam Double newRating) {
        boolean success = merchantService.updateMerchantRating(id.longValue(), newRating);
        return success ? Result.success("评分更新成功") : Result.error("评分更新失败（范围0-5）");
    }

    // ====================== 10. 管理员封禁店铺 ======================
    @PostMapping("/ban/{id}")
    public Result banMerchant(@PathVariable Integer id) {
        boolean success = merchantService.banMerchant(id.longValue());
        return success ? Result.success("店铺已封禁") : Result.error("封禁失败");
    }
}