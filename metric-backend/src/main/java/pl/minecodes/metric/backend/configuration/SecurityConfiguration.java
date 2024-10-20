package pl.minecodes.metric.backend.configuration;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import pl.minecodes.metric.backend.filter.AuthenticationFilter;
import pl.minecodes.metric.backend.filter.RateLimiterFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final RateLimiterFilter rateLimiterFilter;
  private final AuthenticationFilter authenticationFilter;

  public SecurityConfiguration(
      RateLimiterFilter rateLimiterFilter,
      AuthenticationFilter authenticationFilter
  ) {
    this.rateLimiterFilter = rateLimiterFilter;
    this.authenticationFilter = authenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(request -> {
          CorsConfiguration corsConfiguration = new CorsConfiguration();
          corsConfiguration.setAllowedOrigins(List.of("*"));
          corsConfiguration.setAllowedMethods(List.of("*"));
          corsConfiguration.setAllowedHeaders(List.of("*"));
          return corsConfiguration;
        }))
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(this.rateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}