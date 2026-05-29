package org.example.controller;

import org.example.Utils.JwtUtils;
import org.example.config.RoleRedirectProperties;
import org.example.domain.Result;
import org.example.domain.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/takeout/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RoleRedirectProperties roleRedirectProperties;

    // 登录：单输入框（支持用户名/手机号）
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        // 这里传 account，不是 username！
        User loginUser = userService.login(user.getAccount(), user.getPassword());

        if (loginUser == null) {
            return Result.error("登录失败：账号或密码错误");
        }


        String token = jwtUtils.generateToken(loginUser.getUsername(), loginUser.getRole());
        String redirectUrl = roleRedirectProperties.getRedirectUrlByRole(loginUser.getRole());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("redirectUrl", redirectUrl);
        map.put("role", loginUser.getRole());
        map.put("username", loginUser.getUsername());
        map.put("userId", loginUser.getId());

        return Result.success(map);
    }

    // 注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        int res = userService.Register(user);

        if (res == 0) return Result.error("用户名已存在");
        if (res == -1) return Result.error("手机号已注册");
        if (res == -2) return Result.error("用户名不能设置为手机号格式");

        return Result.success("注册成功");
    }

    // 修改信息
    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody User user) {
        return userService.UpdateBasicInfo(user)
                ? Result.success()
                : Result.error("修改失败");
    }

    // 修改密码
    @PostMapping("/updatePassword")
    public Result updatePassword(
            @RequestParam long userId,
            @RequestParam String newPassword) {
        return userService.UpdatePassword(userId, newPassword)
                ? Result.success()
                : Result.error("密码修改失败");
    }


    // 修改手机号
    @PostMapping("/updatePhoneNum")
    public Result updatePhoneNum(
            @RequestParam long userId,
            @RequestParam String oldPhoneNum,
            @RequestParam String newPhoneNum) {

        if (userService.IsPhoneNumberExists(newPhoneNum)) {
            return Result.error("该手机号已被使用");
        }

        return userService.UpdatePhoneNumber(userId, oldPhoneNum, newPhoneNum)
                ? Result.success()
                : Result.error("手机号修改失败");
    }

    // 修改头像
    @PostMapping("/updateAvatar")
    public Result updateAvatar(
            @RequestParam long userId,
            @RequestParam String avatar) {
        return userService.UpdateAvatar(userId, avatar)
                ? Result.success()
                : Result.error("头像修改失败");
    }
}