package org.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户表（含客户、商家、骑手）
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一ID
     */
    private Integer id;
    /*
    * 用户登录注册的账号（手机号/用户名）
    */
    private String account; // 登录用的输入框（用户名/手机号）

    /**
     * 用户名（显示用）
     */
    private String username;



    /**
     * 密码哈希（建议加密存储，如bcrypt）
     */
    private String password;

    /**
     * 手机号（登录/联系用，唯一）
     */
    @JsonProperty("phone")
    private String phoneNumber;

    /**
     * 角色：0-客户，1-商家，2-骑手
     */
    private Integer role;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 账号状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // 无参构造函数
    public User() {
    }

    // 全参构造函数
    public User(Integer id, String account, String username, String password, String phoneNumber, Integer role, String avatar, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.account = account;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}