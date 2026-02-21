package com.liftlab.analytics.metrics.service;

import com.liftlab.analytics.aggregation.AnalyticsStore;
import com.liftlab.analytics.common.constants.RedisKeys;
import com.liftlab.analytics.config.AnalyticsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private static final DateTimeFormatter BUCKET_FMT =
            DateTimeFormatter.ofPattern(RedisKeys.PAGE_VIEW_BUCKET_FORMAT);

    private final AnalyticsStore store;
    private final AnalyticsProperties config;

    public long getActiveUsersCount() {
        return store.getActiveUserCount();
    }

    public long getActiveSessionsForUser(String userId) {
        return store.getActiveSessionCount(userId);
    }

    public List<PageViewCount> getTopPages(int topN) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int windowMinutes = config.getPageViewWindowMinutes();
        Map<String, Long> merged = new HashMap<>();

        for (int i = 0; i < windowMinutes; i++) {
            String bucket = now.minusMinutes(i).format(BUCKET_FMT);
            Map<String, Long> bucketCounts = store.getPageViewCountsForBucket(bucket);
            bucketCounts.forEach((url, count) -> merged.merge(url, count, Long::sum));
        }

        return merged.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(e -> new PageViewCount(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public record PageViewCount(String url, long count) {}
}
