package com.example.Helper;

import com.example.Model.Dto.FavouriteDto;
import com.example.Model.Dto.ProductDto;
import com.example.Model.Dto.UserDto;
import com.example.Model.Entity.Favourite;

public class FavouriteMappingHelper {
    public static FavouriteDto map(final Favourite favourite) {
        return FavouriteDto.builder()
                .userId(favourite.getUserId())
                .productId(favourite.getProductId())
                .likeDate(favourite.getLikeDate())
                .userDto(
                        UserDto.builder()
                                .userId(favourite.getUserId())
                                .build())
                .productDto(
                        ProductDto.builder()
                                .productId(favourite.getProductId())
                                .build())
                .build();
    }

    public static Favourite map(final FavouriteDto favouriteDto) {
        return Favourite.builder()
                .userId(favouriteDto.getUserId())
                .productId(favouriteDto.getProductId())
                .likeDate(favouriteDto.getLikeDate())
                .build();
    }
}
