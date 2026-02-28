package com.example.Service;

import com.example.Model.Dto.Product.ProductDto;
import com.example.Model.Dto.UserDto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CallAPI {
    private final WebClient.Builder webClient;
    @Bean
    public Mono<UserDto> receiverUserDto(Long userId, String token) {
        return webClient.baseUrl("http://USER-SERVICE").build()
                .get()
                .uri(uriBuilder ->
                uriBuilder
                        .path("/api/manager/user/{id}")
                        .build(userId)
        )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    @Bean
    public Mono<ProductDto> receiverProductDto(Long productId) {
        return webClient.baseUrl("http://PRODUCT-SERVICE").build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/products/{id}")
                                .build(productId))
                .retrieve()
                .bodyToMono(ProductDto.class);

    }

}
