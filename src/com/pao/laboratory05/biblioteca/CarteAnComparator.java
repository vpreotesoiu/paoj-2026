package com.pao.laboratory05.biblioteca;

import java.util.Comparator;

public class CarteAnComparator implements Comparator<Carte> {
    public int compare(Carte c1, Carte c2) {
        return c1.getAn() - c2.getAn();
    }
}