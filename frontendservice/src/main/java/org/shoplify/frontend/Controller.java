package org.shoplify.frontend;


import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.userservice.CreateUserResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static org.shoplify.common.util.ServiceClient.PRODUCTSERVICE_URL;
import static org.shoplify.common.util.ServiceClient.USERSERVICE_URL;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());


    @GetMapping(value = "/health/check")
    public String healthCheck() throws Exception {
        return "healthy";
    }

    @PostMapping(value = "/frontend/create_user")
    public String createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(USERSERVICE_URL + "user/create_user", httpServletRequest, CreateUserResponse.class));
    }


    @PostMapping(value = "/frontend/login")
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(USERSERVICE_URL + "user/login", httpServletRequest, CreateUserResponse.class));
    }


    @PostMapping(value = "/frontend/list_categories")
    public String listCategories(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_categories", httpServletRequest, CreateUserResponse.class));
    }
}