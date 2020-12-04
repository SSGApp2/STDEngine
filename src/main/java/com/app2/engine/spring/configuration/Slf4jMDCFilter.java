package com.app2.engine.spring.configuration;

import com.app2.engine.util.AuthorizeUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
//Suphachai Dettasorn
@Component
public class Slf4jMDCFilter extends OncePerRequestFilter {

    @Autowired
    AuthorizeUtil authorizeUtil;


    private final String responseHeader = "Response_Token";
    private final String mdcTokenKey = "req_id";
    private final String requestHeader = "req_id";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws java.io.IOException, ServletException {
        try {
            final String token;
            if (!StringUtils.isEmpty(requestHeader) && !StringUtils.isEmpty(request.getHeader(requestHeader))) {
                token = request.getHeader(requestHeader);
            } else {
                token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            }
            authorizeUtil.setMapUserDetails(request);
            String username = authorizeUtil.getUsername();
            String roleStr = authorizeUtil.getRole();
            MDC.put(mdcTokenKey, token);
            MDC.put("Username", username);
            MDC.put("Role", roleStr);

            if (!StringUtils.isEmpty(responseHeader)) {
                response.addHeader(responseHeader, token);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(mdcTokenKey);
        }
    }
}
