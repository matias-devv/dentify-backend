package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.ProductDTO;
import com.floss.odontologia.model.Product;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface IProductService {

    public String saveProduct(ProductDTO request);

    public Product findProductById(Long id);

    public Product validateIfProductExists(Long id_product);

    public String saveAll(List<ProductDTO> products);

    void validateIfProductIsActive(Product product);
}
