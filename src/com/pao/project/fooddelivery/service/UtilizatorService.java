package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.*;

import java.util.ArrayList;
import java.util.List;

public class UtilizatorService {
    private List<Client> clienti;
    private List<Sofer> soferi;

    private UtilizatorService() {
        this.clienti = new ArrayList<Client>();
        this.soferi = new ArrayList<Sofer>();
    }

    private static class Holder {
        private static final UtilizatorService INSTANCE = new UtilizatorService();
    }

    public static UtilizatorService getInstance() {
        return Holder.INSTANCE;
    }

    public void adaugaClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (existaEmail(c.getEmail())) {
            throw new IllegalArgumentException("Exista deja un utilizator cu email-ul " + c.getEmail() + "!");
        }
        clienti.add(c);
    }

    public void adaugaSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        if (existaEmail(s.getEmail())) {
            throw new IllegalArgumentException("Exista deja un utilizator cu email-ul " + s.getEmail() + "!");
        }
        soferi.add(s);
    }

    public void stergeClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (!clienti.remove(c)) {
            throw new IllegalArgumentException("Clientul nu exista in sistem!");
        }
    }

    public void stergeSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        if (!soferi.remove(s)) {
            throw new IllegalArgumentException("Soferul nu exista in sistem!");
        }
    }

    public List<Client> getClienti() {
        return List.copyOf(clienti);
    }

    public List<Sofer> getSoferi() {
        return List.copyOf(soferi);
    }

    public List<Utilizator> getUtilizatori() {
        List<Utilizator> utilizatori = new ArrayList<Utilizator>();
        utilizatori.addAll(clienti);
        utilizatori.addAll(soferi);
        return List.copyOf(utilizatori);
    }

    public Client cautaClientDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul clientului nu poate fi vid!");
        }
        String emailCautat = email.trim();
        for (Client c : clienti) {
            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Nu exista niciun client cu email-ul " + emailCautat + "!");
    }

    public Sofer cautaSoferDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul soferului nu pote fi vid!");
        }
        String emailCautat = email.trim();
        for (Sofer s : soferi) {
            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
                return s;
            }
        }

        throw new IllegalArgumentException("Nu exista niciun sofer cu email-ul " + emailCautat + "!");
    }

    public Utilizator cautaUtilizatorDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul utilizatorului nu poate fi vid!");
        }
        String emailCautat = email.trim();
        for (Client c : clienti) {
            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
                return c;
            }
        }
        for (Sofer s : soferi) {
            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Nu exista niciun utilizator cu email-ul " + emailCautat + "!");
    }

    public boolean existaEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String emailCautat = email.trim();
        for (Client c : clienti) {
            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
                return true;
            }
        }
        for (Sofer s : soferi) {
            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
                return true;
            }
        }
        return false;
    }

    public ClientPremium promoveazaLaPremium(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
        if (!clienti.contains(c)) {
            throw new IllegalArgumentException("clientul nu exista in sistem!");
        }
        if (c instanceof ClientPremium cp) {
            return cp;
        }

        ClientPremium cp = new ClientPremium(c.getNume(), c.getEmail(), c.getParola());
        for (Adresa adr: c.getAdrese()) {
            cp.adaugaAdresa(adr);
        }
        for (CardBancar cb : c.getCarduri()) {
            cp.adaugaCard(cb);
        }
        for (Restaurant r : c.getRestauranteFavorite()) {
            cp.adaugaRestaurantFavorit(r);
        }
        clienti.remove(c);
        clienti.add(cp);

        return cp;
    }

    public Client expiraAbonamentPremium(ClientPremium cp) {
        if (cp == null) {
            throw new IllegalArgumentException("Clientul premium nu poate fi vid!");
        }

        if (!clienti.contains(cp)) {
            throw new IllegalArgumentException("Clientul premium nu exista in sistem!");
        }

        Client c = new Client(cp.getNume(), cp.getEmail(), cp.getParola());

        for (Adresa adr : cp.getAdrese()) {
            c.adaugaAdresa(adr);
        }
        for (CardBancar cb : cp.getCarduri()) {
            c.adaugaCard(cb);
        }
        for (Restaurant r : cp.getRestauranteFavorite()) {
            c.adaugaRestaurantFavorit(r);
        }
        clienti.remove(cp);
        clienti.add(c);

        return c;
    }
}
