package com.pao.laboratory11.exercise3;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // TODO: Manual demo for bonus requirements.
        System.out.println("TODO: implement laboratory11 exercise3 bonus demo");
        List<Transaction> data = new ArrayList<Transaction>();
        data.add(new Transaction(0, BigDecimal.ONE, LocalDate.of(2026, 6, 12), "Romania", "A"));
        data.add(new Transaction(1, BigDecimal.TWO, LocalDate.of(2026, 6, 5), "Spania", "A"));
        data.add(new Transaction(2, BigDecimal.ONE, LocalDate.of(2026, 7, 26), "Afghanistan", "C"));
        data.add(new Transaction(3, BigDecimal.TWO, LocalDate.of(2026, 7, 30), "Norvegia", "B"));
        data.add(new Transaction(4, BigDecimal.TWO, LocalDate.of(2026, 8, 11), "Norvegia", "B"));
        Snapshot snap = data.stream().collect(CustomCollectors.toSnapshot(5));

        // Interogare 1: top tranzacții (din snapshot)
        List<Transaction> transactions = snap.getTopTransactions();
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        // snap.getTopTransactions().forEach(System.out::println);

        // Interogare 2: total pe țări (desc)
        snap.getCountByCountry().entrySet().stream().sorted((item1, item2) -> item2.getValue().compareTo(item1.getValue())).forEach(System.out::println) /* sort & print */;

        // Interogare 3: canale ordonate după număr
        snap.getCountByChannel().entrySet().stream().sorted((item1, item2) -> item1.getKey().compareTo(item2.getKey())).forEach(System.out::println) /* sort & print */;
    }
}
