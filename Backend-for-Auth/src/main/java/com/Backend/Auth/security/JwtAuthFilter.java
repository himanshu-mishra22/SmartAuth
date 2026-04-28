package com.Backend.Auth.security;

import com.Backend.Auth.helpers.UserHelper;
import com.Backend.Auth.repository.UserRepo;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo  userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {

            //extract token and validate, create authentication and set in security context
            String token = header.substring(7);
            if(!jwtService.isAccessToken(token)){
                filterChain.doFilter(request, response);
                return;
            }
            try{
                Jws<Claims> jwtClaims = jwtService.parse(token);
                Claims payload = jwtClaims.getPayload();


                String userId = payload.getSubject();
                UUID userUUID = UserHelper.parseUUId(userId);

                userRepo.findById(userUUID).ifPresent(user -> {

                    if(!user.isEnable()){
                        try {
                            filterChain.doFilter(request,response);
                        } catch (IOException | ServletException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }

                    List<GrantedAuthority> authorities = user.getRoles() == null ? List.of() : user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    if(SecurityContextHolder.getContext().getAuthentication() == null){

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                });
            }catch (ExpiredJwtException e){
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                e.printStackTrace();
            } catch (JwtException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }
}
