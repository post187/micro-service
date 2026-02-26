package com.example.Helper;

import com.example.Model.Dto.UserDto.UserDto;
import com.example.Model.Dto.order.CartDto;
import com.example.Model.Dto.order.OrderDto;
import com.example.Model.Entity.Cart;
import com.example.Model.Entity.Order;

import java.util.stream.Collectors;

public interface CartMappingHelper {
    static CartDto map(Cart cart) {
        if (cart == null) return null;
        return CartDto.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .orderDtos(cart.getOrders()
                        .stream()
                        .map(order -> OrderDto.builder()
                                .orderId(order.getId())
                                .orderDate(order.getOrderDate())
                                .orderDesc(order.getOrderDesc())
                                .orderFee(order.getOrderFee())
                                .build())
                        .collect(Collectors.toSet()))
                .userDto(
                        UserDto.builder()
                                .id(cart.getUserId())
                                .build())
                .build();
    }

    static Cart map(final CartDto cartDto) {
        if (cartDto == null) return null;
        return Cart.builder()
                .id(cartDto.getCartId())
                .userId(cartDto.getUserId())
                .orders(cartDto.getOrderDtos()
                        .stream()
                        .map(orderDto -> Order.builder()
                                .id(orderDto.getOrderId())
                                .orderDate(orderDto.getOrderDate())
                                .orderDesc(orderDto.getOrderDesc())
                                .orderFee(orderDto.getOrderFee())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
