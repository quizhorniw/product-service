package com.drevotiuk.service;

import static org.assertj.core.api.Assertions.*;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.drevotiuk.model.exception.ProductNotFoundException;

public class ProductServiceUtilsTest {
  private ProductServiceUtils underTest;

  @BeforeEach
  void setUp() {
    underTest = new ProductServiceUtils();
  }

  @Test
  void shouldCreateProductNotFoundException() {
    // when
    ObjectId productId = new ObjectId("66e441940271e6c85203d755");
    ProductNotFoundException e = underTest.createProductNotFoundException(productId);

    // then
    assertThat(e).isInstanceOf(ProductNotFoundException.class);
  }
}
