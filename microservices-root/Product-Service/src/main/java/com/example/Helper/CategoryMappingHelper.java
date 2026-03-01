package com.example.Helper;

import com.example.Model.Dto.CategoryDto;
import com.example.Model.Entity.Category;

import java.util.Optional;

public interface CategoryMappingHelper {

    static CategoryDto map(final Category category) {
        final var parentCategory = Optional.ofNullable(category.getParentCategory())
                .orElseGet(Category::new);
        return CategoryDto.builder()
                .id(category.getId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImage_url())
                .parentCategoryDto(
                        CategoryDto.builder()
                                .id(parentCategory.getId())
                                .categoryTitle(parentCategory.getCategoryTitle())
                                .imageUrl(parentCategory.getImage_url())
                                .build()
                )
                .build();
    }

    static Category map(CategoryDto categoryDto) {
        final var parentCategoryDto = Optional.ofNullable(categoryDto.getParentCategoryDto())
                .orElseGet(CategoryDto::new);
        return Category.builder()
                .id(categoryDto.getId())
                .categoryTitle(categoryDto.getCategoryTitle())
                .image_url(categoryDto.getImageUrl())
                .parentCategory(Category.builder()
                        .id(parentCategoryDto.getId())
                        .categoryTitle(parentCategoryDto.getCategoryTitle())
                        .image_url(parentCategoryDto.getImageUrl())
                        .build())
                .build();
    }

}