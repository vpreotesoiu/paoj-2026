package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.CantitateInvalidaException;
import com.pao.project.fooddelivery.exception.ProdusInvalidException;
import com.pao.project.fooddelivery.exception.ProdusNegasitException;

import java.util.HashMap;
import java.util.Map;

public class Cos {

    private Map<ProdusPersonalizat, Integer> produse;

    public Cos() {
        this.produse = new HashMap<ProdusPersonalizat, Integer>();
    }

    public void adaugaProdus(Produs p, int cant) {
        if (p == null) {
            throw new ProdusInvalidException(p);
        }
        adaugaProdus(new ProdusPersonalizat(p), cant);
    }
    public void adaugaProdus(ProdusPersonalizat pp, int cant) {
        if (pp == null) {
            throw new IllegalArgumentException("Produsul personalizat nu poate fi vid!");
        }
        if (cant <= 0) {
            throw new CantitateInvalidaException(cant);
        }
        // adauga cantitatea peste valoare daca produsul exista,
        // altfel seteaza cantitatea respectiva ca valoare
        if (produse.containsKey(pp)) {
            produse.put(pp, produse.get(pp) + cant);
        }
        else {
            produse.put(pp, cant);
        }
    }

    public void stergeProdus(ProdusPersonalizat pp) {
        if (pp == null) {
            throw new IllegalArgumentException("Produsul personalizat nu poate fi vid!");
        }
        if (produse.remove(pp) == null) { // remove returneaza valoarea asociata cheii,
                                         // deci ar putea da exceptie daca valoarea e
                                         // una falsy, cum ar fi 0. asa ca, verific
                                         // comparativ cu null (care este intradevar
                                         // returnat de remove cand cheia nu este gasita).
            throw new ProdusNegasitException(pp.getProdus());
        }
    }

    public void goleste() {
        produse.clear();
    }

    public boolean esteGol() {
        return produse.isEmpty();
    }

    public double calculeazaSubtotal() {
        double subtotal = 0;

        for (Map.Entry<ProdusPersonalizat, Integer> elem : produse.entrySet()) {
            ProdusPersonalizat pp = elem.getKey();
            Integer cant = elem.getValue();
            subtotal += pp.calculeazaPretTotal() * cant;
        }

        return subtotal;
    }

    public Map<ProdusPersonalizat, Integer> getProduse() { // folosit pentru copia produselor
                                               // in comenzi efectuate
        return Map.copyOf(produse);
    }

    @Override
    public String toString() {
        return "Cos{produse=" + produse + "}";
    }
}
