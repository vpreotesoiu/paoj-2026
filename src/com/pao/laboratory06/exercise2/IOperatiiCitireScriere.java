package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public interface IOperatiiCitireScriere {
    public void citeste(Scanner in);
    public void afiseaza();
    public String tipContract();
    default boolean areBonus() { return false; }
}
