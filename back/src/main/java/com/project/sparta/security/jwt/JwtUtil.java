package com.project.sparta.security.jwt;


import static com.project.sparta.exception.api.Status.INVALID_TOKEN;

import com.project.sparta.exception.CustomException;
import com.project.sparta.security.UserDetailServiceImpl;
import com.project.sparta.user.entity.UserGradeEnum;
import com.project.sparta.user.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailServiceImpl userDetailsService;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L;     //Access 토큰 유효(1시간)
    //private static final long ACCESS_TOKEN_TIME = 10 * 1000L;     //Access 토큰 유효(10초)
    public static final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L;     //Refresh 토큰(1일)

    @Value("${jwt.token.access-token-secret}")
    private String access_token_secretKey;

    @Value(("${jwt.token.refresh-token-secret}"))
    private String refresh_token_secret_key;

    public Key tokenDecode(String token) {
        byte[] bytes = Base64.getDecoder().decode(token);
        return Keys.hmacShaKeyFor(bytes);
    }

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    public String generateAccessToken(String email, UserRoleEnum role, UserGradeEnum grade, String nickname, String imgUrl) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("grade", grade);
        claims.put("nickname", nickname);
        claims.put("profileImage", imgUrl);
        Date date = new Date();

        Key key = tokenDecode(access_token_secretKey);

        String accessToken = BEARER_PREFIX + Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
        return accessToken;
    }


    public String generateRefreshToken(String email, UserRoleEnum role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date date = new Date();

        Key key = tokenDecode(refresh_token_secret_key);

        String refreshToken = BEARER_PREFIX + Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();

        return refreshToken;
    }

    public boolean validateToken(String token) {
        try {
            String changeToken = token.substring(7);
            Jwts.parserBuilder().setSigningKey(access_token_secretKey).build().parseClaimsJws(changeToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public boolean validateRefreshToken(String token) throws CustomException {
        try {
            String changeToken = token.substring(7);
            Jwts.parserBuilder().setSigningKey(refresh_token_secret_key).build().parseClaimsJws(changeToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            //throw new CustomException(INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            //throw new CustomException(INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            //throw new CustomException(INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            //throw new CustomException(INVALID_TOKEN);
        }
        return false;
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken;
        }
        return null;
    }

    public Authentication getAuthenticationByAccessToken(String access_token) {
        String changeToken = access_token.substring(7);
        String userPrincipal = Jwts.parserBuilder().setSigningKey(access_token_secretKey).build().parseClaimsJws(changeToken).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationByRefreshToken(String access_token) {
        String changeToken = access_token.substring(7);
        String userPrincipal = Jwts.parserBuilder().setSigningKey(refresh_token_secret_key).build().parseClaimsJws(changeToken).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getExpiration(String accessToken) {
        String changeToken = accessToken.substring(7);
        Date expiration = Jwts.parserBuilder().setSigningKey(access_token_secretKey).build().parseClaimsJws(changeToken).getBody().getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
