package org.example.interceptor;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Utils.JwtUtils;
import org.example.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        // 只放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 从这里开始，只做Token校验
        String token = request.getHeader("token");
        if (token == null || token.isBlank()) {
            response.getWriter().write(JSON.toJSONString(Result.error("未登录")));
            return false;
        }

        try {
            if (!jwtUtils.validateToken(token)) {
                response.getWriter().write(JSON.toJSONString(Result.error("未登录或已过期")));
                return false;
            }
        } catch (Exception e) {
            response.getWriter().write(JSON.toJSONString(Result.error("未登录")));
            return false;
        }

        return true;
    }
}