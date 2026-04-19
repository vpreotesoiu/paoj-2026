package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.ParolaInvalidaException;

public abstract class Utilizator {
    private static final int LUNGIME_MINIMA_PAROLA = 8;

    private String nume, email, parola;

    public Utilizator(String nume, String email, String parola) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele utilizatorului '" + nume + "' nu contine text!");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email-ul utilizatorului '" + email + "' nu are formatul asteptat!");
        }
        this.nume = nume;
        this.email = email;
        this.parola = valideazaParola(parola);
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public String getParola() {
        return parola;
    }

    private static String valideazaParola(String parola) {
        if (parola == null || parola.isBlank()) {
            throw new ParolaInvalidaException("Parola nu poate fi vida");
        }
        String parolaCurata = parola.trim();
        if (parolaCurata.length() < LUNGIME_MINIMA_PAROLA) {
            throw new ParolaInvalidaException("Parola trebuie sa aiba cel putin " + LUNGIME_MINIMA_PAROLA + " caractere");
        }
        return parolaCurata;
    }

    protected void setParola(String parolaNoua) {
        this.parola = valideazaParola(parolaNoua);
    }

    public abstract String getRol();
}
