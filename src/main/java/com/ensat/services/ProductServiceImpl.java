package com.ensat.services;

import com.ensat.entities.Product;
import com.ensat.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Product service implement.
 */
@Service
public class ProductServiceImpl implements ProductService {
     @Autowired
     private ProductRepository productRepository;

    

    @Override
    @Cacheable(value = "products")
    public Iterable<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Integer id) {
        return productRepository.findById(id).get();
    }

    @Override
    @CachePut(value = "product", key = "#result.id")
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

}
