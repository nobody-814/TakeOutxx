package org.example.service.impl;

import org.example.domain.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public int Register(User user) {

        String inputUsername = user.getUsername();
        String phone = user.getPhoneNumber();

        // ========== 自动生成随机用户名逻辑 ==========
        // 用户没填写用户名 / 用户名空串 → 自动生成
        if (inputUsername == null || inputUsername.trim().isEmpty()) {
            String autoUsername;
            // 循环生成，直到数据库不存在该用户名，保证唯一
            do {
                // 格式：phone_ + 手机号后6位  辨识度高
                autoUsername = "phone_" + phone.substring(5);
                // 也可以用随机数：autoUsername = "user_" + String.format("%06d", (int)(Math.random()*1000000));
            } while (userMapper.countByUsername(autoUsername) > 0);

            // 把生成好的用户名塞入实体，插入数据库
            user.setUsername(autoUsername);
        } else {
            // 用户手动填了用户名
            // 禁止用户名是11位手机号，杜绝登录串号
            if (inputUsername.matches("^1[3-9]\\d{9}$")) {
                return -2;
            }
            // 校验自定义用户名重复
            if (userMapper.countByUsername(inputUsername) > 0) {
                return 0;
            }
        }

        // 校验手机号重复
        if (userMapper.countByPhoneNumber(phone) > 0) {
            return -1;
        }

        // 填充默认字段
        user.setRole(0);
        user.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 插入数据库，自动把随机用户名存进去
        return userMapper.insert(user);
    }

    @Override
    public User login(String account, String password) {
        User user = null;

        // 1. 判断：输入的是不是 11 位手机号
        if (account.matches("^1[3-9]\\d{9}$")) {
            // 是手机号 → 只查手机号（精确匹配，不串）
            if (userMapper.countByPhoneNumber(account) > 0) {
                user = userMapper.findByPhone(account); // 你需要加这个极简mapper
            }
        } else {
            // 是用户名 → 只查用户名（精确匹配，不串）
            if (userMapper.countByUsername(account) > 0) {
                user = userMapper.findByUsername(account); // 你需要加这个极简mapper
            }
        }

        // 没查到用户
        if (user == null) {
            return null;
        }

        // 密码校验
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    @Override
    public boolean IsUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean IsPhoneNumberExists(String phoneNumber) {
        return userMapper.countByPhoneNumber(phoneNumber) > 0;
    }

    @Override
    public boolean UpdatePhoneNumber(long UserId,String OldPhoneNumber, String NewPhoneNumber) {
        return userMapper.updatePhone(UserId,OldPhoneNumber,NewPhoneNumber,LocalDateTime.now())>0;
    }

    @Override
    public boolean UpdateBasicInfo(User user) {
       return userMapper.updateBasicInfo(user)>0;
    }

    @Override
    public boolean UpdatePassword(long userId, String NewPassword) {
        return userMapper.updatePassword(userId,NewPassword)>0;

    }

    @Override
    public Boolean UpdateAvatar(long userId, String avatar) {
        return userMapper.updateAvatar(userId,avatar,LocalDateTime.now())>0;
    }
}
