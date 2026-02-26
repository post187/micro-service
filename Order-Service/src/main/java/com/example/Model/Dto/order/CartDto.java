package com.example.Model.Dto.order;

import com.example.Model.Dto.UserDto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long cartId;
    private Long userId;

    @JsonProperty("order")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<OrderDto> orderDtos;

    @JsonProperty("user")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto userDto;
}
