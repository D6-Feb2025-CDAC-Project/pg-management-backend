package com.easypg.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//	@Autowired
//	private JwtUtil jwtUtil;
//	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		// PRE-PROCESSING
//		// get jwt token from request header
//		String authHeader = request.getHeader("Authorization");
//		System.out.println("Authorization Header: " + authHeader);
//		boolean validHeader = authHeader != null && authHeader.startsWith("Bearer");
//		Authentication auth = null;
//		if (validHeader) {
//			String token = authHeader.replace("Bearer", "").trim();
//			System.out.println("JWT Token: " + token);
//			// validate that jwt token and create Authentication object
//			auth = jwtUtil.validateToken(token);
//		}
//		
//		//  attach auth/principal to current security context
//		if (auth != null && SecurityContextHolder.getContext().getAuthentication() == null)
//			SecurityContextHolder.getContext().setAuthentication(auth);
//		
//		// invoke next filter in the chain
//		filterChain.doFilter(request, response);
//		
//		// POST-PROCESSING (nothing to do here)
//	}
//
//}

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Authentication auth = jwtUtil.validateToken(token);

            if (auth != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
