package com.payment.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static java.time.LocalTime.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_REQUEST_PER_MINUTE = 10;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        String clientIdentifier = getClientIdentifier(request);

        long currentMin = Instant.now().getEpochSecond() / 60;
        String redisKey = "rate:limit:"+clientIdentifier+":"+currentMin;

        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);

            if(count!=null){
                if(count==1){
                    redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
                }
                if(count > MAX_REQUEST_PER_MINUTE){
                    log.warn("Rate limit exceeded for client: {}. Count in current minute: {}", clientIdentifier, count);

                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"success\":false,\"message\":\"Rate limit exceeded. Maximum "
                                    + MAX_REQUEST_PER_MINUTE + " requests per minute allowed.\",\"data\":null}"
                    );
                    return ;
                }
            }
        }catch (Exception e){
            log.error("Redis connection error in RateLimitingFilter. Failing-open.", e);

        }
        filterChain.doFilter(request,response);
    }

    private String getClientIdentifier(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
            return auth.getName();
        }
        String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader==null||xfHeader.trim().isEmpty()){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
