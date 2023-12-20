package com.products.service;

import com.products.entity.Product;
import com.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Integer id) {
        return productRepository.findById(id).get();
    }
}
