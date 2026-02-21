package com.liftlab.analytics.common.constants;

/**
 * Redis key names for analytics. Kept in one place so key shape and naming are consistent
 * and easy to change (e.g. for multi-tenancy or env prefix).
 */
public final class RedisKeys {

    private RedisKeys() {}

    public static final String KEY_PREFIX = "analytics:";

    /** Rate limit: analytics:rate_limit:global:{epochSecond} */
    public static String rateLimitGlobal(long epochSecond) {
        return KEY_PREFIX + "rate_limit:global:" + epochSecond;
    }

    /** Active users ZSet: analytics:active_users */
    public static final String ACTIVE_USERS = KEY_PREFIX + "active_users";

    /** User sessions ZSet: analytics:user:{userId}:sessions */
    public static String userSessions(String userId) {
        return KEY_PREFIX + "user:" + userId + ":sessions";
    }

    /** Page views per minute bucket: analytics:page_views:{yyyyMMddHHmm} */
    public static String pageViewsBucket(String bucket) {
        return KEY_PREFIX + "page_views:" + bucket;
    }

    /** Minute bucket format for page views (UTC). */
    public static final String PAGE_VIEW_BUCKET_FORMAT = "yyyyMMddHHmm";
}
