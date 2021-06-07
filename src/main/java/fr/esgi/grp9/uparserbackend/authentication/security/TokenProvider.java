package fr.esgi.grp9.uparserbackend.authentication.security;

import fr.esgi.grp9.uparserbackend.authentication.login.Role;
import fr.esgi.grp9.uparserbackend.user.domain.UserServiceImpl;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

@Component
public class TokenProvider {

    @Value("${token.secret.key}")
    private String secretKey;
    private final long tokenValidityInMilliseconds = Duration.ofMinutes(60).getSeconds() * 1000;

    @Autowired
    private UserServiceImpl userService;

    public String createToken(String email, Set<Role> set) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", set);

        Date validity = new Date((new Date()).getTime() + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token).getBody();
        UserDetails userDetails = this.userService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    private Jws<Claims> parseToken(String authToken) {
        return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(authToken);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }
}
