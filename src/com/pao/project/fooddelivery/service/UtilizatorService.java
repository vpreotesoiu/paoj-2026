package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.exception.ParolaInvalidaException;
import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.repository.ClientRepository;
import com.pao.project.fooddelivery.repository.SoferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorService {
//    private List<Client> clienti;
//    private List<Sofer> soferi;
    private ClientRepository clientRepository;
    private SoferRepository soferRepository;

    private UtilizatorService() {
//        this.clienti = new ArrayList<Client>();
//        this.soferi = new ArrayList<Sofer>();
        this.clientRepository = new ClientRepository();
        this.soferRepository = new SoferRepository();
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
//        clienti.add(c);
        clientRepository.save(c);
    }

    public void adaugaSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        if (existaEmail(s.getEmail())) {
            throw new IllegalArgumentException("Exista deja un utilizator cu email-ul " + s.getEmail() + "!");
        }
//        soferi.add(s);
        soferRepository.save(s);
    }

    public void stergeClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
//        if (!clienti.remove(c)) {
//            throw new IllegalArgumentException("Clientul nu exista in sistem!");
//        }
        Optional<Client> clientInSistem = clientRepository.findById(c.getId());
        if (clientInSistem.isEmpty()) {
            throw new IllegalArgumentException("Clientul nu exista in sistem!");
        }
        clientRepository.delete(c.getId());
    }

    public void stergeSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
//        if (!soferi.remove(s)) {
//            throw new IllegalArgumentException("Soferul nu exista in sistem!");
//        }
        Optional<Sofer> soferInSistem = soferRepository.findById(s.getId());
        if (soferInSistem.isEmpty()) {
            throw new IllegalArgumentException("Soferul nu exista in sistem!");
        }

        soferRepository.delete(s.getId());
    }

    public List<Client> getClienti() {
//        return List.copyOf(clienti);
        return clientRepository.findAll();
    }

    public List<Sofer> getSoferi() {
//        return List.copyOf(soferi);
        return soferRepository.findAll();
    }

    public List<Utilizator> getUtilizatori() {
        List<Utilizator> utilizatori = new ArrayList<Utilizator>();
        List<Client> clienti = clientRepository.findAll();
        List<Sofer> soferi = soferRepository.findAll();
        utilizatori.addAll(clienti);
        utilizatori.addAll(soferi);
        return List.copyOf(utilizatori);
    }

    public Client cautaClientDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul clientului nu poate fi vid!");
        }
        String emailCautat = email.trim();
//        for (Client c : clienti) {
//            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
//                return c;
//            }
//        }
        Optional<Client> clientCautat = clientRepository.findByEmail(emailCautat);
        if (clientCautat.isPresent()) {
            return clientCautat.get();
        }
        throw new IllegalArgumentException("Nu exista niciun client cu email-ul " + emailCautat + "!");
    }

    public Client autentificaClient(String email, String parola) {
        if (parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("Parola clientului nu poate sa fie vida!");
        }

        Client c = cautaClientDupaEmail(email);
        String parolaClient = c.getParola();
        String parolaCuratat = parola.trim();
        if (!parolaClient.equals(parolaCuratat)) {
            throw new ParolaInvalidaException("Parola clientului este incorecta!");
        }

        return c;
    }

    public Sofer cautaSoferDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul soferului nu pote fi vid!");
        }
        String emailCautat = email.trim();
//        for (Sofer s : soferi) {
//            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
//                return s;
//            }
//        }
        Optional<Sofer> soferCautat = soferRepository.findByEmail(emailCautat);
        if (soferCautat.isPresent()) {
            return soferCautat.get();
        }
        throw new IllegalArgumentException("Nu exista niciun sofer cu email-ul " + emailCautat + "!");
    }

    public Sofer autentificaSofer(String email, String parola) {
        if (parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("Parola soferului nu poate fi vida!");
        }

        Sofer s = cautaSoferDupaEmail(email);
        String parolaSofer = s.getParola();
        String parolaCuratat = parola.trim();
        if (!parolaSofer.equals(parolaCuratat)) {
            throw new ParolaInvalidaException("Parola soferului este incorecta!");
        }

        return s;
    }

    public Utilizator cautaUtilizatorDupaEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul utilizatorului nu poate fi vid!");
        }
        String emailCautat = email.trim();
        Optional<Client> clientCautat = clientRepository.findByEmail(emailCautat);
        if (clientCautat.isPresent()) {
            return clientCautat.get();
        }
        Optional<Sofer> soferCautat = soferRepository.findByEmail(emailCautat);
        if (soferCautat.isPresent()) {
            return soferCautat.get();
        }
