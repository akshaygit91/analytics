package com.liftlab.analytics.ingest;

import com.liftlab.analytics.config.AnalyticsProperties;
import com.liftlab.analytics.common.constants.RedisKeys;
import com.liftlab.analytics.ingest.service.RedisRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RedisRateLimiterTest {

    @Mock
    StringRedisTemplate redisTemplate;

    @Mock
    ValueOperations<String, String> valueOps;

    AnalyticsProperties props;

    RedisRateLimiter limiter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        props = new AnalyticsProperties();
        props.setRateLimitEventsPerSecond(5);
        limiter = new RedisRateLimiter(redisTemplate, props);
    }

    @Test
    void setsTtlWhenCountIsOne() {
        long epoch = Instant.now().getEpochSecond();
        String key = RedisKeys.rateLimitGlobal(epoch);

        when(valueOps.increment(key)).thenReturn(1L);

        boolean allowed = limiter.allow();

        verify(redisTemplate).expire(key, 2, java.util.concurrent.TimeUnit.SECONDS);
        assertTrue(allowed);
    }

    @Test
    void doesNotResetTtlWhenCountGreaterThanOne() {
        long epoch = Instant.now().getEpochSecond();
        String key = RedisKeys.rateLimitGlobal(epoch);

        when(valueOps.increment(key)).thenReturn(2L);

        boolean allowed = limiter.allow();

        verify(redisTemplate, never()).expire(key, 2, java.util.concurrent.TimeUnit.SECONDS);
        assertTrue(allowed);
    }
}
