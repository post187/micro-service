package com.example.Helper;

import com.example.Model.Dto.CategoryDto;
import com.example.Model.Dto.ProductDto;
import com.example.Model.Entity.Category;
import com.example.Model.Entity.Product;

public interface ProductMappingHelper {
    static ProductDto map(final Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .productTitle(product.getProductTitle())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .priceUnit(product.getPriceUnit())
                .quantity(product.getQuantity())
                .categoryDto(
                        CategoryDto.builder()
                                .id(product.getCategory().getId())
                                .categoryTitle(product.getCategory().getCategoryTitle())
                                .imageUrl(product.getCategory().getImage_url())
                                .build())
                .build();
    }

    static Product map(final ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .productTitle(productDto.getProductTitle())
                .imageUrl(productDto.getImageUrl())
                .sku(productDto.getSku())
                .priceUnit(productDto.getPriceUnit())
                .quantity(productDto.getQuantity())
                .category(
                        Category.builder()
                                .id(productDto.getCategoryDto().getId())
                                .categoryTitle(productDto.getCategoryDto().getCategoryTitle())
                                .image_url(productDto.getCategoryDto().getImageUrl())
                                .build())
                .build();
    }
}
