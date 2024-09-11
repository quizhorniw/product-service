package com.drevotiuk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents an item in an order.
 * <p>
 * This class contains the details of a product in an order, including the
 * product's ID and the quantity ordered.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItem {
  /** The ID of the product associated with this order item. */
  private String productId;

  /** The quantity of the product ordered. */
  private int qty;
}
