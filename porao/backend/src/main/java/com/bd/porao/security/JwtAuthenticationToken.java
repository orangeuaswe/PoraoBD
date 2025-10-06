package com.bd.porao.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken
{
    private final Claims claims;

    public JwtAuthenticationToken(Claims claims)
    {
        super(getAuthorities(claims));
        this.claims = claims;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Claims claims)
    {
        String role = claims.get("role", String.class);
        return role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role)) : List.of();
    }

    @Override
    public Object getCredentials()
    {
        return null;
    }

    @Override
    public Object getPrincipal()
    {
        return claims.getSubject();
    }

    public Claims getToken()
    {
        return claims;
    }

}
