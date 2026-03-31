package com.pao.laboratory06.exercise3;

public class Inginer extends Angajat implements PlataOnline, Comparable<Inginer> {
    private double sold;
    public Inginer(String nume, String prenume, String telefon, double salariu, double sold) {
        super(nume,prenume,telefon,salariu);
        this.sold=sold;
    }
    @Override
    public double consultareSold() {
        return sold;
    }
    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.equals("") || parola == null || parola.equals("")) {
            throw new IllegalArgumentException("Autentificarea a fost efectuata cu campurile de nume sau parola goale!");
        }
        System.out.println("Inginerul cu user-ul " + user + " a fost autentificat!");
    }
    @Override
    public boolean efectuarePlata(double suma) {
        if (sold < suma) {
            return false;
        }
        sold -= suma;
        return true;
    }
    @Override
    public int compareTo(Inginer o) {
        return getNume().compareTo(o.getNume());
    }
    @Override
    public String toString() {
        return "Inginer {nume='" + getNume() + "', prenume='" + getPrenume() + "', telefon='" + getTelefon() + "', salariu='" + getSalariu() + "'}";
    }
}
