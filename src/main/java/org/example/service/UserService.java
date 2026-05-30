package org.example.service;

import org.example.domain.User;

public interface UserService {
    //========================注册登陆=========================
    //用户注册
    int Register(User user);

    //用户登陆
    User login(String account,String password);

    //检查用户名是否存在
    boolean IsUsernameExists(String username);

    //检查手机号是否存在
    boolean IsPhoneNumberExists(String PhoneNumber);


    //=======================用户修改基本信息====================
    //用户修改手机号
    boolean UpdatePhoneNumber(long UserId,String OldPhoneNumber,String NewPhoneNumber);

    //用户修改基本信息
    boolean UpdateBasicInfo(User user);

    //用户修改密码
    boolean UpdatePassword(long userId,String NewPassword);

    //用户修改头像
    Boolean UpdateAvatar(long userId,String avatar);


    User getUserByUsername(String username);

}
