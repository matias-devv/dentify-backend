package com.floss.odontologia.service.impl;

import com.floss.odontologia.model.Product;
import com.floss.odontologia.repository.IProductRepository;
import com.floss.odontologia.service.interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Override
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product validateIfProductExists(Long id_product) {
        Product product = this.getProductEntityById(id_product);

        if ( product == null ) {
            return null;
        }
        return product;
    }
}
