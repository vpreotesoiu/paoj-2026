package com.pao.project.fooddelivery.exception;

public class DebitInvalidException extends RuntimeException {
    public DebitInvalidException(double balanta, double suma) {
        super("Debitul este invalid. Balanta: " + balanta + ", suma: " + suma);
    }
}
