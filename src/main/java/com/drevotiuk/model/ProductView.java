package com.drevotiuk.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductView {
  private String name;
  private String category;
  private BigDecimal price;
  private int qty;

  public ProductView(Product product) {
    this.name = product.getName();
    this.category = product.getCategory().name();
    this.price = product.getPrice();
    this.qty = product.getQty();
  }
}
