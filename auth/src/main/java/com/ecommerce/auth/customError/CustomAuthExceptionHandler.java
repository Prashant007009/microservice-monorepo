package com.ecommerce.auth.customError;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    // Handles 401 Unauthorized (invalid/missing token)
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Unauthorized");
        body.put("message", "You must provide a valid JWT token");
        body.put("path", request.getRequestURI());
        mapper.writeValue(response.getOutputStream(), body);
    }

    // Handles 403 Forbidden (valid token but insufficient access)
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Forbidden");
        body.put("message", "You don’t have permission to access this resource");
        body.put("path", request.getRequestURI());
        mapper.writeValue(response.getOutputStream(), body);
    }
}

