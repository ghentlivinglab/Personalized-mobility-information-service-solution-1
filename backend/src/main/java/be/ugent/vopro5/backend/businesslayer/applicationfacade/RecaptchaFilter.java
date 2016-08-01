package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.businessworkflow.CaptchaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by thibault on 4/5/16.
 */
public class RecaptchaFilter implements Filter {

    private static final String CAPTCHA_HEADER = "X-Captcha-Response";

    @Autowired
    private CaptchaHelper captchaHelper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (HttpMethod.POST.matches(((HttpServletRequest) request).getMethod())) {
            String authorizationCode = ((HttpServletRequest) request).getHeader(CAPTCHA_HEADER);
            if (!captchaHelper.verifyCaptcha(authorizationCode)) {
                HttpServletResponse res = (HttpServletResponse) response;
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reCaptcha code");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
