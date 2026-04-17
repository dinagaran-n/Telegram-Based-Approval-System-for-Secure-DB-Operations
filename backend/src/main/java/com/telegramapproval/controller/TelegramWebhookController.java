package com.telegramapproval.controller;

import com.telegramapproval.dto.TelegramUpdate;
import com.telegramapproval.service.TelegramService;
import com.telegramapproval.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookController {

    private final UserService userService;
    private final TelegramService telegramService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleTelegramWebhook(@RequestBody TelegramUpdate update) {
        log.info("Received Telegram webhook update. ID: {}", update.getUpdateId());

        if (update.getCallbackQuery() != null) {
            String callbackData = update.getCallbackQuery().getData();
            String callbackQueryId = update.getCallbackQuery().getId();
            
            try {
                if (callbackData != null && callbackData.contains("_")) {
                    String[] parts = callbackData.split("_");
                    String action = parts[0];
                    Long requestId = Long.parseLong(parts[1]);

                    boolean isApproved = action.equals("APPROVE");
                    userService.processTelegramApproval(requestId, isApproved);
                    
                    // Answer the callback query to stop the loading circle on the Telegram button
                    telegramService.answerCallbackQuery(callbackQueryId, isApproved ? "Request Approved!" : "Request Rejected!");
                }
            } catch (Exception e) {
                log.error("Error processing callback data {}", callbackData, e);
                telegramService.answerCallbackQuery(callbackQueryId, "Error processing request!");
            }
        }

        // Always return 200 OK so Telegram knows we received the webhook
        return ResponseEntity.ok("OK");
    }
}
