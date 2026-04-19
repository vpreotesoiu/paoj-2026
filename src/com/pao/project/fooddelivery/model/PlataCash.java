package com.pao.project.fooddelivery.model;

public class PlataCash implements IPlata {
    // plata cash nu are TVA
    private double subtotal;

    public PlataCash(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public double calculeazaTotal() {
        return subtotal;
    }

    @Override
    public MetodaPlata getMetodaPlata() {
        return MetodaPlata.CASH;
    }
}
