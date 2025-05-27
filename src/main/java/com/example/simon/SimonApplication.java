package com.example.simon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.simon.mapper")
public class SimonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimonApplication.class, args);
    }

}
