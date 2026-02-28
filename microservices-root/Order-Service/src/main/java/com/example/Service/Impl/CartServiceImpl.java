package com.example.Service.Impl;

import com.example.Exception.Wrapper.CartNotFoundException;
import com.example.Helper.CartMappingHelper;
import com.example.Model.Dto.order.CartDto;
import com.example.Model.Entity.Cart;
import com.example.Repository.CartRepository;
import com.example.Repository.OrderRepository;
import com.example.Security.JwtTokenFilter;
import com.example.Service.CallAPI;
import com.example.Service.CartService;
import com.example.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    private final CallAPI callAPI;

    private final OrderService orderService;

    @Override
    public Mono<List<CartDto>> findAll() {
        return Mono.fromSupplier(() -> {
            return cartRepository.findAll()
                    .stream()
                    .map(CartMappingHelper::map)
                    .toList();
        })
                .flatMap(cartDtos -> Flux.fromIterable(cartDtos)
                        .flatMap(cartDto ->
                                callAPI.receiverUserDto(cartDto.getUserId(), JwtTokenFilter.getTokenFromRequest())
                                        .map(userDto -> {
                                            cartDto.setUserDto(userDto);
                                            return  cartDto;
                                        })
                                        .onErrorResume(
                                                throwable -> {
                                                    log.error("Error fetching user info: {}", throwable.getMessage());
                                                    return Mono.just(cartDto);
                                                })
                        , 5).collectList()
                );
    }

    @Override
    public Mono<Page<CartDto>> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("CartDto List, service; fetch all carts with paging and sorting");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return Mono.fromSupplier(() -> cartRepository
                .findAll(pageable)
                .map(CartMappingHelper::map)
                )
                .flatMap(cartDtos -> Flux.fromIterable(cartDtos)
                        .flatMap(cartDto ->
                                        callAPI.receiverUserDto(cartDto.getUserId(), JwtTokenFilter.getTokenFromRequest())
                                                .map(userDto -> {
                                                    cartDto.setUserDto(userDto);
                                                    return cartDto;
                                                })
                                                .onErrorResume(throwable -> {
                                                    log.error("Error fetching user info: {}", throwable.getMessage());
                                                    return Mono.just(cartDto);
                                                })
                                , 5)
                        .collectList()
                        .map(resultList -> new PageImpl<>(resultList, pageable, resultList.size()))

                );
    }

    @Override
    public Mono<CartDto> findById(Long cartId) {
        log.info("CartDto, service; fetch cart by id");

        return Mono.fromSupplier(() -> cartRepository.findById(cartId)
                        .map(CartMappingHelper::map)
                        .orElseThrow(() -> new CartNotFoundException(String.format("Cart with id: %d not found", cartId)))
                )
                .flatMap(cartDto ->
                        callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest())
                                .map(userDto -> {
                                    cartDto.setUserDto(userDto);
                                    return cartDto;
                                })
                                .onErrorResume(throwable -> {
                                    log.error("Error fetching user info: {}", throwable.getMessage());
                                    return Mono.just(cartDto);
                                })
                );
    }


    @Override
    public Mono<CartDto> save(final CartDto cartDto) {
        log.info("CartDto, service; save cart");
        return Mono.fromSupplier(() -> cartRepository.save(modelMapper.map(cartDto, Cart.class)))
                .map(savedCart -> modelMapper.map(savedCart, CartDto.class));
    }


    @Override
    public Mono<CartDto> update(final CartDto cartDto) {
        log.info("CartDto, service; update cart");
        return Mono.fromSupplier(() -> cartRepository.save(CartMappingHelper.map(cartDto)))
                .map(CartMappingHelper::map);
    }

    @Override
    public Mono<CartDto> update(final Long cartId, final CartDto cartDto) {
        log.info("CartDto, service; update cart with cartId");
        return findById(cartId).flatMap(existingCartDto -> {
                    modelMapper.map(cartDto, existingCartDto);
                    return Mono.fromSupplier(() -> cartRepository.save(CartMappingHelper.map(existingCartDto)))
                            .map(CartMappingHelper::map);
                })
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart with id " + cartId + " not found")));
    }


    @Override
    public Mono<Void> deleteById(final Long cartId) {
        log.info("Void, service; delete cart by id");
        cartRepository.findById(cartId)
                .ifPresent(cart -> {
                    orderRepository.deleteAllByCart(cart);
                    cartRepository.deleteById(cartId);
                });
        return Mono.empty();
    }
}
