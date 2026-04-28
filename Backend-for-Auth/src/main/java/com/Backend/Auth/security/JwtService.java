package com.Backend.Auth.security;

import com.Backend.Auth.entities.Role;
import com.Backend.Auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class JwtService {
    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
                      @Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
                      @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
                      @Value("${security.jwt.issuer}")  String issuer) {

        if(secret==null || secret.length()<64){
            throw new IllegalArgumentException("Secret length must be greater than 64 characters");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    //generate access token:
    public String generateAccessToken(User user){
        Instant now = Instant.now();
        List<String> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream().map(Role::getName).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(
                        key, SignatureAlgorithm.HS512
                ).compact();
    }

    //generate refresh token:
    public String generateRefreshToken(User user, String jti){
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //parse token
    public Jws<Claims> parse(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    //to check access token
    public boolean isAccessToken(String token){
        Claims claims = parse(token).getPayload();
        return "access".equals(claims.get("typ"));
    }

    //to check refresh token
    public boolean isRefreshToken(String token){
        Claims claims = parse(token).getPayload();
        return "refresh".equals(claims.get("typ"));
    }

    //to get UUID from access token
    public UUID getUserId(String token){
        Claims claims = parse(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    //to get refreshtokenJTI
    public String getJti(String token){
        return parse(token).getPayload().getId();
    }

    // to get roles
    public List<String> getRoles(String token){
        Claims claims = parse(token).getPayload();
        return (List<String>) claims.get("roles");
    }

    //to get email
    public String getEmail(String token){
        Claims claims = parse(token).getPayload();
        return (String) claims.get("email");
    }




}
