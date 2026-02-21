package com.liftlab.analytics.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liftlab.analytics.common.validation.ValidTimestamp;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Data
public class EventRequest {

    @JsonProperty("timestamp")
    @ValidTimestamp
    private String timestamp;

    @NotBlank(message = "user_id is required")
    @JsonProperty("user_id")
    private String userId;

    @NotBlank(message = "event_type is required")
    @JsonProperty("event_type")
    private String eventType;

    @NotBlank(message = "page_url is required")
    @JsonProperty("page_url")
    private String pageUrl;

    @NotBlank(message = "session_id is required")
    @JsonProperty("session_id")
    private String sessionId;

    public Instant getEventTimeInstant() {
        if (timestamp == null || timestamp.isBlank()) {
            return Instant.now();
        }
        //validation handles malformed timestamps
        return Instant.parse(timestamp);
    }
}
