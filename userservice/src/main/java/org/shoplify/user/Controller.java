package org.shoplify.user;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.riskservice.GetRiskStatusRequest;
import org.shoplify.riskservice.GetRiskStatusResponse;
import org.shoplify.user.model.UserEntity;
import org.shoplify.user.repos.UserRepository;
import org.shoplify.user.util.ServiceUtil;
import org.shoplify.userservice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/user/create_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        CreateUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, CreateUserRequest.class);
        UserEntity entity = new UserEntity();
        Optional<UserEntity> existingEntity = userRepository.findByEmail(request.getEmail());
        if (existingEntity.isPresent()) {
            return ResponseEntity.status(HTTP_BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
        entity.setEmail(request.getEmail());
        entity.setPassword(request.getPassword());
        entity.setType(request.getType().toString());
        entity = userRepository.save(entity);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JsonFormat.printer().print(CreateUserResponse.newBuilder().setUserId(entity.getId() + "")));
    }

    @PostMapping(value = "/user/get_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        GetUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, GetUserRequest.class);

        Optional<UserEntity> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(GetUserResponse.newBuilder().setUserCountry(userEntity.getCountry())
                    .setToken(userEntity.getToken()).setType(userEntity.getType())));
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(GetUserResponse.getDefaultInstance()));
    }

    @PostMapping(value = "/user/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        LoginUserRequest request = ServiceUtil.getRequestBody(httpServletRequest, LoginUserRequest.class);
        Optional<UserEntity> existingEntity = userRepository.findByEmail(request.getEmail());
        if (!existingEntity.isPresent()) {
            logger.info("No user found for " + request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
        UserEntity entity = existingEntity.get();
        if (!request.getPassword().equals(entity.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).build();
        }
        GetRiskStatusResponse riskResponse = ServiceClient.callService(RISKSERVICE_URL + "risk/get_risk_status", JsonFormat.printer()
                .print(GetRiskStatusRequest.newBuilder().setUserId(request.getEmail())), GetRiskStatusResponse.class);
        String token = UUID.randomUUID().toString();
        entity.setToken(token);
        userRepository.save(entity);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer()
                .print(LoginUserResponse.newBuilder().setUserId(entity.getId()).setToken(token)
                        .setStatus(riskResponse.getRiskStatus()
                                .equals("low") ? LoginUserResponse.LoginStatus.SUCCESS : LoginUserResponse.LoginStatus.DENIED_RISK_SUSPENDED)));
    }
}