//        for (Client c : clienti) {
//            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
//                return c;
//            }
//        }
//        for (Sofer s : soferi) {
//            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
//                return s;
//            }
//        }
        throw new IllegalArgumentException("Nu exista niciun utilizator cu email-ul " + emailCautat + "!");
    }

    public boolean existaEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String emailCautat = email.trim();
//        for (Client c : clienti) {
//            if (c.getEmail().equalsIgnoreCase(emailCautat)) {
//                return true;
//            }
//        }
//        for (Sofer s : soferi) {
//            if (s.getEmail().equalsIgnoreCase(emailCautat)) {
//                return true;
//            }
//        }
//        return false;
        return clientRepository.existsEmail(emailCautat) || soferRepository.existsEmail(emailCautat); // mama cat de clean mi-a iest asta
    }

    public ClientPremium promoveazaLaPremium(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }
//        if (!clienti.contains(c)) {
//            throw new IllegalArgumentException("clientul nu exista in sistem!");
//        }
        Optional<Client> clientInSistem = clientRepository.findById(c.getId());
        if (clientInSistem.isEmpty()) {
            throw new IllegalArgumentException("clientul nu exista in sistem!");
        }
        Client clientDePromovat = clientInSistem.get();
        if (clientDePromovat instanceof ClientPremium cp) {
            return cp;
        }

        int id = clientDePromovat.getId();
        String nume = clientDePromovat.getNume();
        String email = clientDePromovat.getEmail();
        String parola = clientDePromovat.getParola();

//        ClientPremium cp = new ClientPremium(c.getNume(), c.getEmail(), c.getParola());
        ClientPremium cp = new ClientPremium(id, nume, email ,parola);
        for (Adresa adr: clientDePromovat.getAdrese()) {
            cp.adaugaAdresa(adr);
        }
        for (CardBancar cb : clientDePromovat.getCarduri()) {
            cp.adaugaCard(cb);
        }
        for (Restaurant r : clientDePromovat.getRestauranteFavorite()) {
            cp.adaugaRestaurantFavorit(r);
        }
//        clienti.remove(c);
//        clienti.add(cp);

        clientRepository.update(cp);

        return cp;
    }

    public Client expiraAbonamentPremium(ClientPremium cp) {
        if (cp == null) {
            throw new IllegalArgumentException("Clientul premium nu poate fi vid!");
        }

//        if (!clienti.contains(cp)) {
//            throw new IllegalArgumentException("Clientul premium nu exista in sistem!");
//        }
        Optional<Client> clientInSistem = clientRepository.findById(cp.getId());
        if (clientInSistem.isEmpty()) {
            throw new IllegalArgumentException("Clientul premium nu exista in sistem!");
        }
        if (!(clientInSistem.get() instanceof ClientPremium)) {
            return clientInSistem.get(); // nu mai e nimic de facut
        }

        int id = clientInSistem.get().getId();
        String nume = clientInSistem.get().getNume();
        String email = clientInSistem.get().getEmail();
        String parola = clientInSistem.get().getParola();
//        Client c = new Client(cp.getNume(), cp.getEmail(), cp.getParola());
        Client c = new Client(id, nume, email, parola);

        for (Adresa adr : clientInSistem.get().getAdrese()) {
            c.adaugaAdresa(adr);
        }
        for (CardBancar cb : clientInSistem.get().getCarduri()) {
            c.adaugaCard(cb);
        }
        for (Restaurant r : clientInSistem.get().getRestauranteFavorite()) {
            c.adaugaRestaurantFavorit(r);
        }
//        clienti.remove(cp);
//        clienti.add(c);
        clientRepository.update(c);

        return c;
    }

    // helper pt main.java
    public void actualizeazaClient(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("Clientul nu poate fi vid!");
        }

        Optional<Client> clientInSistem = clientRepository.findById(c.getId());
        if (clientInSistem.isEmpty()) {
            throw new IllegalArgumentException("Clientul nu exista in sistem!");
        }

        clientRepository.update(c);
    }
}
