package com.online.shop.dto.request;

import com.online.shop.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    private String slug;

    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm không được âm")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity;

    private String scale; // 1:18, 1:24, 1:64 hoặc 1/144 HG, 1/100 MG...

    private String material; // Diecast kim loại, Nhựa ABS, Resin...

    private String brand; // AutoArt, Bandai, Threezero...

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Long categoryId;

    private ProductStatus status;

    private Boolean featured;

    private String imageUrl;

    private List<String> images;
}
