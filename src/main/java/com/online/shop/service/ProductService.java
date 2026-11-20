package com.online.shop.service;

import com.online.shop.dto.common.PageResponse;
import com.online.shop.dto.request.ProductRequest;
import com.online.shop.dto.response.ProductResponse;
import com.online.shop.entity.ProductStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    PageResponse<ProductResponse> getProducts(
            String keyword,
            Long categoryId,
            String scale,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status,
            Pageable pageable
    );

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySlug(String slug);

    List<ProductResponse> getFeaturedProducts();

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
