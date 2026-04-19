package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.StatusComanda;

public class TranzitieInvalidaException extends RuntimeException {
    public TranzitieInvalidaException(StatusComanda sc1, StatusComanda sc2) {
        super("Tranzitie invalida: " + sc1 + " -> " + sc2);
    }
}
