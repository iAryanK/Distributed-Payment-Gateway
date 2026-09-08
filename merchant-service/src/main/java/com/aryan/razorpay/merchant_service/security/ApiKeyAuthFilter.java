package com.aryan.razorpay.merchant_service.security;

import com.aryan.razorpay.common_lib.exceptions.RateLimitException;
import com.aryan.razorpay.common_lib.ratelimit.RateLimitResult;
import com.aryan.razorpay.common_lib.ratelimit.RateLimiter;
import com.aryan.razorpay.merchant_service.cache.ApiKeyCache;
import com.aryan.razorpay.merchant_service.cache.ApiKeyCacheEntry;
import com.aryan.razorpay.merchant_service.entities.ApiKey;
import com.aryan.razorpay.merchant_service.repositories.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:60}")
    private int maxRequestAllowed;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Basic ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String[] credentials = decode(requestTokenHeader);
            if (credentials == null)
                throw new BadRequestException("Malformed API key header");

            String keyId = credentials[0];
            String rawSecret = credentials[1];

            ApiKeyCacheEntry apiKeyEntry = apiKeyCache.get(keyId)
                    .orElseGet(() -> loadAndCache(keyId));

            if (apiKeyEntry == null || !apiKeyEntry.enabled() || !secretMatches(rawSecret, apiKeyEntry))
                throw new BadRequestException("Invalid or missing API Key");

            RateLimitResult rateLimitResult = rateLimiter.check("apiKey:"+keyId, maxRequestAllowed, 60);
            if (!rateLimitResult.isAllowed()) {
                log.warn("Too many requests, KeyId={}", keyId);
                throw new RateLimitException("Too many requests", rateLimitResult.retryAfterSeconds());
            }

            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestAllowed));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.remaining()));

            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId((apiKeyEntry.merchantId()));
            merchantContext.setKeyId(apiKeyEntry.keyId());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElse(null);
        if (apiKey == null) return null;
        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(
                apiKey.getKeyId(),
                apiKey.getMerchant().getId(),
                apiKey.getKeySecretHash(),
                apiKey.getPreviousKeySecretHash(),
                apiKey.getGracePeriodExpiresAt(),
                apiKey.getEnvironment(),
                apiKey.isEnabled()
        );
        return apiKeyCacheEntry;
    }

    private boolean secretMatches(String rawSecret, ApiKeyCacheEntry apiKey) {
        if (passwordEncoder.matches(rawSecret, apiKey.keySecretHash()))
            return true;

        return apiKey.isInGracePeriod() &&
                apiKey.previousKeySecretHash() != null &&
                passwordEncoder.matches(rawSecret, apiKey.previousKeySecretHash());
    }

    private String[] decode(String header) {
        String encoded = header.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if (colon < 1) return null;

        return new String[]{decoded.substring(0, colon), decoded.substring(colon + 1)};
    }
}
