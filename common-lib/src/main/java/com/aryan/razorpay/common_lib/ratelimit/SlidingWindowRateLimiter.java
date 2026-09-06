package com.aryan.razorpay.common_lib.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * This code has a limitation that it won't work properly
 * when multiple threads executes this code simultaneously.
 * So, we rely on lua scripts that run on redis server.
 * It is fast and thread safe.
 */
@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
        long nowMs = System.currentTimeMillis();
        long floorMs = nowMs - (windowSeconds * 1000);

        String redisKey = "ratelimit:sliding:"+key;

        var zSet = redisTemplate.opsForZSet();
        zSet.removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, floorMs);

        Long count = zSet.zCard(redisKey);
        long current = count != null ? count : 0;

        if (current >= maxRequestAllowed) {
            var oldest = zSet.rangeWithScores(redisKey, 0, 0);
            int retryAfter = 1;

            if (oldest != null && !oldest.isEmpty()) {
                Double oldestScore = oldest.iterator().next().getScore();
                if (oldestScore != null) {
                    long windowExpiresMs = oldestScore.longValue() * windowSeconds;
                    retryAfter = (int) Math.ceil((windowExpiresMs - nowMs)/1000.0);
                }
            }

            return RateLimitResult.denied(retryAfter);
        }

        zSet.add(redisKey, UUID.randomUUID().toString(), nowMs);
        redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds + 1));
        return RateLimitResult.allowed((int) (maxRequestAllowed - current - 1));
    }
}
