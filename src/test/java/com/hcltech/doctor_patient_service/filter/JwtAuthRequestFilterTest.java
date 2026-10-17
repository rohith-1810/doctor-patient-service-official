package com.hcltech.doctor_patient_service.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.hcltech.doctor_patient_service.service.JpaUserDetailsService;
import com.hcltech.doctor_patient_service.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class JwtAuthRequestFilterTest {

    @InjectMocks
    private JwtAuthRequestFilter jwtAuthRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Test
    void testDoFilterInternal_validToken_authenticatesUser() throws Exception {
        String jwt = "valid.jwt.token";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        when(jwtUtil.extractUsernameFromToken(jwt)).thenReturn(username);
        when(jpaUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(jwt, userDetails)).thenReturn(true);

        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtAuthRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
