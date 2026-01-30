package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.ProductDTO;
import com.floss.odontologia.model.Product;
import com.floss.odontologia.model.Speciality;
import com.floss.odontologia.repository.IProductRepository;
import com.floss.odontologia.service.interfaces.IProductService;
import com.floss.odontologia.service.interfaces.ISpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ISpecialityService iSpecialityService;

    @Override
    public String saveProduct(ProductDTO request) {

        Product product = new Product();

        product = this.setAttributes( product, request);

        Speciality speciality = iSpecialityService.getSpecialityEntityById(request.id_speciality());

        product.setSpeciality(speciality);

        productRepository.save(product);

        return "the product was saved successfully";
    }

    private Product setAttributes(Product product, ProductDTO request) {
        product.setName_product(request.name_product());
        product.setUnit_price(request.unit_price());
        product.setDescription(request.description());
        product.setActivo(request.activo());
        return product;
    }

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
