package pl.minecodes.metric.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.minecodes.metric.backend.util.AddressUtil;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  private final Logger logger = LoggerFactory.getLogger(RateLimiterFilter.class);

  private final Bandwidth bandwidth = Bandwidth.builder()
      .capacity(300)
      .refillIntervally(300, Duration.ofMinutes(5))
      .build();

  private final Cache<String, Bucket> rateLimitCache = Caffeine.newBuilder()
      .maximumSize(10000)
      .expireAfterAccess(Duration.ofHours(1))
      .build();

  public RateLimiterFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getMethod().equals("GET") || request.getMethod().equals("OPTIONS")) {
      filterChain.doFilter(request, response);
      return;
    }

    String realRequestAddress = AddressUtil.getRealRequestAddress(request);
    Bucket bucket = this.rateLimitCache.get(realRequestAddress, k -> Bucket.builder().addLimit(this.bandwidth).build());

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      this.logger.warn("Request from {} blocked by rate limit filter.", realRequestAddress);
      response.setContentType("application/json");
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      this.objectMapper.writeValue(response.getWriter(), Map.of("message", "Too many requests, try again later."));
    }
  }
}