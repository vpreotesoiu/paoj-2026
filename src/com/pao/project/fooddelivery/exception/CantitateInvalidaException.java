package com.pao.project.fooddelivery.exception;

public class CantitateInvalidaException extends RuntimeException {
    public CantitateInvalidaException(int cantitate) {
        super("Cantitate invalida: " + cantitate);
    }
}
