package com.telegramapproval.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Maps the incoming Telegram webhook update payload.
 * We only care about callback_query updates (button clicks).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramUpdate {

    @JsonProperty("update_id")
    private Long updateId;

    @JsonProperty("callback_query")
    private CallbackQuery callbackQuery;

    // ─── Inner Classes ───────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackQuery {

        /** The callback query unique ID — must be answered to dismiss the loading spinner. */
        private String id;

        /** The data attached to the inline button, e.g. "APPROVE_42" or "REJECT_42". */
        private String data;

        /** The original message that was sent by the bot. */
        private Message message;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {

            @JsonProperty("message_id")
            private Integer messageId;

            private Chat chat;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Chat {
                private Long id;
            }
        }
    }
}
