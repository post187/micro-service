package com.example.Api;

import com.example.Model.Dto.CategoryDto;
import com.example.Model.Dto.ProductDto;
import com.example.Service.CategoryService;
import com.example.Service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Flux<List<ProductDto>> findAll() {
        log.info("ProductDto List, controller; fetch all categories");
        return productService.findAll();
    }

    // Get detailed information of a specific product
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findById(@PathVariable("productId")
                                               @NotBlank(message = "Input must not be blank!")
                                               @Valid final String productId) {
        log.info("ProductDto, resource; fetch product by id");
        return ResponseEntity.ok(productService.findById(Long.parseLong(productId)));
    }

    // Create a new product
    @PostMapping
    public ResponseEntity<ProductDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; save product");
        return ResponseEntity.ok(productService.save(productDto));
    }

    // Update information of all product
    @PutMapping
    public ResponseEntity<ProductDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product");
        return ResponseEntity.ok(productService.update(productDto));
    }

    // Update information of a product:
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable("productId")
                                             @NotBlank(message = "Input must not be blank!")
                                             @Valid final String productId,
                                             @RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product with productId");
        return ResponseEntity.ok(productService.update(Long.parseLong(productId), productDto));
    }

    // Delete a product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("productId") final String productId) {
        log.info("Boolean, resource; delete product by id");
        productService.deleteById(Long.parseLong(productId));
        return ResponseEntity.ok(true);
    }
}
