package com.example.Api;

import java.nio.file.AccessDeniedException;
import java.time.ZonedDateTime;

import com.example.Service.RatingService;
import com.example.ViewModel.RatingListVm;
import com.example.ViewModel.RatingPostVm;
import com.example.ViewModel.RatingVm;
import com.example.ViewModel.ResponseStatusVm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/backoffice/ratings")
    public ResponseEntity<RatingListVm> getRatingListWithFilter(
            @RequestParam(value = "productName", defaultValue = "", required = false) String productName,
            @RequestParam(value = "customerName", defaultValue = "", required = false) String cusName,
            @RequestParam(value = "message", defaultValue = "", required = false) String message,
            @RequestParam(value = "createdFrom", defaultValue = "1970-01-01T00:00:00.000Z", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdFrom,
            @RequestParam(
                    value = "createdTo",
                    defaultValue = "#{T(java.time.ZonedDateTime).now().toString()}",
                    required = false
            )
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdTo,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return ResponseEntity.ok(ratingService.getRatingListWithFilter(productName, cusName,
                message, createdFrom, createdTo,
                pageNo, pageSize));
    }

    @DeleteMapping("/backoffice/ratings/{id}")
    public ResponseEntity<ResponseStatusVm> deleteRating(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.deleteRating(id));
    }

    @GetMapping({"/storefront/ratings/products/{productId}"})
    public ResponseEntity<RatingListVm> getRatingList(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {

        return ResponseEntity.ok(ratingService.getRatingListByProductId(productId, pageNo, pageSize));
    }

    @PostMapping("/storefront/ratings")
    public ResponseEntity<RatingVm> createRating(@Valid @RequestBody RatingPostVm ratingPostVm) {
        try {
            return ResponseEntity.ok(ratingService.createRating(ratingPostVm));
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/storefront/ratings/product/{productId}/average-star")
    public Double getAverageStarOfProduct(@PathVariable Long productId) {
        return ratingService.calculateAverageStar(productId);
    }
}