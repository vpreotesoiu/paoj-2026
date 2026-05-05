package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    public static void main(String[] args) throws Exception {
        // TODO: Implementează conform Readme.md
        //
        // 1. Citește N din stdin, apoi cele N tranzacții (id suma data contSursa contDestinatie tip)
        // 2. Setează câmpul note = "procesat" pe fiecare tranzacție înainte de serializare
        // 3. Serializează lista de tranzacții în OUTPUT_FILE cu ObjectOutputStream (try-with-resources)
        // 4. Deserializează lista din OUTPUT_FILE cu ObjectInputStream (try-with-resources)
        // 5. Procesează comenzile din stdin până la EOF:
        //    - LIST          → afișează toate tranzacțiile, câte una pe linie
        //    - FILTER yyyy-MM → afișează tranzacțiile cu data care începe cu yyyy-MM
        //                       sau "Niciun rezultat." dacă nu există
        //    - NOTE id        → afișează "NOTE[id]: <valoarea câmpului note>"
        //                       sau "NOTE[id]: not found" dacă id-ul nu există
        //
        // Format linie tranzacție:
        //   [id] data tip: suma RON | contSursa -> contDestinatie
        //   Ex: [1] 2024-01-15 CREDIT: 1500.00 RON | RO01SRC1 -> RO01DST1

        List<Tranzactie> tranzactii = new ArrayList<Tranzactie>();

        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] split = line.split(" ");
            int id = Integer.parseInt(split[0]);
            double suma = Double.parseDouble(split[1]);
            String data = split[2];
            String contSursa = split[3];
            String contDestinatie = split[4];
            String tipStr = split[5];
            TipTranzactie tip;
            if (tipStr.equals("CREDIT")) {
                tip = TipTranzactie.CREDIT;
            } else {
                tip = TipTranzactie.DEBIT;
            }
            Tranzactie t = new Tranzactie(id, suma, data, contSursa, contDestinatie, tip);
            tranzactii.add(t);
        }

        for (Tranzactie t : tranzactii) {
            t.setNote("procesat");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("output/lab09_ex1.ser"))) {
            oos.writeObject(tranzactii);
        }

        List<Tranzactie> tranzactiiDeserialized;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("output/lab09_ex1.ser"))) {
            tranzactiiDeserialized = (List<Tranzactie>) ois.readObject();
        }

        while (true) {
            String comanda = scanner.nextLine();
            if (comanda.isBlank()) {
                break;
            }
            if (comanda.equals("LIST")) {
                for (Tranzactie t : tranzactiiDeserialized) {
                    System.out.println(t);
                }
            }
            else if (comanda.startsWith("FILTER")) {
                String[] split = comanda.split(" ");
                String data = split[1];
                for (Tranzactie t : tranzactii) {
                    if (t.getData().startsWith(data)) {
                        System.out.println(t);
                    }
                }
            }
            else if (comanda.startsWith("NOTE")) {
                String[] split = comanda.split(" ");
                int id = Integer.parseInt(split[1]);
                boolean gasit = false;
                for (Tranzactie t : tranzactiiDeserialized) {
                    if (t.getId() == id) {
                        System.out.println("NOTE[" + id + "]: " + t.getNote());
                        gasit = true;
                        break;
                    }
                }
                if (!gasit) {
                    System.out.println("NOTE[" + id + "]: not found");
                }
            }
        }

    }
}
