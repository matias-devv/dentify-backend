package com.dentify.domain.product.service;

import com.dentify.domain.product.dto.ProductDTO;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.speciality.model.Speciality;
import com.dentify.domain.product.repository.IProductRepository;
import com.dentify.domain.speciality.service.ISpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ISpecialityService specialityService;

    @Override
    public String saveProduct(ProductDTO dto) {

        Product product = this.setAttributes(dto);

        Speciality speciality = specialityService.getSpecialityEntityById(dto.id_speciality());

        product.setSpeciality(speciality);

        productRepository.save( product);

        return "the product was saved successfully";
    }

    private Product setAttributes( ProductDTO dto) {
        Product product = new Product();
        product.setName_product(dto.name_product());
        product.setUnit_price(dto.unit_price());
        product.setDescription(dto.description());
        product.setActive(dto.activo());
        return product;
    }

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById( id ).orElseThrow( () -> new RuntimeException("Product not found"));
    }

    @Override
    public Product validateIfProductExists(Long id_product) {
        Product product = this.findProductById(id_product);

        if ( product == null ) {
            return null;
        }
        return product;
    }

    @Override
    public String saveAll(List<ProductDTO> products) {

        List<Product> newProducts = new ArrayList<>();

        if ( products != null){

            products.forEach(dto -> {

                Product product = this.setAttributes(dto);

                Speciality speciality = specialityService.getSpecialityEntityById(dto.id_speciality());

                product.setSpeciality(speciality);

                newProducts.add(product);
            });
        }

        productRepository.saveAll(newProducts);

        return "All the products was saved successfully";
    }

    @Override
    public void validateIfProductIsActive(Product product) {
        if (!product.getActive()) {
            throw new RuntimeException("product is not active");
        }
    }
}
