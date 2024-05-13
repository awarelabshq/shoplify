package org.shoplify.user;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.userservice.CreateUserRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    @PostMapping(value = "/user/create_user")
    public String createUser(HttpServletRequest httpServletRequest) throws InvalidProtocolBufferException {
        return JsonFormat.printer().print(CreateUserRequest.newBuilder().setEmail("nuwansam@gmail.com").build());
    }
}