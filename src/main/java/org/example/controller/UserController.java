package org.example.controller;

import org.example.Utils.JwtUtils;
import org.example.Utils.SecurityUtil;
import org.example.config.RoleRedirectProperties;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/takeout/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRedirectProperties roleRedirectProperties;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        User loginUser = userService.login(user.getAccount(), user.getPassword());
        if (loginUser == null) return Result.error("登录失败：账号或密码错误");
        String token = jwtUtils.generateToken(loginUser.getUsername(), loginUser.getRole());
        String redirectUrl = roleRedirectProperties.getRedirectUrlByRole(loginUser.getRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("redirectUrl", redirectUrl);
        map.put("role", loginUser.getRole());
        map.put("username", loginUser.getUsername());
        map.put("userId", loginUser.getId());
        map.put("phone", loginUser.getPhoneNumber());
        map.put("address", loginUser.getAddress());
        return Result.success(map);

    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        int res = userService.Register(user);
        return switch (res) {
            case 0 -> Result.error("用户名已存在");
            case -1 -> Result.error("手机号已注册");
            case -2 -> Result.error("用户名不能设置为手机号格式");
            default -> Result.success("注册成功");
        };
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        User user = SecurityUtil.getCurrentUser();
        if (user == null) return Result.error("请先登录");
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody User user) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) return Result.error("请先登录");
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!user.getPhoneNumber().matches("^1[3-9]\\d{9}$"))
                return Result.error("手机号必须为11位有效号码");
            currentUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getAvatar() != null) currentUser.setAvatar(user.getAvatar());
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty())
            currentUser.setUsername(user.getUsername());
        if (user.getAddress() != null) currentUser.setAddress(user.getAddress());
        boolean updated = userService.UpdateBasicInfo(currentUser);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam long userId, @RequestParam String newPassword) {
        return userService.UpdatePassword(userId, newPassword)
                ? Result.success() : Result.error("密码修改失败");
    }

    @PostMapping("/updatePhoneNum")
    public Result updatePhoneNum(@RequestParam long userId,
                                  @RequestParam String oldPhoneNum,
                                  @RequestParam String newPhoneNum) {
        if (!newPhoneNum.matches("^1[3-9]\\d{9}$"))
            return Result.error("手机号必须为11位有效号码");
        if (userService.IsPhoneNumberExists(newPhoneNum))
            return Result.error("该手机号已被使用");
        return userService.UpdatePhoneNumber(userId, oldPhoneNum, newPhoneNum)
                ? Result.success() : Result.error("手机号修改失败");
    }

    @PostMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam long userId, @RequestParam String avatar) {
        return userService.UpdateAvatar(userId, avatar)
                ? Result.success() : Result.error("头像修改失败");
    }
}