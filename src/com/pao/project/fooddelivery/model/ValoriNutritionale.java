package com.pao.project.fooddelivery.model;

public record ValoriNutritionale(int calorii, double proteine, double carbohidrati, double grasimi) {
    public ValoriNutritionale(int calorii, double proteine, double carbohidrati, double grasimi) {
        if (calorii < 0) {
            throw new IllegalArgumentException("Numarul de calorii nu poate fi negativ!");
        }
        if (proteine < 0) {
            throw new IllegalArgumentException("Cantitatea de proteine nu poate fi negativa!");
        }
        if (carbohidrati < 0) {
            throw new IllegalArgumentException("Cantitatea de carbohidrati nu poate fi negativa!");
        }
        if (grasimi < 0) {
            throw new IllegalArgumentException("Cantitatea de grasimi nu poate fi negativa!");
        }
        this.calorii = calorii;
        this.proteine = proteine;
        this.carbohidrati = carbohidrati;
        this.grasimi = grasimi;
    }

    @Override
    public String toString() {
        return "ValoriNutritionale{calorii=" + calorii + ", proteine=" + proteine + ", carbohidrati=" + carbohidrati + ", grasimi=" + grasimi + "}";
    }
}
