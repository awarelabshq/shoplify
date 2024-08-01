package org.shoplify.product;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.product.model.CategoryEntity;
import org.shoplify.product.model.ProductEntity;
import org.shoplify.product.repos.CategoryRepository;
import org.shoplify.product.repos.ProductRepository;
import org.shoplify.product.util.ServiceUtil;
import org.shoplify.productservice.*;
import org.shoplify.storage.ProductMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping(value = "/health/check")
    public String healthCheck() throws Exception {
        return "healthy";
    }

    @PostMapping(value = "/product/list_categories", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public ResponseEntity listProducts(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        ListProductsRequest request = ServiceUtil.getRequestBody(httpServletRequest, ListProductsRequest.class);
        List<ProductEntity> products = productRepository.findAll();
        ListProductsResponse.Builder response = ListProductsResponse.newBuilder();
        for (ProductEntity entity : products) {
            ProductMetadata.Builder metadata = ProductMetadata.newBuilder();
            JsonFormat.parser().merge(entity.getMetadata(), metadata);

            if (metadata.getCategoriesList().contains(request.getCategory()) && metadata.getAvailableCountriesList()
                    .contains(request.getUserCountry())) {
                response.addProducts(getProductItem(entity, metadata.getUnitPrice()));
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(JsonFormat.printer().print(response));
    }

    private ProductItem getProductItem(ProductEntity entity, float unitPrice) {
        return ProductItem.newBuilder().setName(entity.getTitle()).setImageUrl(entity.getImageUrl())
                .setDescription(entity.getDescription())
                .setPrice(unitPrice).build();
    }

    private Category getCategory(CategoryEntity category) {
        return Category.newBuilder().setDescription(category.getDescription()).setName(category.getTitle())
                .setImageUrl(category.getImageUrl()).build();
    }

}