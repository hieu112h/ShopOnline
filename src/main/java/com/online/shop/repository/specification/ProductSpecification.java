package com.online.shop.repository.specification;

import com.online.shop.entity.Product;
import com.online.shop.entity.ProductStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String keyword,
            Long categoryId,
            String scale,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase().trim() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate brandLike = cb.like(cb.lower(root.get("brand")), pattern);
                predicates.add(cb.or(nameLike, brandLike));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (StringUtils.hasText(scale)) {
                predicates.add(cb.equal(cb.lower(root.get("scale")), scale.toLowerCase().trim()));
            }

            if (StringUtils.hasText(brand)) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.toLowerCase().trim()));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
