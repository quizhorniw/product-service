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

@Document("products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
  @Id
  private ObjectId id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Category is required")
  private ProductCategory category;

  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price cannot be negative")
  private BigDecimal price;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer qty;
}
