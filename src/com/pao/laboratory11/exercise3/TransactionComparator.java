package com.pao.laboratory11.exercise3;

import java.util.Comparator;

public class TransactionComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction t1, Transaction t2) {
        int compAmount = t2.getAmount().compareTo(t1.getAmount()); // dupa amount descrescator
        if (compAmount != 0) {
            return compAmount;
        }

        int compDate = t2.getDate().compareTo(t1.getDate());
        if (compDate != 0) {
            return compDate;
        }

        return Integer.compare(t1.getId(), t2.getId());
    }
}
