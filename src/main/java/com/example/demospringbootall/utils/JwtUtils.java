package com.example.demospringbootall.utils;

import com.example.demospringbootall.entities.Role;
import com.example.demospringbootall.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


    private static final String jwtSecret="dafbDhfhCSJhhfh99wj3jhjha";
    private static final int jwtExpirationMs = 120;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> claim = new HashMap<>();
        claim.put("role",userPrincipal.getAuthorities());
        return Jwts.builder()
                .setClaims(claim)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+ jwtExpirationMs*60*1000))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public List<Role> getRole(String token){
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        List<Role> list = (List<Role>) claims.get("role");
        return list;
    }
    public boolean validateJwtToken(String authToken){
        try{
            String username = getUserNameFromJwtToken(authToken);
            if(username!=null){
                return true;
            }
        }catch (Exception e){
            logger.error("Exception: ", e.getMessage());
        }
        return false;
    }
}
