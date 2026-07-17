package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class TakeOutxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeOutxxApplication.class, args);
    }
}