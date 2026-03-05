package com.example.ViewModel;

public record RatingPostVm(String content,
                           int star,
                           Long productId,
                           String productName) {
}
