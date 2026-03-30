package com.pao.laboratory06.exercise2;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Vezi Readme.md pentru cerințe
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        ArrayList<Colaborator> colaboratori = new ArrayList<Colaborator>();
        for (int i = 0; i < n; ++i) {
            String tipColabStr = scanner.next();
            TipColaborator tipColaborator;
            Colaborator colab;
            switch (tipColabStr) {
                case "CIM" -> {
                    tipColaborator = TipColaborator.CIM;
                    colab = new CIMColaborator();
                }
                case "PFA" -> {
                    tipColaborator = TipColaborator.PFA;
                    colab = new PFAColaborator();
                }
                case "SRL" -> {
                    tipColaborator = TipColaborator.SRL;
                    colab = new SRLColaborator();
                }
                default -> {
                    throw new RuntimeException("Tipul de colaborator este invalid!");
                }
            }
            colab.citeste(scanner);
            colaboratori.add(colab);

        }
    }
}
