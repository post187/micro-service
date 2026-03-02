package com.example.Service.Impl;

import com.example.Exception.Wrapper.CategoryNotFoundException;
import com.example.Helper.CategoryMappingHelper;
import com.example.Model.Dto.CategoryDto;
import com.example.Model.Entity.Category;
import com.example.Repository.CategoryRepository;
import com.example.Repository.CategoryRepositoryPagingAndSorting;
import com.example.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final CategoryRepositoryPagingAndSorting categoryRepositoryPagingAndSorting;

    @Override
    public Flux<List<CategoryDto>> findAll() {
        return Flux.just(categoryRepository.findAll())
                .flatMap(categories -> Flux.fromIterable(categories)
                        .map(CategoryMappingHelper::map)
                        .distinct()
                        .collectList()
                )
                .map(categoryDtos -> {
                    log.info("Categories fetched successfully");
                    return categoryDtos;
                })
                .onErrorResume(throwable -> {
                    log.error("Error while fetching categories: " + throwable.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    @Override
    public Page<CategoryDto> findAllCategory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDto> categoryDtos = categoryPage.getContent()
                .stream()
                .map(CategoryMappingHelper::map)
                .distinct()
                .toList();

        return new PageImpl<>(categoryDtos, pageable, categoryPage.getTotalPages());
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        if (!categoryPage.hasContent()) return new ArrayList<>();

        List<CategoryDto> categoryDtos = categoryPage.getContent()
                .stream()
                .map(CategoryMappingHelper::map)
                .distinct()
                .toList();
        return categoryDtos;
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryMappingHelper::map)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id[%d] not found", categoryId)));
    }

    @Override
    public Mono<CategoryDto> save(CategoryDto categoryDto) {
        return Mono.just(categoryDto)
                .map(CategoryMappingHelper::map)
                .flatMap(
                        category ->
                                Mono.fromCallable(() -> CategoryMappingHelper.map(categoryRepository.save(category)))
                                        .onErrorMap(DataIntegrityViolationException.class, e -> new CategoryNotFoundException("Bad Request", e))
                );
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto) {
        try {
            Category existingCategory = categoryRepository.findById(categoryDto.getId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryDto.getId()));

            BeanUtils.copyProperties(categoryDto, existingCategory, "categoryId", "parentCategoryDto");

            if (categoryDto.getParentCategoryDto() != null) {
                existingCategory.setParentCategory(CategoryMappingHelper.map(categoryDto.getParentCategoryDto()));
            }

            return CategoryMappingHelper.map(categoryRepository.save(existingCategory));
        } catch (CategoryNotFoundException e) {
            log.error("Error updating category. Category with id [{}] not found.", categoryDto.getId());
            throw new CategoryNotFoundException(String.format("Category with id [%d] not found.", categoryDto.getId()), e);
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating category: Data integrity violation", e);
            throw new CategoryNotFoundException("Error updating category: Data integrity violation", e);
        } catch (Exception e) {
            log.error("Error updating category", e);
            throw new CategoryNotFoundException("Error updating category", e);
        }
    }

    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        try {
            CategoryDto existingCategoryDto = this.findById(categoryId);

            if (categoryId.equals(categoryDto.getId())) {
                throw new BadRequestException("Id not match");
            }
            Category existingCategory = CategoryMappingHelper.map(existingCategoryDto);
            BeanUtils.copyProperties(categoryDto, existingCategory, "categoryId", "parentCategoryDto");

            if (categoryDto.getParentCategoryDto() != null) {
                existingCategory.setParentCategory(CategoryMappingHelper.map(categoryDto.getParentCategoryDto()));
            }

            Category updatedCategory = categoryRepository.save(existingCategory);

            // Map the updated Category back to CategoryDto and return
            return CategoryMappingHelper.map(updatedCategory);

        } catch (CategoryNotFoundException e) {
            log.error("Error updating category. Category with id [{}] not found.", categoryId);
            throw new CategoryNotFoundException(String.format("Category with id [%d] not found.", categoryId), e);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating category: Data integrity violation", e);
            throw new CategoryNotFoundException("Error updating category: Data integrity violation", e);
        } catch (Exception e) {
            log.error("Error updating category", e);
            throw new CategoryNotFoundException("Error updating category", e);
        }
    }

    @Override
    public void deleteById(Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (CategoryNotFoundException e) {
            log.error("Error delete category", e);
            throw new CategoryNotFoundException("Error updating category", e);
        }
    }
}
