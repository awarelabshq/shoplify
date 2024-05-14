package org.shoplify.user;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.model.UserEntity;
import org.shoplify.common.repos.UserRepository;
import org.shoplify.user.util.ServiceUtil;
import org.shoplify.userservice.CreateUserRequest;
import org.shoplify.userservice.CreateUserResponse;
import org.shoplify.userservice.LoginUserRequest;
import org.shoplify.userservice.LoginUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.shoplify.common.util.Constants.HTTP_BAD_REQUEST;
import static org.shoplify.common.util.Constants.HTTP_UNAUTHORIZED;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/user/create_user")
    public String createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        CreateUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, CreateUserRequest.class);
        UserEntity entity = new UserEntity();
        Optional<UserEntity> existingEntity = userRepository.findByEmail(request.getEmail());
        if (existingEntity.isPresent()) {
            return ServiceUtil.updateReturnResponse(httpServletResponse, HTTP_BAD_REQUEST);
        }
        entity.setEmail(request.getEmail());
        entity.setPassword(request.getPassword());
        entity.setType(request.getType().toString());
        entity = userRepository.save(entity);
        return JsonFormat.printer().print(CreateUserResponse.newBuilder().setUserId(entity.getId() + ""));
    }

    @PostMapping(value = "/user/get_users")
    public String getUsers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        CreateUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, CreateUserRequest.class);
        List<UserEntity> entities = userRepository.findAll();
        if (!entities.isEmpty()) {
            return JsonFormat.printer().print(LoginUserResponse.newBuilder().setUserId(entities.get(0).getId() + "")
                    .setToken(entities.get(0).getToken()));
        }
        return JsonFormat.printer().print(LoginUserResponse.getDefaultInstance());
    }

    @PostMapping(value = "/user/login")
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        LoginUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, LoginUserRequest.class);
        Optional<UserEntity> existingEntity = userRepository.findByEmail(request.getEmail());
        if (!existingEntity.isPresent()) {
            logger.info("No user found for " + request.getEmail());
            return ServiceUtil.updateReturnResponse(httpServletResponse, HTTP_BAD_REQUEST);
        }
        UserEntity entity = existingEntity.get();
        if (!request.getPassword().equals(entity.getPassword())) {
            return ServiceUtil.updateReturnResponse(httpServletResponse, HTTP_UNAUTHORIZED);
        }
        String token = UUID.randomUUID().toString();
        entity.setToken(token);
        userRepository.save(entity);
        return JsonFormat.printer()
                .print(LoginUserResponse.newBuilder().setUserId(entity.getId() + "").setToken(token));
    }
}