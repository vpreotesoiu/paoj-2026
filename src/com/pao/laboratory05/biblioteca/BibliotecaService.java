package com.pao.laboratory05.biblioteca;

import java.util.Arrays;
import java.util.Comparator;

public class BibliotecaService {
    private Carte[] carti;
    private BibliotecaService() {
        this.carti = new Carte[0];
    }
    private static class Holder {
        private static final BibliotecaService INSTANCE = new BibliotecaService();
    }
    public static BibliotecaService getInstance() {
        return Holder.INSTANCE;
    }
    void addCarte(Carte carte) {
        Carte[] tmp = new Carte[carti.length + 1];
        System.arraycopy(carti, 0, tmp, 0, carti.length);
        tmp[carti.length] = carte;
        this.carti = tmp;
    }
    void listSortedByRating() {
        Carte[] tmp = new Carte[carti.length];
        System.arraycopy(carti, 0, tmp, 0, carti.length);
        Arrays.sort(tmp);
        for (Carte c : tmp) {
            System.out.println("Carte{titlu='" + c.getTitlu() + "', autor='" + c.getAutor() + "', an=" + c.getAn() + ", rating=" + c.getRating() + "}");
        }
    }

    void listSortedBy(Comparator<Carte> comparator) {
        Carte[] tmp = new Carte[carti.length];
        System.arraycopy(carti, 0, tmp, 0, carti.length);
        Arrays.sort(tmp, comparator);
        for (Carte c : tmp) {
            System.out.println("Carte{titlu='" + c.getTitlu() + "', autor='" + c.getAutor() + "', an=" + c.getAn() + ", rating=" + c.getRating() + "}");
        }
    }
}
