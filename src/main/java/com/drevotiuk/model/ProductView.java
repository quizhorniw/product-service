package com.drevotiuk.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a view of a product with essential details.
 * <p>
 * This class is used to present a simplified representation of a
 * {@link Product} for display purposes,
 * containing fields such as name, category, price, and quantity.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductView {
  /** The name of the product. */
  private String name;

  /** The category of the product. */
  private String category;

  /** The price of the product. */
  private BigDecimal price;

  /** The quantity of the product in stock. */
  private int qty;

  public ProductView(Product product) {
    this.name = product.getName();
    this.category = product.getCategory().name();
    this.price = product.getPrice();
    this.qty = product.getQty();
  }
}
