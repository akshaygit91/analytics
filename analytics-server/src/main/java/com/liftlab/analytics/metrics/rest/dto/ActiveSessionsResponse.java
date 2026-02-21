package com.liftlab.analytics.metrics.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActiveSessionsResponse(
        @JsonProperty("userId") String userId,
        @JsonProperty("activeSessionsCount") long activeSessionsCount,
        @JsonProperty("windowMinutes") int windowMinutes
) {}
