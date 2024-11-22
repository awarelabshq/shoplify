package org.shoplify.product;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.opentelemetry.api.trace.Span;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.SneakyThrows;
import org.shoplify.frontendservice.*;
import org.shoplify.product.dtos.*;
import org.shoplify.product.model.CategoryEntity;
import org.shoplify.product.model.ProductEntity;
import org.shoplify.product.repos.CategoryRepository;
import org.shoplify.product.repos.ProductRepository;
import org.shoplify.product.util.ServiceUtil;
import org.shoplify.productservice.ListCategoriesRequest;
import org.shoplify.productservice.ListCategoriesResponse;
import org.shoplify.productservice.*;
import org.shoplify.storage.ProductMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping(value = "/health/check")
    @Operation(summary = "Health Check", description = "Checks the health status of the service.")
    public String healthCheck() throws Exception {
        return "healthy";
    }

    @PostMapping(value = "/product/list_categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Categories", description = "Lists all categories available.")
    @RequestBody(content = @Content(schema = @Schema(implementation = ListCategoriesRequestDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListCategoriesResponseDTO.class)))
    })
    public ResponseEntity createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        ListCategoriesRequest request = ServiceUtil.getRequestBody(httpServletRequest, ListCategoriesRequest.class);
        List<CategoryEntity> categories = categoryRepository.findAll();
        ListCategoriesResponse.Builder response = ListCategoriesResponse.newBuilder();
        if (!categories.isEmpty()) {
            response.setFeaturedCategory(getCategory(categories.get(0)));
        }
        for (int i = 1; i < categories.size(); i++) {
            response.addCategories(getCategory(categories.get(i)));
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(response));
    }

    @PostMapping(value = "/product/list_products", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Products", description = "Lists products based on the provided request.")
    @RequestBody(content = @Content(schema = @Schema(implementation = org.shoplify.product.dtos.ListProductsRequestDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = org.shoplify.product.dtos.ListProductsResponseDTO.class)))
    })
    public ResponseEntity listProducts(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        ListProductsRequest request = ServiceUtil.getRequestBody(httpServletRequest, ListProductsRequest.class);
        List<ProductEntity> products = productRepository.findAll();
        ListProductsResponse.Builder response = ListProductsResponse.newBuilder();
        for (ProductEntity entity : products) {
            ProductMetadata.Builder metadata = ProductMetadata.newBuilder();
            JsonFormat.parser().merge(entity.getMetadata(), metadata);

            if (metadata.getCategoriesList().contains(request.getCategory()) && metadata.getAvailableCountriesList()
                    .contains(request.getUserCountry())) {
                response.addProducts(getProductItem(entity));
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(response));
    }

    @PostMapping(value = "/product/list_cart", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Cart", description = "Lists items in the cart based on the provided request.")
    @RequestBody(content = @Content(schema = @Schema(implementation = ListCartRequestDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cart items",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListCartResponseDTO.class)))
    })
    public ResponseEntity listCart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        ListCartRequest request = ServiceUtil.getRequestBody(httpServletRequest, ListCartRequest.class);
        List<CartItem> items = request.getItemsList();
        Map<Long, ProductEntity> products = productRepository.findAllByIdIn(items.stream()
                        .map(item -> item.getProductId()).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, product -> product));
        if (products.keySet().isEmpty()) {
            throw new RuntimeException("Invalid cart");
        }
        Map<Long, Long> qtyMap = items.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity,
                        Long::sum // In case there are duplicate product IDs, sum the quantities
                ));
        ListCartResponse.Builder response = ListCartResponse.newBuilder();
        float sumCost = 0;
        for (ProductEntity entity : products.values()) {
            ProductItem productItem = getProductItem(entity);
            float totalCost = productItem.getPrice() * qtyMap.get(entity.getId());
            response.addCheckoutItems(DetailedCartCheckoutItem.newBuilder().setItem(productItem).setTotalCost(totalCost)
                    .build());
            sumCost += totalCost;
        }
        response.setSumCost(sumCost);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(response));
    }

    @PostMapping(value = "/product/get_shipping_cost", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Shipping Cost", description = "Calculates the shipping cost based on the provided request.")
    @RequestBody(content = @Content(schema = @Schema(implementation = GetShippingCostRequestDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping cost details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetShippingCostResponseDTO.class)))
    })
    public ResponseEntity getShippingCost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        GetShippingCostRequest request = ServiceUtil.getRequestBody(httpServletRequest, GetShippingCostRequest.class);
        GetShippingCostResponse.Builder response = GetShippingCostResponse.newBuilder();
        if (request.getZipCode().startsWith("00")) {
            response.setIsSupported(false);
            Span.current().setAttribute("shipping_supported_zip_code", false);
            throw new RuntimeException("Zip code not supported");
        } else {
            response.setIsSupported(true);
            Span.current().setAttribute("shipping_supported_zip_code", true);
        }
        response.setShippingCost((float) (0.1 * request.getTotalCost()));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(response));
    }


    @SneakyThrows
    private ProductItem getProductItem(ProductEntity entity) {
        ProductMetadata.Builder metadata = ProductMetadata.newBuilder();
        JsonFormat.parser().merge(entity.getMetadata(), metadata);
        return ProductItem.newBuilder().setName(entity.getTitle()).setImageUrl(entity.getImageUrl())
                .setId(entity.getId())
                .setDescription(entity.getDescription())
                .setPrice(metadata.getUnitPrice()).build();
    }

    private Category getCategory(CategoryEntity category) {
        return Category.newBuilder().setDescription(category.getDescription()).setName(category.getTitle())
                .setImageUrl(category.getImageUrl()).build();
    }

}