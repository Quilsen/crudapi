package org.example.crudapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.crudapi.dto.ApiResponse;
import org.example.crudapi.service.JwtUtil;
import org.example.crudapi.service.MyUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil;
    private MyUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                prepareErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired: " + ex.getMessage());
                return;
            } catch (io.jsonwebtoken.MalformedJwtException ex) {
                prepareErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid token: " + ex.getMessage());
                return;
            } catch (Exception ex) {
                prepareErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException ex) {
                prepareErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found: " + ex.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void prepareErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ApiResponse<String> apiResponse = new ApiResponse<>(status, message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
