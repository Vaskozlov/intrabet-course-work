package org.intrabet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.intrabet.service.auth.JwtTokenService;
import org.intrabet.service.auth.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public TokenAuthenticationFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader.startsWith(jwtTokenService.getTokenType())) {
            tryAuthorizeWith(jwtTokenService, authHeader, request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void tryAuthorizeWith(
            TokenService tokenService,
            String authHeader,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String token = authHeader
                .substring(tokenService.getTokenType().length() + 1);

        final String username = tokenService.getUsername(token);

        if (username == null) {
            return;
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext.getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (tokenService.isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            securityContext.setAuthentication(authToken);
        } else if (tokenService.isTokenExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\"}");
            response.getWriter().flush();
        }
    }
}
