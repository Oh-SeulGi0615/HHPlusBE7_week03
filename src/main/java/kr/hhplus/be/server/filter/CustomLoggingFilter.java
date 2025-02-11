package kr.hhplus.be.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.info("Request received: method={}, URI={}, query={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getQueryString());

        chain.doFilter(request, response);

        if (httpResponse.getStatus() >= 400 && httpResponse.getStatus() < 500) {
            logger.warn("Client error: status={}, URI={}", httpResponse.getStatus(), httpRequest.getRequestURI());
        } else if (httpResponse.getStatus() >= 500) {
            logger.error("Server error: status={}, URI={}", httpResponse.getStatus(), httpRequest.getRequestURI());
        } else {
            logger.info("Response sent: status={}, URI={}", httpResponse.getStatus(), httpRequest.getRequestURI());
        }
    }
}
