package com.example.Service;

import com.example.Model.Dto.ProductDto;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ProductService {

    Flux<List<ProductDto>> findAll();

    ProductDto findById(final Long productId);

    ProductDto save(final ProductDto productDto);

    ProductDto update(final ProductDto productDto);

    ProductDto update(final Long productId, final ProductDto productDto);

    void deleteById(final Long productId);
}
