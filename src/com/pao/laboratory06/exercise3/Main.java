package com.pao.laboratory06.exercise3;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Vezi Readme.md pentru cerințe
        System.out.println("Sortare ingineri");
        ArrayList<Inginer> ingineri = new ArrayList<Inginer>();
        ingineri.add(new Inginer("Popescu", "Ion", "0722", 4500, 10300));
        ingineri.add(new Inginer("Ionescu", "Ana", "0733", 7000, 21700));
        ingineri.add(new Inginer("Ardeleanu", "Dan", "0744", 5500, 15900));

        System.out.println("Inainte de sortare");
        for (Inginer i : ingineri) {
            System.out.println(i.toString());
        }

        ingineri.sort((Inginer i1, Inginer i2) -> i1.compareTo(i2));
        System.out.println("Sortare alfabetica dupa nume");
        for (Inginer i : ingineri) {
            System.out.println(i.toString());
        }

        ingineri.sort(new ComparatorInginerSalariu());
        System.out.println("Sortare descrescatoare dupa salariu");
        for (Inginer i : ingineri) {
            System.out.println(i.toString());
        }

        System.out.println("Test plata online");
        PlataOnline plataInginer = ingineri.get(0);
        plataInginer.autentificare("Gigel", "parola123");
        System.out.println("Sold inginer: " + plataInginer.consultareSold());

        System.out.println("Test persoana juridica cu sms");
        PersoanaJuridica firma = new PersoanaJuridica("MultiBani", "Afacere", "0729372629");
        PlataOnlineSMS platformaSMS = firma;

        if (platformaSMS.trimiteSMS("Plata dvs. de 120 de lei a fost confirmata!")) {
            System.out.println("SMS trimis cu succes catre ProduseFaineSRL");
        }

        System.out.println("Mesaje:");
        for (String s : firma.getSmsTrimise()) {
            System.out.println(s);
        }

        System.out.println("Edge cases");

        // autentificare cu null
        try {
            plataInginer.autentificare(null, "123");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare:" + e.getMessage());
        }

        // trimitere SMS fara numar de telefon
        PersoanaJuridica firmaFaraTel = new PersoanaJuridica("Florarie SRL", "Teapa", null);
        boolean smsAFostTrimis = firmaFaraTel.trimiteSMS("Ce mai faci? Alo?");
        if (smsAFostTrimis) {
            System.out.println("SMS-ul a fost trimis cumva catre o firma fara numar de telefon");
        } else {
            System.out.println("E bine, SMS-ul nu a fost trimis catre o firma fara numar de telefon");
        }
    }
}