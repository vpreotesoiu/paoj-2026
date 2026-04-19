package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.Client;
import com.pao.project.fooddelivery.model.Cos;
import com.pao.project.fooddelivery.model.Produs;
import com.pao.project.fooddelivery.model.ProdusPersonalizat;

import java.util.HashMap;
import java.util.Map;

public class CosService {
    private Map<Client, Cos> cosuri;

    private CosService() {
        this.cosuri = new HashMap<Client, Cos>();
    }

    private static class Holder {
        private static final CosService INSTANCE = new CosService();
    }

    public static CosService getInstance() {
        return Holder.INSTANCE;
    }

    public Cos getCos(Client c) {
        if (c == null){
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (!cosuri.containsKey(c)) {
            cosuri.put(c, new Cos());
        }
        return cosuri.get(c);
    }

    public void adaugaProdus(Client c, Produs p, int cant) {
        if (c == null ){
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        getCos(c).adaugaProdus(p, cant);
    }

    public void adaugaProdus(Client c, ProdusPersonalizat pp, int cant) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        getCos(c).adaugaProdus(pp, cant);
    }

    public void stergeProdus(Client c, ProdusPersonalizat pp) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        getCos(c).stergeProdus(pp);
    }

    public void golesteCos(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        getCos(c).goleste();;
    }

    public boolean esteGol(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        return getCos(c).esteGol();
    }

    public double calculeazaSubtotal(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        return getCos(c).calculeazaSubtotal();
    }

    public boolean areCos(Client c) {
        if (c == null) {
            return false;
        }
        return cosuri.containsKey(c);
    }

    public void stergeCos(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        cosuri.remove(c);
    }

    public Map<ProdusPersonalizat, Integer> getProduseCos(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        return getCos(c).getProduse();
    }
}
