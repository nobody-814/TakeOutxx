package org.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String account;
    private String username;
    private String password;
    @JsonProperty("phone")
    private String phoneNumber;
    private Integer role;
    private String avatar;
    private Integer status;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(Integer id, String account, String username, String password, String phoneNumber, Integer role, String avatar, Integer status, String address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.account = account;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.avatar = avatar;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}