package com.example.product_inventory.service;


import com.example.product_inventory.model.Product;
import com.example.product_inventory.repository.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<String> getProductNamesByCategory(String category) {
        return repo.findByCategoryIgnoreCase(category.trim())
                .stream()
                .map(Product::getName)
                .toList();
    }

    public int getTotalInventoryValue() {
        return repo.findAll()
                .stream()
                .mapToInt(Product::getPrice)
                .sum();
    }

    public String getCostliestProduct() {
        return repo.findAll()
                .stream()
                .max(Comparator.comparingInt(Product::getPrice))
                .map(p -> {
                    System.out.println("Costliest Product: " + p.getName().toUpperCase());
                    return p.getName().toUpperCase();
                })
                .orElse("No products available");
    }

    public Map<String, List<String>> getProductsGroupedByCategory() {
        Map<String, List<Product>> grouped = repo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        Map<String, List<String>> categoryToNames = new HashMap<>();

        grouped.forEach((category, items) -> {
            List<String> names = items.stream().map(Product::getName).toList();
            categoryToNames.put(category, names);
            System.out.println("• " + category);
            names.forEach(name -> System.out.println("   - " + name));
        });

        return categoryToNames;
    }

    public Map<String, List<String>> getAffordableAndExpensiveProducts(int threshold) {
        Map<Boolean, List<Product>> partitioned = repo.findAll()
                .stream()
                .collect(Collectors.partitioningBy(p -> p.getPrice() > threshold));

        List<String> expensive = partitioned.get(true).stream().map(Product::getName).toList();
        List<String> cheap = partitioned.get(false).stream().map(Product::getName).toList();

        System.out.println("Expensive Products:");
        expensive.forEach(System.out::println);

        System.out.println("Cheap Products:");
        cheap.forEach(System.out::println);

        Map<String, List<String>> result = new HashMap<>();
        result.put("expensive", expensive);
        result.put("cheap", cheap);

        return result;
    }

    public Map<String, Double> getAveragePriceByCategory() {
        return repo.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingInt(Product::getPrice)
                ));
    }

    public List<Product> searchProductByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }
    public Product saveProduct(Product product) {
        return repo.save(product);
    }

    public void deleteProduct(Long id) {
        repo.deleteById(id);
    }
    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setImageUrl(updatedProduct.getImageUrl());

        return repo.save(existing);
    }

    public void saveProductsFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<Product> products = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                Product product = new Product();
                product.setName(record.get("name"));
                product.setCategory(record.get("category"));
                product.setPrice(Integer.parseInt(record.get("price")));
                product.setImageUrl(record.get("imageUrl"));
                products.add(product);
            }

            repo.saveAll(products);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }



}
