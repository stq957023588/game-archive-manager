package com.fool.gamearchivemanager.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JWTUtils {


    public static Map<String, Claim> parsingToken(String secret, String issuer, String token) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        DecodedJWT verify = verifier.verify(token);

        return verify.getClaims();
    }
}
