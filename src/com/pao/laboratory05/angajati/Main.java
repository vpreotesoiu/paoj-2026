package com.pao.laboratory05.angajati;

import java.util.Scanner;

/**
 * Exercise 3 — Angajați
 *
 * Cerințele complete se află în:
 *   src/com/pao/laboratory05/Readme.md  →  secțiunea "Exercise 3 — Angajați"
 *
 * Creează fișierele de la zero în acest pachet, apoi rulează Main.java
 * pentru a verifica output-ul așteptat din Readme.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Cerințele se află în Readme.md — secțiunea Exercise 3.");
        AngajatService as = AngajatService.getInstance();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Gestionare Angajați =====");
            System.out.println("1. Adaugă angajat");
            System.out.println("2. Listare după salariu");
            System.out.println("3. Caută după departament");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");
            // citește opțiunea și execută acțiunea
            int n = scanner.nextInt();
            scanner.nextLine();
            if (n == 1) {
                System.out.print("Nume: ");
                String nume = scanner.nextLine();
                System.out.print("Departament (nume): ");
                String numeDept = scanner.nextLine();
                System.out.print("Departament (locatie) " );
                String locDept = scanner.nextLine();
                System.out.print("Salariu: ");
                double salariu = scanner.nextDouble();
                scanner.nextLine();
                Departament d = new Departament(numeDept, locDept);
                as.addAngajat(new Angajat(nume, d, salariu));
            } else if (n == 2) {
                as.listBySalary();
            } else if (n == 3) {
                System.out.print("Departament: ");
                String numeDept = scanner.nextLine();
                as.findByDepartment(numeDept);
            } else if (n == 0) {
                System.out.println("La revedere!");
                return;
            }
        }
    }
}
