package com.liftlab.analytics.ingest;

/**
 * Allows swapping rate-limiting strategy (e.g. Redis, in-memory, token bucket) without changing ingest flow.
 */
public interface RateLimiter {

    /**
     * @return true if the request is allowed, false if rate limit exceeded
     */
    boolean allow();
}
