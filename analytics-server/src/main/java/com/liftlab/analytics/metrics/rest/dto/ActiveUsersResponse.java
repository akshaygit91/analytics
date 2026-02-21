package com.liftlab.analytics.metrics.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActiveUsersResponse(
        @JsonProperty("activeUsersCount") long activeUsersCount,
        @JsonProperty("windowMinutes") int windowMinutes
) {}
