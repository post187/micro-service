package com.example.Api;

import com.example.Constant.ConfigConstant;
import com.example.Model.Dto.FavouriteDto;
import com.example.Model.Dto.Response.CollectionResponse;
import com.example.Model.Entity.Id.FavouriteId;
import com.example.Service.FavouriteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteApi {

    @Autowired
    private final FavouriteService favouriteService;

    @GetMapping
    public ResponseEntity<CollectionResponse<FavouriteDto>> findAll() {
        return ResponseEntity.ok(new CollectionResponse<>(this.favouriteService.findAll()));
    }

    @GetMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<FavouriteDto> findById(@PathVariable("userId") final String userId,
                                                 @PathVariable("productId") final String productId,
                                                 @PathVariable("likeDate") final String likeDate) {
        return ResponseEntity.ok(this.favouriteService.findById(
                new FavouriteId(Long.parseLong(userId), Long.parseLong(productId),
                        LocalDateTime.parse(likeDate, DateTimeFormatter.ofPattern(ConfigConstant.LOCAL_DATE_TIME_FORMAT)))));
    }

    @GetMapping("/find")
    public ResponseEntity<FavouriteDto> findById(@RequestBody
                                                 @NotNull(message = "Input must not be NULL")
                                                 @Valid final FavouriteId favouriteId) {
        return ResponseEntity.ok(this.favouriteService.findById(favouriteId));
    }

    @PostMapping
    public ResponseEntity<FavouriteDto> save(@RequestBody
                                             @NotNull(message = "Input must not be NULL")
                                             @Valid final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteService.save(favouriteDto));
    }

    @PutMapping
    public ResponseEntity<FavouriteDto> update(@RequestBody
                                               @NotNull(message = "Input must not be NULL")
                                               @Valid final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteService.update(favouriteDto));
    }

    @DeleteMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("userId") final String userId,
                                              @PathVariable("productId") final String productId,
                                              @PathVariable("likeDate") final String likeDate) {
        favouriteService.deleteById(
                new FavouriteId(Long.parseLong(userId), Long.parseLong(productId),
                        LocalDateTime.parse(likeDate, DateTimeFormatter.ofPattern(ConfigConstant.LOCAL_DATE_TIME_FORMAT)))
        );
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final FavouriteId favouriteId) {
        favouriteService.deleteById(favouriteId);
        return ResponseEntity.ok(true);
    }

}