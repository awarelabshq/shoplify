package org.shoplify.user;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.riskservice.GetRiskStatusRequest;
import org.shoplify.riskservice.GetRiskStatusResponse;
import org.shoplify.user.model.UserEntity;
import org.shoplify.user.repos.UserRepository;
import org.shoplify.user.util.ServiceUtil;
import org.shoplify.userservice.CreateUserRequest;
import org.shoplify.userservice.CreateUserResponse;
import org.shoplify.userservice.LoginUserRequest;
import org.shoplify.userservice.LoginUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.shoplify.common.util.Constants.HTTP_BAD_REQUEST;
import static org.shoplify.common.util.Constants.HTTP_UNAUTHORIZED;
import static org.shoplify.common.util.ServiceClient.RISKSERVICE_URL;
import static org.shoplify.common.util.ServiceClient.USERSERVICE_URL;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/health/check")
    public String healthCheck() throws Exception {
        return "healthy";
    }

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
        List<UserEntity> entities = userRepository.findAll();
        if (!entities.isEmpty()) {
            return JsonFormat.printer().print(LoginUserResponse.newBuilder().setUserId(entities.get(0).getId() + "")
                    .setToken(entities.get(0).getToken()));
        }
        return JsonFormat.printer().print(LoginUserResponse.getDefaultInstance());
    }

    @PostMapping(value = "/user/login")
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
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
        GetRiskStatusResponse riskResponse = ServiceClient.callService(RISKSERVICE_URL + "risk/get_risk_status", JsonFormat.printer()
                .print(GetRiskStatusRequest.newBuilder().setUserId(request.getEmail())), GetRiskStatusResponse.class);
        String token = UUID.randomUUID().toString();
        entity.setToken(token);
        userRepository.save(entity);
        return JsonFormat.printer()
                .print(LoginUserResponse.newBuilder().setUserId(entity.getId() + "").setToken(token)
                        .setStatus(riskResponse.getRiskStatus()
                                .equals("low") ? LoginUserResponse.LoginStatus.SUCCESS : LoginUserResponse.LoginStatus.DENIED_RISK_SUSPENDED));
    }
}