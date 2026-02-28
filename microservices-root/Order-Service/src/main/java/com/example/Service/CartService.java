package com.example.Service;

import com.example.Model.Dto.order.CartDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CartService {
    Mono<List<CartDto>> findAll();

    Mono<Page<CartDto>> findAll(int page, int size, String sortBy, String sortOrder);

    Mono<CartDto> findById(Long cartId);

    Mono<CartDto> save(final CartDto cartDto);

    Mono<CartDto> update(final CartDto cartDto);

    Mono<CartDto> update(final Long cartId, final CartDto cartDto);

    Mono<Void> deleteById(final Long cartId);
}