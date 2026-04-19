package com.pao.project.fooddelivery.exception;

public class ProcentInvalidException extends RuntimeException {
    public ProcentInvalidException(double procent) {
        super("Procentul este invalid: " + procent);
    }
}
