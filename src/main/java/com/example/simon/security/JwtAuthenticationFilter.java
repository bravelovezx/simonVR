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
import org.springframework.security.core.Authentication;

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
                    logger.info("=== JWT认证详细信息 ===");
                    logger.info("从Token中解析的用户ID: {}", userId);
                    logger.info("用户名: {}", username);

                    Map<String, Object> details = new HashMap<>();
                    details.put("userId", userId);
                    logger.info("设置到Authentication details中的userId: {}", userId);
                    logger.info("完整的details对象: {}", details);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(details);
                    logger.info("Authentication对象创建完成，details: {}", authentication.getDetails());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Successfully authenticated user: {} with ID: {}", username, userId);
                    
                    // 验证设置是否成功
                    Authentication storedAuth = SecurityContextHolder.getContext().getAuthentication();
                    Map<String, Object> storedDetails = (Map<String, Object>) storedAuth.getDetails();
                    Integer storedUserId = (Integer) storedDetails.get("userId");
                    logger.info("验证SecurityContext中存储的用户ID: {}", storedUserId);
                    
                    if (!userId.equals(storedUserId)) {
                        logger.error("❌ 用户ID存储验证失败！期望: {}, 实际: {}", userId, storedUserId);
                    } else {
                        logger.info("✅ 用户ID存储验证成功: {}", storedUserId);
                    }
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