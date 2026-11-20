package com.online.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 150, message = "Tên danh mục tối đa 150 ký tự")
    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    private String categoryGroup; // 'CAR_MODEL' hoặc 'ROBOT_MODEL'

    private Boolean active;
}
