package com.bd.porao.util;

import com.bd.porao.security.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils
{
    private SecurityUtils()
    {
        // Utility class
    }

    public static Long getCurrentUserId()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            return Long.valueOf(jwtAuth.getToken().getSubject());
        }
        return null;
    }

    public static Claims getCurrentJwt()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) auth).getToken();
        }
        return null;
    }

    public static String getCurrentUserEmail()
    {
        Claims jwt = getCurrentJwt();
        return jwt != null ? jwt.get("email", String.class) : null;
    }


    public static String getCurrentUserRole()
    {
        Claims jwt = getCurrentJwt();
        return jwt != null ? jwt.get("role", String.class) : null;
    }

    public static boolean hasRole(String role)
    {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(role);
    }

    public static boolean isAuthenticated()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);
    }

}
