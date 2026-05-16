package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.repository.ComandaRepository;
import com.pao.project.fooddelivery.repository.SoferRepository;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComandaService {
//    private List<Comanda> comenzi;

    private ComandaRepository comandaRepository;
    private SoferRepository soferRepository;

    private ComandaService() {
//        this.comenzi = new ArrayList<Comanda>();
        this.comandaRepository = new ComandaRepository();
        this.soferRepository = new SoferRepository();
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
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            proceseazaPlata(c, total, mp, cardAles);
            Comanda cmd = new Comanda(c, r, adrLiv, prodCos, mp);
//        comenzi.add(cmd);
            comandaRepository.save(cmd);
            cs.golesteCos(c);

            conn.commit();
            return cmd;
        }
        catch (Exception e) {
            try {
                conn.rollback();
            }
            catch (SQLException se) {
                throw new RuntimeException("Eroare la rollback pentru checkout: ", se);
            }
            throw new RuntimeException("Eroare la checkout: ", e);
        }
        finally {
            try {
                conn.setAutoCommit(true);
            }
            catch (SQLException se) {
                throw new RuntimeException("Eroare la resetarea autocommitului dupa checkout: ", se);
            }
        }
    }

    public List<Comanda> getComenzi() {
//        return List.copyOf(comenzi);
        return comandaRepository.findAll();
    }

    // astea returneaza detalii facute prin join
    public List<String> getDetaliiIstoricComenziClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        return comandaRepository.getDetaliiIstoricComenziClient(c.getId());
    }
    public List<String> getDetaliiComenziDisponibile() {
        return comandaRepository.getDetaliiComenziDisponibile();
    }
    //---------------------------------------------------------------------

    public List<Comanda> getIstoricClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("CLientul nu poate fi vid!");
        }

        List<Comanda> ist = new ArrayList<Comanda>();
//        for (Comanda cmd : comenzi) {
//            if (cmd.getClient().equals(c) && cmd.getStatus() == StatusComanda.FINALIZATA) {
//                ist.add(cmd);
//            }
//        }
        for (Comanda cmd : comandaRepository.findAll()) {
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
//        for (Comanda cmd : comenzi) {
//            if (cmd.getClient().equals(c) && cmd.getStatus() != StatusComanda.FINALIZATA) {
//                cmdActive.add(cmd);
//            }
//        }
        for (Comanda cmd : comandaRepository.findAll()) {
            if (cmd.getClient().equals(c) && cmd.getStatus() != StatusComanda.FINALIZATA) {
                cmdActive.add(cmd);
            }
        }
        return cmdActive;
    }

    public List<Comanda> getComenziDisponibilePtLivrare() {
        List<Comanda> disp = new ArrayList<Comanda>();
//        for (Comanda c : comenzi) {
//            if (c.getStatus() == StatusComanda.NEPRELUATA && !c.areSoferAsignat()) {
//                disp.add(c);
//            }
//        }
        for (Comanda c : comandaRepository.findAll()) {
            if (c.getStatus() == StatusComanda.NEPRELUATA && !c.areSoferAsignat()) {
                disp.add(c);
            }
        }

        return disp;
    }

    public Comanda cautaDupaID(int id) {
//        for (Comanda c : comenzi) {
//            if (c.getId() == id) {
//                return c;
//            }
//        }
        Optional<Comanda> cmdGasita = comandaRepository.findById(id);
        if (cmdGasita.isEmpty()) {
            throw new IllegalArgumentException("Nu exista nici o comanda cu id " + id);
        }
        return cmdGasita.get();
//        throw new IllegalArgumentException("Nu exista nici o comanda cu id " + id);
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

        // e nevoie sa facem update-urile intr-o singura tranzactie, ca operatiile sa fie rolled back
        // in caz ca intervine o eroare. de asta facem chestia asta complicata
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            c.asigneazaSofer(s); // din moment ce asta modifica si soferul, trebuie update si la sofer
            // in repository
            c.avanseazaStatus();;
            comandaRepository.update(c);
            soferRepository.update(s);
            conn.commit();
        }
        catch (Exception e) {
            try {
                conn.rollback();
            }
            catch (SQLException se) {
                throw new RuntimeException("Eroare la rollback pentru preluarea comenzii: ", se);
            }

            throw new RuntimeException("Eroare la preluarea comenzii: ", e);
        }
        finally {
            // folosim finally ca sa se intample chestiile astea indiferent ca a fost o exceptie sau nu
            // inainte sa fie unwound call stackul
            try {
                conn.setAutoCommit(true);
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la resetarea auto commitului dupa preluarea comenzii: ", e);
            }
        }
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

        Connection conn = DatabaseConnection.getInstance().getConnection();;

        try {
            conn.setAutoCommit(false);
            s.adaugaComision(comision);
            s.setDisponibil(true);
            c.avanseazaStatus();

            comandaRepository.update(c);
            soferRepository.update(s);
            conn.commit();
        }
        catch (Exception e) {
            try {
                conn.rollback();
            }
            catch (SQLException se) {
                throw new RuntimeException("Eroare la rollback pt finalizarea comenzii: ", se);
            }
            throw new RuntimeException("Eroare la finalizarea comenzii: ", e);
        }
        finally {
            try {
                conn.setAutoCommit(true);
            }
            catch (SQLException se) {
                throw new RuntimeException("Eroare la resetarea autocommitului dupa finalizarea comenzii: ", se);
            }
        }
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
