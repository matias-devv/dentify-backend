package com.floss.odontologia.controller;

import com.floss.odontologia.dto.request.ProductDTO;
import com.floss.odontologia.service.interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @PostMapping("/save")
    public String saveProduct(@RequestBody ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }
}
