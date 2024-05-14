package org.shoplify.user;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.shoplify.user.util.ServiceUtil.PAYLOAD_HTTP_ATTRIBUTE;


@Component
public class LogInterceptor implements HandlerInterceptor, Ordered {
    private static final Logger logger = Logger.getLogger(LogInterceptor.class.getName());

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().equals("/health/check")) {
            logger.info("Prehandle " + request.getRequestURI());
        }
        if (!request.getRequestURI().equals("/error")) {
            String requestBody = getRequestBody(request);
            request.setAttribute(PAYLOAD_HTTP_ATTRIBUTE, requestBody);
        }
        return true;
    }


    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }
}