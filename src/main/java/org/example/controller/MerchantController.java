package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.SecurityUtil;
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

    @PostMapping("/create")
    public Result createMerchant(@RequestBody Merchant merchant) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        int code = merchantService.createMerchant(merchant, user.getId().longValue());
        return switch (code) {
            case -1 -> Result.error("用户不存在");
            case -2 -> Result.error("你已经创建过店铺，不可重复入驻");
            default -> code > 0
                    ? Result.success("商家入驻成功！账号已升级为商家，请重新登录或刷新页面。")
                    : Result.error("入驻失败，服务器异常");
        };
    }

    @GetMapping("/detail/{id}")
    public Result getMerchantById(@PathVariable Integer id) {
        Merchant merchant = merchantService.getMerchantById(id.longValue());
        return merchant == null ? Result.error("店铺不存在或已被封禁") : Result.success(merchant);
    }

    @GetMapping("/myShop")
    public Result getMyShop() {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("未登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        return merchant == null ? Result.error("你还不是商家") : Result.success(merchant);
    }

    @GetMapping("/validList")
    public Result getAllValidMerchants() {
        return Result.success(merchantService.getAllValidMerchants());
    }

    @PostMapping("/updateStatus")
    public Result updateMerchantStatus(@RequestParam Integer status) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("未登录");
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) return Result.error("无店铺信息");
        boolean success = merchantService.updateMerchantStatus(merchant.getId().longValue(), status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @PostMapping("/updateInfo")
    public Result updateMerchantInfo(@RequestBody Merchant merchant) {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("未登录");
        Merchant myShop = merchantService.getMerchantByUserId(user.getId().longValue());
        if (myShop == null) return Result.error("无店铺信息");
        merchant.setId(myShop.getId());
        boolean success = merchantService.updateMerchantInfo(merchant);
        return success ? Result.success("店铺信息更新成功") : Result.error("更新失败");
    }

    @GetMapping("/search")
    public Result searchMerchants(@RequestParam String keyword) {
        return Result.success(merchantService.searchMerchants(keyword));
    }

    @GetMapping("/page")
    public Result getMerchantsByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(merchantService.getMerchantsByPage(pageNum, pageSize));
    }

    @PostMapping("/updateRating")
    public Result updateMerchantRating(@RequestParam Integer id, @RequestParam Double newRating) {
        boolean success = merchantService.updateMerchantRating(id.longValue(), newRating);
        return success ? Result.success("评分更新成功（范围0-5）") : Result.error("评分更新失败");
    }

    @PostMapping("/ban/{id}")
    public Result banMerchant(@PathVariable Integer id) {
        boolean success = merchantService.banMerchant(id.longValue());
        return success ? Result.success("店铺已封禁") : Result.error("封禁失败");
    }
}