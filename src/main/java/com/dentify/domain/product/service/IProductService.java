package com.dentify.domain.product.service;

import com.dentify.calendar.dto.response.ProductResponse;
import com.dentify.domain.product.dto.ActiveProductResponse;
import com.dentify.domain.product.dto.ProductDTO;
import com.dentify.domain.product.model.Product;

import java.util.List;

public interface IProductService {

    public String saveProduct(ProductDTO request);

    public Product findProductById(Long id);

    public Product validateIfProductExists(Long id_product);

    public String saveAll(List<ProductDTO> products);

    void validateIfProductIsActive(Product product);

    List<ActiveProductResponse> getActiveProducts();
}
