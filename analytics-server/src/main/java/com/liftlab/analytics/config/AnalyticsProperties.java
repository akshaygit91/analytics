package com.liftlab.analytics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Data
@Validated
@ConfigurationProperties(prefix = "analytics")
public class AnalyticsProperties {

    @Positive
    @Max(10_000)
    private int rateLimitEventsPerSecond = 100;

    @Min(1)
    @Max(60)
    private int activeWindowMinutes = 5;

    @Min(1)
    @Max(120)
    private int pageViewWindowMinutes = 15;
}
