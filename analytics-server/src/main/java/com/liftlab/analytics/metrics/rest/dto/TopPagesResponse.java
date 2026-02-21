package com.liftlab.analytics.metrics.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liftlab.analytics.metrics.service.MetricsService;

import java.util.List;

public record TopPagesResponse(
        @JsonProperty("windowMinutes") int windowMinutes,
        @JsonProperty("topPages") List<MetricsService.PageViewCount> topPages
) {}
