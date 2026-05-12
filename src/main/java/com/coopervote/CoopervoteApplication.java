package com.coopervote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoopervoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoopervoteApplication.class, args);
    }
}