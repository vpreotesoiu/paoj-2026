package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.Produs;

public class ProdusNegasitException extends RuntimeException {
    public ProdusNegasitException(Produs p) {
        super("Produsul nu a putut fi gasit: " + p);
    }
}
