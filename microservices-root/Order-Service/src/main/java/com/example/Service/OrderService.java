package com.example.Service;

import com.example.Model.Dto.order.OrderDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {
    Mono<List<OrderDto>> findAll();

    Mono<Page<OrderDto>> findAll(int page, int size, String sortBy, String sortOrder);

    Mono<OrderDto> findById(Long orderId);

    Mono<OrderDto> save(final OrderDto orderDto);

    Mono<OrderDto> update(final OrderDto orderDto);

    Mono<OrderDto> update(final Long orderId, final OrderDto orderDto);

    Mono<Void> deleteById(final Long orderId);

    Boolean existsByOrderId(Long orderId);
}
