package com.dentify.domain.product.controller;

import com.dentify.calendar.dto.response.ProductResponse;
import com.dentify.domain.product.dto.ActiveProductResponse;
import com.dentify.domain.product.dto.ProductDTO;
import com.dentify.domain.product.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @PostMapping("/save")
    public String saveProduct(@RequestBody ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }

    @PostMapping("/save/all")
    public String saveAll(@RequestBody List<ProductDTO> products) {
        return productService.saveAll(products);
    }

    @GetMapping("/active")
    public List<ActiveProductResponse> getActiveProducts(){
        return productService.getActiveProducts();
    }
}
