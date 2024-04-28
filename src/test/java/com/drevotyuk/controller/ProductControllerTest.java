package com.drevotyuk.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.drevotyuk.model.Product;
import com.drevotyuk.repository.ProductRepository;
import com.drevotyuk.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {
    @InjectMocks
    private ProductController controller;

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("TestProduct1", "Description1", 10.0, 5));
        productList.add(new Product("TestProduct2", "Description2", 15.0, 3));

        when(repository.findAll()).thenReturn(productList);

        Iterable<Product> products = controller.getAllProducts();
        Assert.assertEquals(2, ((List<Product>) products).size());
    }

    @Test
    public void testGetProductByName() {
        String productName = "TestProduct";
        Product product = new Product(productName, "Description", 10.0, 5);

        when(service.getProduct(anyString()))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        ResponseEntity<Product> responseEntity = controller.getProduct(productName);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(productName, responseEntity.getBody().getName());
    }

    @Test
    public void testAddProduct() {
        Product productToAdd = new Product("NewProduct", "Description", 10.0, 5);
        when(service.addProduct(productToAdd))
                .thenReturn(new ResponseEntity<>(productToAdd, HttpStatus.CREATED));

        ResponseEntity<Product> responseEntity = controller.addProduct(productToAdd);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assert.assertEquals(productToAdd, responseEntity.getBody());
    }

    @Test
    public void testUpdateProduct() {
        String productName = "TestProduct";
        Product productToUpdate = new Product(productName, "UpdatedDescription", 15.0, 3);

        when(service.updateProduct(productName, productToUpdate))
                .thenReturn(new ResponseEntity<>(productToUpdate, HttpStatus.OK));

        ResponseEntity<Product> responseEntity = controller.updateProduct(productName, productToUpdate);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(productToUpdate, responseEntity.getBody());
    }

    @Test
    public void testDeleteProduct() {
        String productName = "TestProduct";

        when(service.deleteProduct(productName)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<Product> responseEntity = controller.deleteProduct(productName);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
