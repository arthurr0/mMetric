package pl.minecodes.metric.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.minecodes.metric.backend.project.Project;
import pl.minecodes.metric.backend.project.ProjectService;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  private static final String X_API_KEY = "X-API-KEY";
  private static final String X_INTEGRATION_KEY = "X-INTEGRATION-KEY";

  @Value("${api.key}")
  private String apiKey;

  private final ProjectService projectService;

  public AuthenticationFilter(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String requestApiKey = request.getHeader(X_API_KEY);
    String requestIntegrationKey = request.getHeader(X_INTEGRATION_KEY);

    if (requestApiKey != null) {
      if (this.apiKey.equals(requestApiKey)) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            X_API_KEY,
            null,
            Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    if (requestIntegrationKey != null) {
      Optional<Project> projectOptional = this.projectService.findByIntegrationKey(requestIntegrationKey);
      if (projectOptional.isPresent()) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            X_INTEGRATION_KEY,
            null,
            Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
