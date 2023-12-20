package com.products.controller;

import com.products.dto.ProductRatingDetails;
import com.products.dto.RatingDTO;
import com.products.entity.Product;
import com.products.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/{id}")
    @CircuitBreaker(name = "inventory", fallbackMethod = "executeFallBackMethod")
    @Retry(name = "inventory", fallbackMethod = "executeFallBackMethod")
    public ResponseEntity<ProductRatingDetails> getProductRatings(@PathVariable("id") Integer id) {
        Product product = productService.getProductById(id);
        String apiUrl =
                UriComponentsBuilder.fromUriString("http://localhost:9093/api/v1/ratings/{id}")
                        .buildAndExpand(id)
                        .toUriString();
        System.out.println("URL :" + apiUrl);
        ResponseEntity<RatingDTO> responseEntity = restTemplate.getForEntity(apiUrl, RatingDTO.class);
        return new ResponseEntity<>(buildProductRatingDetails(product, responseEntity.getBody()), HttpStatus.CREATED);
    }

    public ResponseEntity<ProductRatingDetails> executeFallBackMethod(Integer id, RuntimeException runtimeException) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(buildProductRatingDetails(product, null), HttpStatus.OK);
    }

    private ProductRatingDetails buildProductRatingDetails(Product product, RatingDTO ratingDTO) {
        ProductRatingDetails productRatingDetails = new ProductRatingDetails();
        productRatingDetails.setProductId(product.getId());
        productRatingDetails.setProductName(product.getName());
        productRatingDetails.setRatingDetails(ratingDTO != null ? ratingDTO : new RatingDTO());
        return productRatingDetails;
    }
}
