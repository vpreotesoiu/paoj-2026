package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComandaService {
    private List<Comanda> comenzi;

    private ComandaService() {
        this.comenzi = new ArrayList<Comanda>();
    }

    private static class Holder {
        private static final ComandaService INSTANCE = new ComandaService();
    }

    public static ComandaService getInstance() {
        return Holder.INSTANCE;
    }

    public Comanda checkout(Client c, Restaurant r, Adresa adrLiv, MetodaPlata mp, CardBancar cardAles) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }
        if (adrLiv == null) {
            throw new IllegalArgumentException("Adresa de livrare nu poate fi vida!");
        }
        if (mp == null) {
            throw new IllegalArgumentException("Metoda de plata nu poate fi vida!");
        }
        CosService cs = CosService.getInstance();
        if (cs.esteGol(c)) {
            throw new IllegalArgumentException("Cosul clientului este gol!");
        }

        Map<ProdusPersonalizat, Integer> prodCos = cs.getProduseCos(c);
        // valideaza daca produsele din cos apartin restaurantului r
        List<Produs> prodRest = r.getProduse();
        for (ProdusPersonalizat pp : prodCos.keySet()) {
            if (!prodRest.contains(pp.getProdus())) {
                throw new IllegalArgumentException("Produsul " + pp.getProdus().getNume() +
                        " nu apartine restaurantului " + r.getNume() + "!");
            }
        }


        double total = calculeazaTotal(c, r, prodCos, mp);
        proceseazaPlata(c, total, mp, cardAles);
        Comanda cmd = new Comanda(c, r, adrLiv, prodCos, mp);
        comenzi.add(cmd);
        cs.golesteCos(c);

        return cmd;
    }

    public List<Comanda> getComenzi() {
        return List.copyOf(comenzi);
    }
    public List<Comanda> getIstoricClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("CLientul nu poate fi vid!");
        }

        List<Comanda> ist = new ArrayList<Comanda>();
        for (Comanda cmd : comenzi) {
            if (cmd.getClient().equals(c) && cmd.getStatus() == StatusComanda.FINALIZATA) {
                ist.add(cmd);
            }
        }
        return ist;
    }

    public List<Comanda> getComenziActiveClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        List<Comanda> cmdActive = new ArrayList<Comanda>();
        for (Comanda cmd : comenzi) {
            if (cmd.getClient().equals(c) && cmd.getStatus() != StatusComanda.FINALIZATA) {
                cmdActive.add(cmd);
            }
        }
        return cmdActive;
    }

    public List<Comanda> getComenziDisponibilePtLivrare() {
        List<Comanda> disp = new ArrayList<Comanda>();
        for (Comanda c : comenzi) {
            if (c.getStatus() == StatusComanda.NEPRELUATA && !c.areSoferAsignat()) {
                disp.add(c);
            }
        }

        return disp;
    }

    public Comanda cautaDupaID(int id) {
        for (Comanda c : comenzi) {
            if (c.getId() == id) {
                return c;
            }
        }
        throw new IllegalArgumentException("Nu exista nici o comanda cu id " + id);
    }

    public void preiaComanda(int idCmd, Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        Comanda c = cautaDupaID(idCmd);

        if (c.getStatus() != StatusComanda.NEPRELUATA) {
            throw new IllegalStateException("Comanda nu mai este disponibila pt preluare in sistem!");
        }
        if (!s.isDisponibil()) {
            throw new IllegalStateException("Soferul nu este disponibil acum!");
        }

        c.asigneazaSofer(s);
        c.avanseazaStatus();;
    }

    private double calculeazaSubtotalProduse(Map<ProdusPersonalizat, Integer> produse) {
        double subtotal = 0;
        for (Map.Entry<ProdusPersonalizat, Integer> elem: produse.entrySet()) {
            ProdusPersonalizat pp = elem.getKey();
            Integer cant = elem.getValue();
            subtotal += pp.calculeazaPretTotal() * cant;
        }
        return subtotal;
    }

    public void finalizeazaComanda(int idCmd) {
        Comanda c = cautaDupaID(idCmd);
        if (c.getStatus() != StatusComanda.IN_LIVRARE) {
            throw new IllegalStateException("Doar comenzile care sunt in livrare pot fi finalizate!");
        }
        if (!c.areSoferAsignat()) {
            throw new IllegalStateException("Comanda nu are sofer asignat!");
        }

        Restaurant restCmd = c.getRestaurant();
        Sofer s = c.getSoferAsignat();
        double subtotal = calculeazaSubtotalProduse(c.getProduse());
        double comision = subtotal * restCmd.getProcentComisionSofer();

        s.adaugaComision(comision);
        s.setDisponibil(true);
        c.avanseazaStatus();
    }

    public double calculeazaSubtotal(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        return CosService.getInstance().calculeazaSubtotal(c);
    }

    public double calculeazaTaxaLivrare(Client c, Restaurant r) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }

        if (c.areLivrareGratuita(r)) {
            return 0;
        }
        return r.getTaxaLivrare();
    }
    public double calculeazaTotal(Client c, Restaurant r, Map<ProdusPersonalizat, Integer> produse,
                                  MetodaPlata mp) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }
        if (produse == null || produse.isEmpty()) {
            throw new IllegalArgumentException("Lista de produse nu poate fi vida!");
        }
        if (mp == null) {
            throw new IllegalArgumentException("Metoda de plata nu poate fi vida!");
        }

        double subtot = calculeazaSubtotalProduse(produse);
        double taxaLiv = calculeazaTaxaLivrare(c, r);
        double baniPlata = subtot + taxaLiv;

        IPlata p;
        if (mp == MetodaPlata.CARD) {
            p = new PlataCard(baniPlata);
        }
        else {
            p = new PlataCash(baniPlata);
        }

        return p.calculeazaTotal();
    }

    public double calculeazaCheckout(Client c, Restaurant r, MetodaPlata mp) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }
        if (mp == null) {
            throw new IllegalArgumentException("Metoda de plata nu poate fi vida!");
        }

        Map<ProdusPersonalizat, Integer> prodCos = CosService.getInstance().getProduseCos(c);
        if (prodCos.isEmpty()) {
            throw new IllegalStateException("Cosul clientului este gol, nu se poate face checkout!");
        }
        // valideaza daca produsele din cos apartin restaurantului r
        List<Produs> prodRest = r.getProduse();
        for (ProdusPersonalizat pp : prodCos.keySet()) {
            if (!prodRest.contains(pp.getProdus())) {
                throw new IllegalArgumentException("Produsul " + pp.getProdus().getNume() +
                        " nu apartine restaurantului " + r.getNume() + "!");
            }
        }

        return calculeazaTotal(c, r, prodCos, mp);
    }

    public void proceseazaPlata(Client c, double total, MetodaPlata mp, CardBancar cb) {
        if (c == null) {
            throw new IllegalArgumentException("Clentul nu poate fi vid!");
        }
        if (total <= 0) {
            throw new IllegalArgumentException("Totalul de plata trebuie as fie pozitiv!");
        }
        if (mp == null) {
            throw new IllegalArgumentException("Metoda de plata nu poate fi vida!");
        }
        if (mp == MetodaPlata.CASH) {
            return;
        }

        if (cb == null) {
            throw new IllegalArgumentException("Pentru plata cu cardul trebuie sa fie ales un card!");
        }
        if (!c.getCarduri().contains(cb)) {
            throw new IllegalArgumentException("Cardul ales pentru checkout nu apartine clientului!");
        }

        cb.debiteaza(total);
    }
}
