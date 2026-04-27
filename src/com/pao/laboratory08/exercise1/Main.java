package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    // Calea către fișierul cu date — relativă la rădăcina proiectului
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        // TODO: Implementează conform Readme.md
        //
        // 1. Citește studenții din FILE_PATH cu BufferedReader
        // 2. Citește comanda din stdin: PRINT, SHALLOW <nume> sau DEEP <nume>
        // 3. Execută comanda:
        //    - PRINT → afișează toți studenții
        //    - SHALLOW <nume> → shallow clone + modifică orașul clonei la "MODIFICAT" + afișează
        //    - DEEP <nume> → deep clone + modifică orașul clonei la "MODIFICAT" + afișează

        ArrayList<Student> studenti = new ArrayList<Student>();

        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));

        String linie;
        while ((linie = br.readLine()) != null && linie.length() > 0) {
            String[] linieSplit = linie.split(",");
            studenti.add(new Student(linieSplit[0].trim(), Integer.parseInt(linieSplit[1].trim()), new Adresa(linieSplit[2], linieSplit[3])));
        }

        Scanner scanner = new Scanner(System.in);
        String comanda = scanner.nextLine();
        if (comanda.equals("PRINT")) {
            for (Student s : studenti) {
                System.out.println(studenti);
            }
        }
        else if (comanda.startsWith("SHALLOW")) {
            String nume = comanda.split(" ")[1];
            for (Student s : studenti) {
                if (s.getNume().equals(nume)) {
                    Student copie = (Student) s.clone();
                    copie.getAdresa().setOras("MODIFICAT");
                    System.out.println("original: " + s);
                    System.out.println("copie: " + copie);
                }
            }
        }
        else if (comanda.startsWith("DEEP")) {
            String nume = comanda.split(" ")[1];
            for (Student s : studenti) {
                if (s.getNume().equals(nume)) {
                    Student copie = (Student) s.clone();
                    copie.setAdresa((Adresa) s.getAdresa().clone());
                    copie.getAdresa().setOras("MODIFICAT");
                    System.out.println("original: " + s);
                    System.out.println("copie: " + copie);
                }
            }
        }

        br.close();
    }
}
