package com.liftlab.analytics.metrics.rest;

import com.liftlab.analytics.config.AnalyticsProperties;
import com.liftlab.analytics.metrics.rest.dto.ActiveSessionsResponse;
import com.liftlab.analytics.metrics.rest.dto.ActiveUsersResponse;
import com.liftlab.analytics.metrics.rest.dto.TopPagesResponse;
import com.liftlab.analytics.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private static final int TOP_PAGES_LIMIT = 5;

    private final MetricsService metricsService;
    private final AnalyticsProperties config;

    @GetMapping("/active-users")
    public ActiveUsersResponse activeUsers() {
        long count = metricsService.getActiveUsersCount();
        return new ActiveUsersResponse(count, config.getActiveWindowMinutes());
    }

    @GetMapping("/top-pages")
    public TopPagesResponse topPages() {
        return new TopPagesResponse(
                config.getPageViewWindowMinutes(),
                metricsService.getTopPages(TOP_PAGES_LIMIT));
    }

    @GetMapping("/active-sessions")
    public ActiveSessionsResponse activeSessions(@RequestParam String userId) {
        long count = metricsService.getActiveSessionsForUser(userId);
        return new ActiveSessionsResponse(userId, count, config.getActiveWindowMinutes());
    }
}
