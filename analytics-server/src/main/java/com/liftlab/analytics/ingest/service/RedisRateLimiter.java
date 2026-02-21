package com.liftlab.analytics.ingest.service;

import com.liftlab.analytics.common.constants.RedisKeys;
import com.liftlab.analytics.config.AnalyticsProperties;
import com.liftlab.analytics.ingest.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimiter implements RateLimiter {

    private static final int KEY_TTL_SECONDS = 2;

    private final StringRedisTemplate redisTemplate;
    private final AnalyticsProperties config;

    @Override
    public boolean allow() {
        long epochSecond = Instant.now().getEpochSecond();
        String key = RedisKeys.rateLimitGlobal(epochSecond);
        Long count = redisTemplate.opsForValue().increment(key);
        // Only set TTL when the key was newly created to avoid repeatedly resetting it.
        if (count != null && count == 1L) {
            redisTemplate.expire(key, KEY_TTL_SECONDS, TimeUnit.SECONDS);
        }
        boolean allowed = count != null && count <= config.getRateLimitEventsPerSecond();
        if (!allowed) {
            log.warn("Rate limit exceeded: current count {} for window {}", count, epochSecond);
        }
        return allowed;
    }
}
