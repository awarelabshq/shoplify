package org.shoplify.frontend;

import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.userservice.GetUserRequest;
import org.shoplify.userservice.GetUserResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.shoplify.common.util.ServiceClient.USERSERVICE_URL;

@Component
public class LogInterceptor implements HandlerInterceptor, Ordered {

    private static final Logger logger = Logger.getLogger(LogInterceptor.class.getName());
    private static final String PAYLOAD_HTTP_ATTRIBUTE = "payload";

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

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isEmpty()) {
            // Extract the token from the Authorization header (format: "Bearer <token>")
            String token = authHeader.replace("Bearer ", "").trim();

            String userId = request.getHeader("user-id");
            if (userId != null && !userId.isEmpty()) {
                // Call the user service to validate the token
                GetUserResponse getUserResponse = ServiceClient.callService(
                        USERSERVICE_URL + "user/get_user",
                        JsonFormat.printer().print(
                                GetUserRequest.newBuilder().setUserId(Long.parseLong(userId))
                        ),
                        GetUserResponse.class
                );

                String validToken = getUserResponse.getToken();

                // Check if the provided token matches the valid token
                if (!token.equals(validToken)) {
                    logger.warning("Invalid token for user ID: " + userId);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.getWriter().write("Unauthorized: Invalid token");
                    return false; // Stop further processing
                }
            } else {
                logger.warning("Missing user-id header");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.getWriter().write("Bad Request: Missing user-id header");
                return false; // Stop further processing
            }
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