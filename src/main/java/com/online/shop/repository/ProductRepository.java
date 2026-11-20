package com.online.shop.repository;

import com.online.shop.entity.Product;
import com.online.shop.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByFeaturedTrueAndStatus(ProductStatus status);
}
