package com.drevotiuk.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a product in the inventory.
 * <p>
 * This class is mapped to the "products" collection in MongoDB and contains
 * details about the product, such as its ID, name, category, price, and
 * quantity.
 * </p>
 */
@Document("products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
  /** The unique identifier of the product. */
  @Id
  private ObjectId id;

  /** The name of the product. */
  @NotBlank(message = "Name is required")
  private String name;

  /** The category of the product. */
  @NotNull(message = "Category is required")
  private ProductCategory category;

  /** The price of the product. */
  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price cannot be negative")
  private BigDecimal price;

  /** The quantity of the product in stock. */
  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer qty;
}
