package com.example.manger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MangerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangerApplication.class, args);
    }

}
