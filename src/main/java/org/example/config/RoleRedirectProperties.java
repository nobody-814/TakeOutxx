package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "role.redirect")
public class RoleRedirectProperties {
    // getter/setter
    private Map<Integer, String> redirect = new HashMap<>();

    // 获取跳转地址
    public String getRedirectUrlByRole(Integer role) {
        return redirect.getOrDefault(role, "/consumer/home");
    }

}