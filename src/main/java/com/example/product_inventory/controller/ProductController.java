package com.example.product_inventory.controller;

import com.example.product_inventory.model.Product;
import com.example.product_inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping("/by-category")
    public List<String> getByCategory(@RequestParam String category) {
        return service.getProductNamesByCategory(category);
    }

    @GetMapping("/costliest")
    public String getCostliestProduct() {
        return service.getCostliestProduct();
    }

    @GetMapping("/search")
    public List<Product> searchByName(@RequestParam String name) {
        return service.searchProductByName(name);
    }

    @GetMapping("/average")
    public Map<String, Double> getAveragePriceByCategory() {
        return service.getAveragePriceByCategory();
    }

    // Optional: additional endpoints

    @GetMapping("/")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }
    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return service.updateProduct(id, updatedProduct);
    }


    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            service.saveProductsFromCsv(file);
            return ResponseEntity.ok("File uploaded and products saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }





}
