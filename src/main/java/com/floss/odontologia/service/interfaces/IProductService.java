package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.ProductDTO;
import com.floss.odontologia.model.Product;

public interface IProductService {

    public String saveProduct(ProductDTO request);

    public Product getProductEntityById(Long id);

    public Product validateIfProductExists(Long id_product);
}
