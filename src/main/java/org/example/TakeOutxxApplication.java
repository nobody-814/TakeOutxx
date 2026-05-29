package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class TakeOutxxApplication {
    public static void main(String[] args) {
        // 正确：使用 SpringApplication 类的 run 方法
        SpringApplication.run(TakeOutxxApplication.class, args);
    }
}