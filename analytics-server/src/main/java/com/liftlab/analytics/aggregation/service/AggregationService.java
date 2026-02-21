package com.liftlab.analytics.aggregation.service;

import com.liftlab.analytics.common.constants.EventTypes;
import com.liftlab.analytics.common.constants.RedisKeys;
import com.liftlab.analytics.common.model.EventRequest;
import com.liftlab.analytics.aggregation.AnalyticsStore;
import com.liftlab.analytics.config.AnalyticsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private static final DateTimeFormatter BUCKET_FMT =
            DateTimeFormatter.ofPattern(RedisKeys.PAGE_VIEW_BUCKET_FORMAT);

    private final AnalyticsStore store;
    private final AnalyticsProperties config;

    public void process(EventRequest event) {
        // Bucket format maps to the per-minute buckets displayed on the LiftLab "Top Pages" widget.
        long eventEpochSec = event.getEventTimeInstant().getEpochSecond();
        int activeWindowSec = config.getActiveWindowMinutes() * 60;
        double scoreThreshold = eventEpochSec - activeWindowSec;

        store.addActiveUser(event.getUserId(), eventEpochSec);
        store.expireActiveUsersKey(config.getActiveWindowMinutes() + 1);
        store.removeActiveUsersWithScoreBefore(scoreThreshold);

        store.addUserSession(event.getUserId(), event.getSessionId(), eventEpochSec);
        store.removeUserSessionsWithScoreBefore(event.getUserId(), scoreThreshold);
        store.expireUserSessionsKey(event.getUserId(), config.getActiveWindowMinutes() + 5);

        if (EventTypes.PAGE_VIEW.equalsIgnoreCase(event.getEventType())) {
            String bucket = LocalDateTime.ofInstant(event.getEventTimeInstant(), ZoneOffset.UTC).format(BUCKET_FMT);
            store.incrementPageView(bucket, event.getPageUrl());
            store.expirePageViewBucket(bucket, config.getPageViewWindowMinutes() + 2);
        }

        log.debug("Processed event: user={}, type={}", event.getUserId(), event.getEventType());
    }
}
