package com.daitan.messenger.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private Logger LOGGER = LoggerFactory.getLogger(AuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        super.commence(request, response, authException);
        LOGGER.debug("HTTP status 401 - {}", authException.getMessage());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Messenger-App");
        super.afterPropertiesSet();
    }
}
