package com.telegramapproval.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegramapproval.model.ActionType;
import com.telegramapproval.model.PendingRequest;
import com.telegramapproval.model.RequestStatus;
import com.telegramapproval.repository.PendingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final PendingRequestRepository requestRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Async("telegramTaskExecutor")
    public void sendApprovalMessage(PendingRequest request) {
        log.info("Sending approval message to Telegram for request ID: {}", request.getId());
        
        try {
            Map<String, String> payloadMap = objectMapper.readValue(request.getPayload(), Map.class);
            
            String actionEmoji = getActionEmoji(request.getActionType());
            String text = String.format(
                    "<b>%s New Request #%d</b>\n" +
                    "Action: <b>%s</b>\n" +
                    "━━━━━━━━━━━━━━━━\n" +
                    "Name: %s\n" +
                    "Email: %s\n" +
                    "━━━━━━━━━━━━━━━━\n" +
                    "<i>Please approve or reject this action.</i>",
                    actionEmoji, request.getId(), request.getActionType(),
                    payloadMap.getOrDefault("name", "N/A"),
                    payloadMap.getOrDefault("email", "N/A")
            );

            // Create Inline Keyboard with Approve/Reject buttons
            Map<String, Object> approveButton = new HashMap<>();
            approveButton.put("text", "✅ Approve");
            approveButton.put("callback_data", "APPROVE_" + request.getId());

            Map<String, Object> rejectButton = new HashMap<>();
            rejectButton.put("text", "❌ Reject");
            rejectButton.put("callback_data", "REJECT_" + request.getId());

            Map<String, Object> inlineKeyboardMarkup = new HashMap<>();
            inlineKeyboardMarkup.put("inline_keyboard", new Object[][]{{approveButton, rejectButton}});

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chat_id", chatId);
            requestBody.put("text", text);
            requestBody.put("parse_mode", "HTML");
            requestBody.put("reply_markup", inlineKeyboardMarkup);

            String url = TELEGRAM_API_URL + botToken + "/sendMessage";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");
                if (result != null && result.containsKey("message_id")) {
                    Integer messageId = (Integer) result.get("message_id");
                    request.setTelegramMessageId(messageId);
                    requestRepository.save(request);
                    log.info("Message sent successfully. Telegram message_id: {}", messageId);
                }
            } else {
                log.error("Failed to send message to Telegram. Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error sending Telegram approval message", e);
        }
    }

    @Async("telegramTaskExecutor")
    public void updateMessageStatus(Integer messageId, Long requestId, RequestStatus status, String actionText) {
        if (messageId == null) return;
        
        try {
            String statusEmoji = status == RequestStatus.APPROVED ? "✅" : "❌";
            String newText = String.format(
                    "<b>Request #%d</b>\n" +
                    "Status: %s <b>%s</b>\n" +
                    "<i>Action completed</i>",
                    requestId, statusEmoji, status
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chat_id", chatId);
            requestBody.put("message_id", messageId);
            requestBody.put("text", newText);
            requestBody.put("parse_mode", "HTML");
            
            // Remove the inline keyboard
            Map<String, Object> emptyReplyMarkup = new HashMap<>();
            emptyReplyMarkup.put("inline_keyboard", new Object[][]{});
            requestBody.put("reply_markup", emptyReplyMarkup);

            String url = TELEGRAM_API_URL + botToken + "/editMessageText";
            restTemplate.postForObject(url, requestBody, Map.class);
            log.info("Successfully updated Telegram message {} to status {}", messageId, status);
            
        } catch (Exception e) {
            log.error("Error editing Telegram message", e);
        }
    }
    
    public void answerCallbackQuery(String callbackQueryId, String text) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("callback_query_id", callbackQueryId);
            requestBody.put("text", text);
            requestBody.put("show_alert", false);

            String url = TELEGRAM_API_URL + botToken + "/answerCallbackQuery";
            restTemplate.postForObject(url, requestBody, Map.class);
        } catch (Exception e) {
            log.error("Error answering callback query", e);
        }
    }

    private String getActionEmoji(ActionType type) {
        return switch (type) {
            case ADD -> "➕";
            case UPDATE -> "✏️";
            case DELETE -> "🗑️";
        };
    }
}
