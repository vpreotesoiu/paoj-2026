package com.pao.project.fooddelivery.model;

public record Adresa(String strada, int numar, String oras, String codPostal) {
    public Adresa(String strada, int numar, String oras, String codPostal) {
        if (strada == null || strada.isBlank()) {
            throw new IllegalArgumentException("Strada asociata adresei nu poate fi goala!");
        }
        if (numar <= 0) {
            throw new IllegalArgumentException("Numarul strazii asociat adresei trebuie sa fie pozitiv!");
        }
        if (oras == null || oras.isBlank()) {
            throw new IllegalArgumentException("Orasul asociat adresei nu poate fi gol!");
        }
        if (codPostal == null || codPostal.isBlank()) {
            throw new IllegalArgumentException("Codul postal asociat adresei nu poate fi vid!");
        }
        String codPostalTrimmed = codPostal.trim();
        if (codPostalTrimmed.length() != 6) {
            throw new IllegalArgumentException("Codul postal trebuie sa contina 6 cifre!");
        }
        for (int i = 0; i < codPostalTrimmed.length(); ++i) {
            if (!Character.isDigit(codPostalTrimmed.charAt(i))) {
                throw new IllegalArgumentException("Codul postal trebuie sa contina doar cifre!");
            }
        }
        this.strada = strada.trim();
        this.numar = numar;
        this.oras = oras.trim();
        this.codPostal = codPostalTrimmed;
    }
    @Override
    public String toString() {
        return "Adresa{strada='" + strada + "', numar='" + numar + "', oras='" + oras + "', codPostal='" + codPostal + "'}";
    }
}
