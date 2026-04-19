package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.Restaurant;

public class RestaurantNegasitException extends RuntimeException {
    public RestaurantNegasitException(Restaurant restaurant) {
        super("Restaurantul nu a fost gasit in favorite: " + restaurant);
    }
}
