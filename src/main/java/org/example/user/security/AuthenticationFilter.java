package org.example.user.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends GenericFilterBean {


    private final TokenService tokenService;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final Optional<String> authorization =
                Optional.ofNullable(((HttpServletRequest) request).getHeader(AUTHORIZATION));
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (authorization.isEmpty()) {
            log.debug("No bearer token found - return empty.");
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access");
            return;
        }
        try {
            String tokenValue = authorization.get();
            String token = tokenValue.substring("Bearer".length()).trim();
            log.debug("Bearer token is {}", tokenValue);
            SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthentication(token));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Portal Security Exception", e);
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access");
        }
    }
}
