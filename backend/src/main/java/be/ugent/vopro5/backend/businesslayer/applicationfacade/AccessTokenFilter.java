package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by thibault on 4/2/16.
 */
public class AccessTokenFilter extends OncePerRequestFilter {
    private final AccessTokenService accessTokenService;

    /**
     * Return the AccessTokenService
     * @param accessTokenService
     */
    public AccessTokenFilter(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if (accessTokenService.requestHasAuthentication(req)) {
            // Apply the authentication to Spring Security's Context
            SecurityContextHolder.getContext().setAuthentication(accessTokenService.getAuthentication(req));
        }
        chain.doFilter(req, res);
    }
}