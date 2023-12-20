package com.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRatingDetails {
    private Integer productId;
    private String productName;
    private RatingDTO ratingDetails;
}
