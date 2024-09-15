package com.drevotiuk.service;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.bson.types.ObjectId;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductCategory;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.model.exception.ProductExistsException;
import com.drevotiuk.model.exception.ProductNotFoundException;
import com.drevotiuk.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductManagementServiceTest {
  @Mock
  private ProductRepository productRepository;
  @Mock
  private MongoTemplate mongoTemplate;
  private ProductServiceUtils productServiceUtils;
  private ProductManagementService underTest;

  @BeforeEach
  void setUp() {
    productServiceUtils = new ProductServiceUtils();
    underTest = new ProductManagementService(productRepository, productServiceUtils, mongoTemplate);
  }

  @Test
  void shouldFindAllProducts() {
    // when
    underTest.findAll();

    // then
    verify(productRepository).findAll();
  }

  @Test
  void shouldFindProductById() {
    // given
    ObjectId productId = ObjectId.get();
    Product product = new Product(productId, "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);
    given(productRepository.findById(productId)).willReturn(Optional.of(product));

    // when
    ProductView found = underTest.find(productId);

    // then
    assertThat(found).isEqualTo(new ProductView(product));
    verify(productRepository).findById(productId);
  }

  @Test
  void shouldThrowWhenDidNotFindProductById() {
    // given
    ObjectId productId = ObjectId.get();

    // when
    // then
    assertThatThrownBy(() -> underTest.find(productId))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("Product not found");

    verify(productRepository).findById(productId);
  }

  @Test
  void shouldAddProductToDatabase() {
    // given
    Product product = new Product(ObjectId.get(), "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);

    // when
    ProductView saved = underTest.create(product);

    // then
    assertThat(saved).isEqualTo(new ProductView(product));

    ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productArgumentCaptor.capture());

    Product capturedProduct = productArgumentCaptor.getValue();
    assertThat(capturedProduct).isEqualTo(product);
  }

  @Test
  void shouldThrowWhenCannotSaveProduct() {
    // given
    Product product = new Product(ObjectId.get(), "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);
    given(productRepository.existsByName(product.getName())).willReturn(true);

    // when
    // then
    assertThatThrownBy(() -> underTest.create(product))
        .isInstanceOf(ProductExistsException.class)
        .hasMessageStartingWith("Product with name")
        .hasMessageEndingWith("already exists");

    verify(productRepository, never()).save(product);
  }

  @Test
  void shouldUpdateProduct() {
    // given
    ObjectId productId = ObjectId.get();
    Product initial = new Product(productId, "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);
    Product updated = new Product(productId, "Test product", ProductCategory.HEALTH, BigDecimal.ONE, 40);
    given(productRepository.findById(productId)).willReturn(Optional.of(initial));

    // when
    ProductView result = underTest.update(productId, updated);

    // then
    assertThat(result).isEqualTo(new ProductView(updated));

    ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productArgumentCaptor.capture());

    Product capturedProduct = productArgumentCaptor.getValue();
    assertThat(capturedProduct).isEqualTo(updated);
  }

  @Test
  void shouldThrowWhenCannotUpdateProduct() {
    // given
    ObjectId productId = ObjectId.get();
    Product updated = new Product(productId, "Test product 1", ProductCategory.HEALTH, BigDecimal.ONE, 40);
    given(productRepository.findById(productId)).willReturn(Optional.empty());

    // when
    // then
    assertThatThrownBy(() -> underTest.update(productId, updated))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("Product not found");

    verify(productRepository, never()).save(updated);
  }

  @Test
  void shouldDeleteProductById() {
    // given
    ObjectId productId = ObjectId.get();
    given(productRepository.existsById(productId)).willReturn(true);

    // when
    underTest.delete(productId);

    // then
    verify(productRepository).deleteById(productId);
  }

  @Test
  void shouldThrowWhenCannotDeleteProduct() {
    // given
    ObjectId productId = ObjectId.get();
    given(productRepository.existsById(productId)).willReturn(false);

    // when
    // then
    assertThatThrownBy(() -> underTest.delete(productId))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("Product not found");

    verify(productRepository, never()).deleteById(productId);
  }
}
