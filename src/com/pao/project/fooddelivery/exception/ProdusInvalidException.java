package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.Produs;

public class ProdusInvalidException extends RuntimeException {
    public ProdusInvalidException(Produs p) {
        super("Produsul este invalid: " + p);
    }
}
