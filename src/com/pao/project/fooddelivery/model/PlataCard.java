package com.pao.project.fooddelivery.model;

public class PlataCard implements IPlata {
    private static double TVA = 0.09;
    private double subtotal;

    public PlataCard(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public double calculeazaTotal() {
        return subtotal * (1 + TVA);
    }

    @Override
    public MetodaPlata getMetodaPlata() {
        return MetodaPlata.CARD;
    }
}
