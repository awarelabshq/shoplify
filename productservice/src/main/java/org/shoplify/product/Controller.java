package org.shoplify.product;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.shoplify.product.model.CategoryEntity;
import org.shoplify.product.repos.CategoryRepository;
import org.shoplify.product.util.ServiceUtil;
import org.shoplify.productservice.Category;
import org.shoplify.productservice.ListCategoriesRequest;
import org.shoplify.productservice.ListCategoriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/health/check")
    public String healthCheck() throws Exception {
        return "healthy";
    }

    @PostMapping(value = "/product/list_categories")
    public String createUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvalidProtocolBufferException {
        ListCategoriesRequest request = ServiceUtil.getRequestBody(httpServletRequest, ListCategoriesRequest.class);
        logger.info("Got request for list categories");
        List<CategoryEntity> categories = categoryRepository.findAll();
        ListCategoriesResponse.Builder response = ListCategoriesResponse.newBuilder();
        if (!categories.isEmpty()) {
            response.setFeaturedCategory(getCategory(categories.get(0)));
        }
        for (int i = 1; i < categories.size(); i++) {
            response.addCategories(getCategory(categories.get(i)));
        }
        return JsonFormat.printer().print(response);
    }

    private Category getCategory(CategoryEntity category) {
        return Category.newBuilder().setDescription(category.getDescription()).setName(category.getTitle())
                .setImageUrl(category.getImageUrl()).build();
    }

}