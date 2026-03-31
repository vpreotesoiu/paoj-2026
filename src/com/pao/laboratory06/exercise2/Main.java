package com.pao.laboratory06.exercise2;

import java.util.*;
import java.util.stream.Stream;

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
                    colab = new CIMColaborator();
                }
                case "PFA" -> {
                    colab = new PFAColaborator();
                }
                case "SRL" -> {
                    colab = new SRLColaborator();
                }
                default -> {
                    throw new RuntimeException("Tipul de colaborator este invalid!");
                }
            }
            colab.citeste(scanner);
            colaboratori.add(colab);
        }
        for (int i = 0; i < n; ++i) {
            colaboratori.get(i).afiseaza();
            System.out.print("\n");
        }
        System.out.print("\n\n");
        colaboratori.sort((c1, c2) -> Double.compare(c2.calculeazaVenitAnual(), c1.calculeazaVenitAnual()));

        System.out.print("Colaborator cu venit net maxim: "); colaboratori.get(0).afiseaza();
        System.out.print("\n");

        Colaborator[] colabJur = colaboratori.stream().filter((c) -> c.getTipPersoana().equals(Persoana.Juridica)).toArray(Colaborator[]::new);
        System.out.println("Colaboratori persoane juridice:");
        for (Colaborator colab : colabJur) {
            colab.afiseaza();
            System.out.print("\n");
        }

        System.out.println("Sume si colaboratori pe tip");

        Map<String, Integer> persPeTip = new HashMap<String, Integer>();
        Map<String, Double> sumePeTip = new HashMap<String, Double>();

        for (Colaborator colab : colaboratori) {
            String tipContract = colab.tipContract();
            double venitColab = colab.calculeazaVenitAnual();
            if (!persPeTip.containsKey(tipContract)) {
                persPeTip.put(tipContract, 0);
            }
            if (!sumePeTip.containsKey(tipContract)) {
                sumePeTip.put(tipContract, (double)0);
            }
            persPeTip.put(tipContract, persPeTip.get(tipContract) + 1);
            sumePeTip.put(tipContract, sumePeTip.get(tipContract) + venitColab);
        }

        for (Map.Entry<String, Integer> pereche : persPeTip.entrySet()) {
            String tipContract = pereche.getKey();
            int nrPers = pereche.getValue();
            double suma = sumePeTip.get(tipContract);
            System.out.println(tipContract + ": suma = " + suma + ", numar=" + nrPers);
        }
    }
}
