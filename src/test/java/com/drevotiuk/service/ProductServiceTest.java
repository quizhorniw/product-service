package com.drevotiuk.service;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.bson.types.ObjectId;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.drevotiuk.model.OrderItem;
import com.drevotiuk.model.Product;
import com.drevotiuk.model.ProductCategory;
import com.drevotiuk.model.ProductView;
import com.drevotiuk.model.exception.ProductNotFoundException;
import com.drevotiuk.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
  @Mock
  private ProductRepository productRepository;
  private ProductServiceUtils productServiceUtils;
  private ProductService underTest;

  @BeforeEach
  void setUp() {
    productServiceUtils = new ProductServiceUtils();
    underTest = new ProductService(productRepository, productServiceUtils);
  }

  @Test
  void canFindAllProducts() {
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
  void shouldProduceTotalPrice() {
    // given
    ObjectId productId = ObjectId.get();
    Product product = new Product(productId, "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);
    OrderItem item = new OrderItem(productId.toString(), 3);
    given(productRepository.findById(productId)).willReturn(Optional.of(product));

    // when
    BigDecimal result = underTest.consumeAndProduceTotalPrice(item);

    // then
    assertThat(result)
        .isNotNull()
        .isEqualTo(BigDecimal.valueOf(30));

    verify(productRepository).findById(productId);
  }

  @Test
  void shouldReturnNullWhenProducingTotalPrice() {
    // given
    ObjectId productId = ObjectId.get();
    Product product = new Product(productId, "Test product", ProductCategory.TOYS, BigDecimal.TEN, 80);
    OrderItem item = new OrderItem(productId.toString(), 81);
    given(productRepository.findById(productId)).willReturn(Optional.of(product));

    // when
    BigDecimal result = underTest.consumeAndProduceTotalPrice(item);

    // then
    assertThat(result).isNull();
    verify(productRepository).findById(productId);
  }
}
