package org.shoplify.frontend;


import com.google.api.client.json.Json;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.frontend.util.ServiceUtil;
import org.shoplify.productservice.*;
import org.shoplify.userservice.CreateUserResponse;
import org.shoplify.userservice.GetUserRequest;
import org.shoplify.userservice.GetUserResponse;
import org.shoplify.userservice.LoginUserResponse;
import org.springframework.web.bind.annotation.*;

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
                .print(ServiceClient.callService(USERSERVICE_URL + "user/create_user", ServiceUtil.getRequestBody(httpServletRequest), CreateUserResponse.class));
    }


    @PostMapping(value = "/frontend/login")
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(USERSERVICE_URL + "user/login", ServiceUtil.getRequestBody(httpServletRequest), LoginUserResponse.class));
    }


    @PostMapping(value = "/frontend/list_categories")
    public String listCategories(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        org.shoplify.frontendservice.ListCategoriesRequest requestBody = ServiceUtil.getRequestBody(httpServletRequest, org.shoplify.frontendservice.ListCategoriesRequest.class);
        GetUserResponse userResponse = ServiceClient.callService(USERSERVICE_URL + "user/get_user", JsonFormat.printer()
                .print(GetUserRequest.newBuilder().setUserId(requestBody.getUserId())), GetUserResponse.class);
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_categories", JsonFormat.printer()
                        .print(ListCategoriesRequest.newBuilder()
                                .setUserCountry(userResponse.getUserCountry())), ListCategoriesResponse.class));
    }

    @GetMapping(value = "/frontend/list_products")
    public String listProducts(
            @RequestParam("category") String category,
            @RequestParam("country") String country,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        String request = JsonFormat.printer()
                .print(ListProductsRequest.newBuilder().setCategory(category).setUserCountry(country));
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_products", request, ListProductsResponse.class));
    }

    @PostMapping(value = "/frontend/submit_search", consumes = "application/x-www-form-urlencoded")
    public String submitSearch(
            @RequestParam("search_query") String searchQuery,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        String request = JsonFormat.printer()
                .print(SearchProductsRequest.newBuilder().setQuery(searchQuery));
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_products", request, ListProductsResponse.class));
    }
}