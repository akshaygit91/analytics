package com.liftlab.analytics.aggregation;

import java.util.Map;

/**
 * Abstraction over where we store and read rolling metrics for LiftLab's dashboards.
 * Implementations should favor low-latency operations (e.g. Redis) so real-time widgets
 * like "active users" and "top pages" update quickly without blocking ingestion.
 */
public interface AnalyticsStore {

    void addActiveUser(String userId, double scoreSeconds);

    void removeActiveUsersWithScoreBefore(double maxScoreExclusive);

    void expireActiveUsersKey(long minutes);

    long getActiveUserCount();

    void addUserSession(String userId, String sessionId, double scoreSeconds);

    void removeUserSessionsWithScoreBefore(String userId, double maxScoreExclusive);

    void expireUserSessionsKey(String userId, long minutes);

    long getActiveSessionCount(String userId);

    void incrementPageView(String bucketKey, String pageUrl);

    void expirePageViewBucket(String bucketKey, long minutes);

    Map<String, Long> getPageViewCountsForBucket(String bucketKey);
}
