package com.liftlab.analytics.aggregation.service;

import com.liftlab.analytics.aggregation.AnalyticsStore;
import com.liftlab.analytics.common.constants.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisAnalyticsStore implements AnalyticsStore {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addActiveUser(String userId, double scoreSeconds) {
        redisTemplate.opsForZSet().add(RedisKeys.ACTIVE_USERS, userId, scoreSeconds);
    }

    @Override
    public void removeActiveUsersWithScoreBefore(double maxScoreExclusive) {
        redisTemplate.opsForZSet().removeRangeByScore(RedisKeys.ACTIVE_USERS, 0, maxScoreExclusive);
    }

    @Override
    public void expireActiveUsersKey(long minutes) {
        redisTemplate.expire(RedisKeys.ACTIVE_USERS, minutes, TimeUnit.MINUTES);
    }

    @Override
    public long getActiveUserCount() {
        Long count = redisTemplate.opsForZSet().zCard(RedisKeys.ACTIVE_USERS);
        return count == null ? 0 : count;
    }

    @Override
    public void addUserSession(String userId, String sessionId, double scoreSeconds) {
        String key = RedisKeys.userSessions(userId);
        redisTemplate.opsForZSet().add(key, sessionId, scoreSeconds);
    }

    @Override
    public void removeUserSessionsWithScoreBefore(String userId, double maxScoreExclusive) {
        String key = RedisKeys.userSessions(userId);
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, maxScoreExclusive);
    }

    @Override
    public void expireUserSessionsKey(String userId, long minutes) {
        redisTemplate.expire(RedisKeys.userSessions(userId), minutes, TimeUnit.MINUTES);
    }

    @Override
    public long getActiveSessionCount(String userId) {
        Long count = redisTemplate.opsForZSet().zCard(RedisKeys.userSessions(userId));
        return count == null ? 0 : count;
    }

    @Override
    public void incrementPageView(String bucket, String pageUrl) {
        String key = RedisKeys.pageViewsBucket(bucket);
        redisTemplate.opsForHash().increment(key, pageUrl, 1);
    }

    @Override
    public void expirePageViewBucket(String bucket, long minutes) {
        redisTemplate.expire(RedisKeys.pageViewsBucket(bucket), minutes, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Long> getPageViewCountsForBucket(String bucketKey) {
        String key = RedisKeys.pageViewsBucket(bucketKey);
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
        Map<String, Long> out = new HashMap<>();
        for (Map.Entry<Object, Object> e : raw.entrySet()) {
            String url = String.valueOf(e.getKey());
            String val = String.valueOf(e.getValue());
            try {
                long parsed = Long.parseLong(val);
                out.put(url, parsed);
            } catch (NumberFormatException ex) {
                log.warn("Skipping non-numeric page view count for key {} field {}: {}", key, url, val);
            }
        }
        return out;
    }
}
