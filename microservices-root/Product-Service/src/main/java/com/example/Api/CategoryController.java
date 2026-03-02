package com.example.Api;

import com.example.Model.Dto.CategoryDto;
import com.example.Service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Flux<List<CategoryDto>>> findAll() {
        log.info("CategoryDto List, controller; fetch all categories");
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/paging")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryDto> categoryPage = categoryService.findAllCategory(page, size);
        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }

    @GetMapping("/paging-and-sorting")
    public ResponseEntity<List<CategoryDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "categoryId") String sortBy) {

        List<CategoryDto> list = categoryService.getAllCategories(pageNo, pageSize, sortBy);

        return new ResponseEntity<List<CategoryDto>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    // Get detailed information of a specific category:
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId")
                                                @NotBlank(message = "Input must not be blank")
                                                @Valid final String categoryId) {
        log.info("CategoryDto, resource; fetch category by id");
        return ResponseEntity.ok(categoryService.findById(Long.parseLong(categoryId)));
    }

    //     Create a new category
    @PostMapping
    public ResponseEntity<Mono<CategoryDto>> save(@RequestBody @NotNull(message = "Input must not be NULL")
                                                  @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; save category");
        return ResponseEntity.ok(categoryService.save(categoryDto));
    }

    // Update information of all category
    @PutMapping
    public ResponseEntity<CategoryDto> update(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category");
        return ResponseEntity.ok(categoryService.update(categoryDto));
    }

    // Update information of a category
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable("categoryId")
                                              @NotBlank(message = "Input must not be blank")
                                              @Valid final String categoryId,
                                              @RequestBody @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category with categoryId");
        return ResponseEntity.ok(categoryService.update(Long.parseLong(categoryId), categoryDto));
    }

    // Delete a category
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
        log.info("Boolean, resource; delete category by id");
        categoryService.deleteById(Long.parseLong(categoryId));
        return ResponseEntity.ok(true);
    }
}
