package org.shoplify.frontend;

import com.google.protobuf.util.JsonFormat;
import org.shoplify.common.util.ServiceClient;
import org.shoplify.frontend.dtos.*;
import org.shoplify.frontend.util.ServiceUtil;
import org.shoplify.frontendservice.*;
import org.shoplify.productservice.*;
import org.shoplify.productservice.ListCategoriesRequest;
import org.shoplify.productservice.ListCategoriesResponse;
import org.shoplify.userservice.CreateUserResponse;
import org.shoplify.userservice.GetUserRequest;
import org.shoplify.userservice.GetUserResponse;
import org.shoplify.userservice.LoginUserResponse;
import org.shoplify.frontend.dtos.ListProductsResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    @Autowired
    OpenAiKotlinFacade openAiKotlinFacade;

    @GetMapping(value = "/health/check")
    @Operation(summary = "Health Check", description = "Checks the health of the service.")
    public String healthCheck() throws Exception {
        return "healthy";
    }

    @PostMapping(value = "/frontend/create_user", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create User", description = "Creates a new user.")
    @RequestBody(description = "Create user request", required = true, content = @Content(schema = @Schema(implementation = CreateUserRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(schema = @Schema(implementation = CreateUserResponseDTO.class)))
    public String createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(USERSERVICE_URL + "user/create_user", ServiceUtil.getRequestBody(httpServletRequest), CreateUserResponse.class));
    }

    @PostMapping(value = "/frontend/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Chat", description = "Provide chat support")
    @RequestBody(description = "Chat", required = true, content = @Content(schema = @Schema(implementation = ChatRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "Chat successfully processed", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    public String chat(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        ChatRequest.Builder builder = ChatRequest.newBuilder();
        JsonFormat.parser().merge(ServiceUtil.getRequestBody(httpServletRequest), builder);
        String response = openAiKotlinFacade.getResponse(builder.getMessage(), builder.getPreviousMessagesList());
        return JsonFormat.printer()
                .print(ChatResponse.newBuilder().setMessage(response));
    }

    @PostMapping(value = "/frontend/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login", description = "Logs in a user.")
    @RequestBody(description = "Login request", required = true, content = @Content(schema = @Schema(implementation = LoginUserRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginUserResponseDTO.class)))
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return JsonFormat.printer()
                .print(ServiceClient.callService(USERSERVICE_URL + "user/login", ServiceUtil.getRequestBody(httpServletRequest), LoginUserResponse.class));
    }

    @PostMapping(value = "/frontend/list_categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Categories", description = "Lists categories based on user information.")
    @RequestBody(description = "List categories request", required = true, content = @Content(schema = @Schema(implementation = ListCategoriesRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "List of categories", content = @Content(schema = @Schema(implementation = ListCategoriesResponseDTO.class)))
    public String listCategories(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        org.shoplify.frontendservice.ListCategoriesRequest requestBody = ServiceUtil.getRequestBody(httpServletRequest, org.shoplify.frontendservice.ListCategoriesRequest.class);
        GetUserResponse userResponse = ServiceClient.callService(USERSERVICE_URL + "user/get_user", JsonFormat.printer()
                .print(GetUserRequest.newBuilder().setUserId(requestBody.getUserId())), GetUserResponse.class);
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_categories", JsonFormat.printer()
                        .print(ListCategoriesRequest.newBuilder()
                                .setUserCountry(userResponse.getUserCountry())), ListCategoriesResponse.class));
    }

    @GetMapping(value = "/frontend/list_products", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Products", description = "Lists products based on category and country.")
    @Parameter(name = "category", description = "Product category", required = true)
    @Parameter(name = "country", description = "User country", required = true)
    @ApiResponse(responseCode = "200", description = "List of products", content = @Content(schema = @Schema(implementation = ListProductsResponseDTO.class)))
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

    @PostMapping(value = "/frontend/list_cart", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Cart", description = "Lists items in the cart.")
    @RequestBody(description = "List cart request", required = true, content = @Content(schema = @Schema(implementation = ListCartRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "List of cart items", content = @Content(schema = @Schema(implementation = ListCartResponseDTO.class)))
    public String listCart(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        ListCartRequest requestBody = ServiceUtil.getRequestBody(httpServletRequest, ListCartRequest.class);
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/list_cart", JsonFormat.printer()
                        .print(requestBody), ListCartResponse.class));
    }

    @PostMapping(value = "/frontend/get_shipping_cost", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Shipping Cost", description = "Gets the shipping cost for a given request.")
    @RequestBody(description = "Get shipping cost request", required = true, content = @Content(schema = @Schema(implementation = GetShippingCostRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "Shipping cost details", content = @Content(schema = @Schema(implementation = GetShippingCostResponseDTO.class)))
    public String getShippingCost(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        GetShippingCostRequest requestBody = ServiceUtil.getRequestBody(httpServletRequest, GetShippingCostRequest.class);
        return JsonFormat.printer()
                .print(ServiceClient.callService(PRODUCTSERVICE_URL + "product/get_shipping_cost", JsonFormat.printer()
                        .print(requestBody), GetShippingCostResponse.class));
    }

    @PostMapping(value = "/frontend/submit_search", consumes = "application/x-www-form-urlencoded", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Submit Search", description = "Submits a search query and returns the results.")
    @Parameter(name = "search_query", description = "Search query", required = true)
    @ApiResponse(responseCode = "200", description = "Search results", content = @Content(schema = @Schema(implementation = ListProductsResponseDTO.class)))
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