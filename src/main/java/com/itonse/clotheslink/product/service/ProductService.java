package com.itonse.clotheslink.product.service;

import com.itonse.clotheslink.product.dto.ProductDto;
import com.itonse.clotheslink.product.dto.ProductResponse;
import com.itonse.clotheslink.seller.domain.Seller;

public interface ProductService {
    ProductResponse addProduct(String token, ProductDto dto);

    Seller validateSeller(String token);
}
