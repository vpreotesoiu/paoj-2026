package com.pao.laboratory05.angajati;

import com.pao.laboratory05.biblioteca.BibliotecaService;
import com.pao.laboratory05.biblioteca.Carte;

import java.util.Arrays;

public class AngajatService {
    private Angajat[] angajati;
    private AngajatService() {
        this.angajati = new Angajat[0];
    }
    private static class Holder {
        private static final AngajatService INSTANCE = new AngajatService();
    }
    public static AngajatService getInstance() {
        return AngajatService.Holder.INSTANCE;
    }
    public void addAngajat(Angajat a) {
        Angajat[] tmp = new Angajat[angajati.length + 1];
        System.arraycopy(angajati, 0, tmp, 0, angajati.length);
        tmp[angajati.length] = a;
        this.angajati = tmp;
        System.out.println("Angajat adaugat: " + a.getNume());
    }
    void printAll() {
        for (int i = 1; i <= angajati.length; ++i)
        {
            System.out.println(i + ". " + angajati[i-1]);
        }
    }
    void listBySalary() {
        Angajat[] tmp = new Angajat[angajati.length];
        System.arraycopy(angajati, 0, tmp, 0, angajati.length);
        Arrays.sort(tmp);
        for (int i = 1; i <= angajati.length; ++i) {
            System.out.println(i + ". " + tmp[i-1]);
        }
    }

    void findByDepartment(String numeDept) {
        boolean gasit = false;
        for (Angajat a : angajati) {
            if (a.getDepartament().nume().equalsIgnoreCase(numeDept)) {
                gasit = true;
                System.out.println(a);
            }
        }
        if (!gasit) {
            System.out.println("Niciun angajat in departamentul <" + numeDept + ">");
        }
    }
}
