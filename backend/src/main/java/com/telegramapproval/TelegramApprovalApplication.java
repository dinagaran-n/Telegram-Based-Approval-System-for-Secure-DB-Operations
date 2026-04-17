package com.telegramapproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TelegramApprovalApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramApprovalApplication.class, args);
    }
}
