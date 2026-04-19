package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.Restaurant;

public class RestaurantInvalidException extends RuntimeException {
    public RestaurantInvalidException(Restaurant r) {
        super("Restaurantul precizat este invalid: " + r);
    }
}
