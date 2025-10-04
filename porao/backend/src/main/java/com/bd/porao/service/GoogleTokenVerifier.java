package com.bd.porao.service;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleTokenVerifier
{

    @Value("S{oauth.google.clientId}")
    private String clientId;

    public GoogleProfile verify(String idToken) throws Exception
    {
        var jwt = SignedJWT.parse(idToken);
        var claims = jwt.getJWTClaimsSet();
        if(!claims.getAudience().contains(clientId))
        {
            throw new RuntimeException("Invalid");
        }
        String issuer = claims.getIssuer();
        if(!"https://accounts.google.com".equals(issuer)&&!"accounts.google.com".equals(issuer))
        {
            throw new RuntimeException("Invalid");
        }
        String email = claims.getStringClaim("email");
        String name = claims.getStringClaim("name");
        return new GoogleProfile(email,name);
    }
    public record GoogleProfile(String email, String name)
    {

    }
}
