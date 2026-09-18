package com.aryan.razorpay.common_lib.idempotency;

import com.aryan.razorpay.common_lib.cache.ApiKeyCache;
import com.aryan.razorpay.common_lib.cache.RedisApiKeyCache;
import com.aryan.razorpay.common_lib.context.MerchantContext;
import com.aryan.razorpay.common_lib.ratelimit.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
public class SharedResilienceAutoConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public ApiKeyCache apiKeyCache(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisApiKeyCache(stringRedisTemplate, objectMapper);
    }

    @Bean
    public IdempotencyStore idempotencyStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisIdempotencyStore(stringRedisTemplate);
    }

    @Bean
    public IdempotencyFilter idempotencyFilter(MerchantContext merchantContext,
                                               IdempotencyStore idempotencyStore,
                                               @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        return new IdempotencyFilter(merchantContext, idempotencyStore, handlerExceptionResolver);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
    public RateLimiter fixedWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new FixedWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-window")
    public RateLimiter slidingWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new SlidingWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-window-lua")
    public RateLimiter slidingWindowLuaLimiter(StringRedisTemplate stringRedisTemplate) {
        return new SlidingWindowLuaLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "token-bucket")
    public RateLimiter tokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new TokenBucketRateLimiter(stringRedisTemplate);
    }
}
