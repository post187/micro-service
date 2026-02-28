package com.example.Service;

import com.example.Model.Dto.Product.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//blocking
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductDto getProductById(@PathVariable("productId") Integer productId);
}
