package com.pao.project.fooddelivery.exception;

public class SumaInvalidaException extends RuntimeException {
    public SumaInvalidaException(double suma) {
        super("Suma este invalida: " + suma);
    }
}
