package com.pao.laboratory06.exercise3;

import java.util.ArrayList;

public class PersoanaJuridica extends Persoana implements PlataOnlineSMS {
    ArrayList<String> smsTrimise;
    private double sold;
    public PersoanaJuridica(String nume, String prenume, String telefon) {
        super(nume, prenume, telefon);
        smsTrimise = new ArrayList<String>();
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
        System.out.println("Persoana juridica cu user-ul " + user + " a fost autentificat!");
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
    public boolean trimiteSMS(String mesaj) {
        String telefon = getTelefon();
        if (telefon == null || telefon.equals("")) {
            return false;
        }
        smsTrimise.add(mesaj);
        return true;
    }

    public ArrayList<String> getSmsTrimise() {
        return smsTrimise;
    }
}
