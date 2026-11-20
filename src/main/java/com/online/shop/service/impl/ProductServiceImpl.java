package com.online.shop.service.impl;

import com.online.shop.dto.common.PageResponse;
import com.online.shop.dto.request.ProductRequest;
import com.online.shop.dto.response.ProductResponse;
import com.online.shop.entity.Category;
import com.online.shop.entity.Product;
import com.online.shop.entity.ProductStatus;
import com.online.shop.exception.BadRequestException;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.CategoryRepository;
import com.online.shop.repository.ProductRepository;
import com.online.shop.service.ProductService;
import com.online.shop.util.SlugUtil;
import com.online.shop.repository.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(
            String keyword,
            Long categoryId,
            String scale,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status,
            Pageable pageable) {

        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String cleanedScale = StringUtils.hasText(scale) ? scale.trim() : null;
        String cleanedBrand = StringUtils.hasText(brand) ? brand.trim() : null;

        Specification<Product> spec = ProductSpecification.filter(
                cleanedKeyword, categoryId, cleanedScale, cleanedBrand, minPrice, maxPrice, status
        );

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Page<ProductResponse> responsePage = productPage.map(ProductResponse::from);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", id));
        return ProductResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "slug", slug));
        return ProductResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndStatus(ProductStatus.IN_STOCK)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "id", request.getCategoryId()));

        String slug = StringUtils.hasText(request.getSlug())
                ? SlugUtil.toSlug(request.getSlug())
                : SlugUtil.toSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity())
                .scale(request.getScale())
                .material(request.getMaterial())
                .brand(request.getBrand())
                .category(category)
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.IN_STOCK)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .imageUrl(request.getImageUrl())
                .images(request.getImages() != null ? new ArrayList<>(request.getImages()) : new ArrayList<>())
                .build();

        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "id", request.getCategoryId()));

        if (StringUtils.hasText(request.getSlug())) {
            String newSlug = SlugUtil.toSlug(request.getSlug());
            if (!newSlug.equals(product.getSlug()) && productRepository.existsBySlug(newSlug)) {
                throw new BadRequestException("Slug sản phẩm '" + newSlug + "' đã tồn tại!");
            }
            product.setSlug(newSlug);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setScale(request.getScale());
        product.setMaterial(request.getMaterial());
        product.setBrand(request.getBrand());
        product.setCategory(category);
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        product.setImageUrl(request.getImageUrl());
        if (request.getImages() != null) {
            product.setImages(new ArrayList<>(request.getImages()));
        }

        Product updated = productRepository.save(product);
        return ProductResponse.from(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", id));
        productRepository.delete(product);
    }
}
