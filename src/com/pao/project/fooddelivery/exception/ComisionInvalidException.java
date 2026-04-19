package com.pao.project.fooddelivery.exception;

public class ComisionInvalidException extends RuntimeException {
    public ComisionInvalidException(double comision) {
        super("Comisionul soferului este invalid: " + comision);
    }
}
