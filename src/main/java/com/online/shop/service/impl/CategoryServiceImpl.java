package com.online.shop.service.impl;

import com.online.shop.dto.request.CategoryRequest;
import com.online.shop.dto.response.CategoryResponse;
import com.online.shop.entity.Category;
import com.online.shop.exception.BadRequestException;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.CategoryRepository;
import com.online.shop.service.CategoryService;
import com.online.shop.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String group) {
        List<Category> categories;
        if (StringUtils.hasText(group)) {
            categories = categoryRepository.findByCategoryGroupAndActiveTrue(group.toUpperCase());
        } else {
            categories = categoryRepository.findByActiveTrue();
        }

        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "id", id));
        return CategoryResponse.from(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "slug", slug));
        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String slug = StringUtils.hasText(request.getSlug())
                ? SlugUtil.toSlug(request.getSlug())
                : SlugUtil.toSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .categoryGroup(StringUtils.hasText(request.getCategoryGroup()) ? request.getCategoryGroup().toUpperCase() : "CAR_MODEL")
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "id", id));

        category.setName(request.getName());

        if (StringUtils.hasText(request.getSlug())) {
            String newSlug = SlugUtil.toSlug(request.getSlug());
            if (!newSlug.equals(category.getSlug()) && categoryRepository.existsBySlug(newSlug)) {
                throw new BadRequestException("Slug '" + newSlug + "' đã tồn tại!");
            }
            category.setSlug(newSlug);
        }

        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        if (StringUtils.hasText(request.getCategoryGroup())) {
            category.setCategoryGroup(request.getCategoryGroup().toUpperCase());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updated = categoryRepository.save(category);
        return CategoryResponse.from(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "id", id));
        categoryRepository.delete(category);
    }
}
