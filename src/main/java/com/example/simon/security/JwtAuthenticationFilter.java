package com.example.simon.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authorizationHeader = request.getHeader("Authorization");

        logger.debug("Processing request for URI: {}", requestURI);
        logger.debug("Authorization header: {}", authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwt);
                logger.debug("Successfully extracted username from token: {}", username);
            } catch (Exception e) {
                logger.error("Failed to parse JWT token", e);
            }
        } else {
            logger.debug("No JWT token found in request headers");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Starting authentication for user: {}", username);

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.debug("Loaded user details: {}", userDetails);

                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    Integer userId = jwtTokenUtil.getUserIdFromToken(jwt);

                    Map<String, Object> details = new HashMap<>();
                    details.put("userId", userId);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(details);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Successfully authenticated user: {} with ID: {}", username, userId);
                } else {
                    logger.warn("Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                logger.error("Authentication failed for user: {}", username, e);
            }
        }

        chain.doFilter(request, response);
    }
}