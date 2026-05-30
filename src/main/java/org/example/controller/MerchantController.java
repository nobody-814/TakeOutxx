package org.example.controller;

import jakarta.annotation.Resource;
import org.example.Utils.JwtUtils;
import org.example.domain.Merchant;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.MerchantService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takeout/merchant")
@CrossOrigin
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    // 商家入驻（创建店铺）
    @PostMapping("/create")
    public Result createMerchant(@RequestBody Merchant merchant, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("请先登录");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Long userId = user.getId().longValue();
        int code = merchantService.createMerchant(merchant, userId);

        switch (code) {
            case -1:
                return Result.error("用户不存在");
            case -2:
                return Result.error("你已经创建过店铺，不可重复入驻");
            // 移除 -3 的判断，普通用户申请成功后会自动成为商家
            default:
                if (code > 0) {
                    // 入驻成功，角色已由 Service 层自动升级
                    return Result.success("商家入驻成功！你的账号已升级为商家，请重新登录或刷新页面。");
                } else {
                    return Result.error("入驻失败，服务器异常");
                }
        }
    }

    // 以下方法保持不变，仅将 userService.login 替换为 getUserByUsername 的已在之前完成
    // 省略其余方法，与修改后的版本一致...
    @GetMapping("/detail/{id}")
    public Result getMerchantById(@PathVariable Integer id) {
        Merchant merchant = merchantService.getMerchantById(id.longValue());
        if (merchant == null) {
            return Result.error("店铺不存在或已被封禁");
        }
        return Result.success(merchant);
    }

    @GetMapping("/myShop")
    public Result getMyShop(@RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("未登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) {
            return Result.error("你还不是商家");
        }
        return Result.success(merchant);
    }

    @GetMapping("/validList")
    public Result getAllValidMerchants() {
        List<Merchant> list = merchantService.getAllValidMerchants();
        return Result.success(list);
    }

    @PostMapping("/updateStatus")
    public Result updateMerchantStatus(@RequestParam Integer status, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("未登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Merchant merchant = merchantService.getMerchantByUserId(user.getId().longValue());
        if (merchant == null) {
            return Result.error("无店铺信息");
        }
        boolean success = merchantService.updateMerchantStatus(merchant.getId().longValue(), status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @PostMapping("/updateInfo")
    public Result updateMerchantInfo(@RequestBody Merchant merchant, @RequestHeader("token") String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error("未登录");
        }
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Merchant myShop = merchantService.getMerchantByUserId(user.getId().longValue());
        if (myShop == null) {
            return Result.error("无店铺信息");
        }
        merchant.setId(myShop.getId());
        boolean success = merchantService.updateMerchantInfo(merchant);
        return success ? Result.success("店铺信息更新成功") : Result.error("更新失败");
    }

    @GetMapping("/search")
    public Result searchMerchants(@RequestParam String keyword) {
        List<Merchant> list = merchantService.searchMerchants(keyword);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result getMerchantsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Merchant> list = merchantService.getMerchantsByPage(pageNum, pageSize);
        return Result.success(list);
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