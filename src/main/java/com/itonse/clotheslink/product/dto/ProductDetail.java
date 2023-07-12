package com.itonse.clotheslink.product.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ProductDetail {

    private Long ProductId;
    private String name;
    private String description;
    private int price;
    private int stock;
}
