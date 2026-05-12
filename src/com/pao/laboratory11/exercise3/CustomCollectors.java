package com.pao.laboratory11.exercise3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

public final class CustomCollectors {
    private CustomCollectors() {}

    public static Collector<Transaction, ?, Snapshot> toSnapshot(int topN) {
        if (topN < 0) {
            throw new IllegalArgumentException("topN must be non-negative");
        }

        class Agg {
            // astea sunt luate din Snapshot
            Map<String, Long> countByCountry = new HashMap<String, Long>();
            Map<String, Long> countByChannel = new HashMap<String, Long>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<Transaction> topTransactions = new ArrayList<Transaction>();
        }
        return Collector.of(
                Agg::new,
                (agg, tx) -> {
                    String country = tx.getCountry();
                    if (agg.countByCountry.containsKey(country)) {
                        Long crt = agg.countByCountry.get(country);
                        agg.countByCountry.put(country, crt+1);
                    }
                    else {
                        agg.countByCountry.put(country, 1L);
                    }

                    String channel = tx.getChannel();
                    if (agg.countByChannel.containsKey(channel)) {
                        Long crt = agg.countByChannel.get(channel);
                        agg.countByChannel.put(channel, crt+1);
                    }
                    else {
                        agg.countByChannel.put(channel, 1L);
                    }

                    BigDecimal amount = tx.getAmount();
                    agg.totalAmount = agg.totalAmount.add(amount);
                    agg.topTransactions.add(tx);
                },
                (a,b) -> {
                    for (String country : b.countByCountry.keySet()) {
                        Long cntB = b.countByCountry.get(country);
                        if (a.countByCountry.containsKey(country)) {
                            Long cntA = a.countByCountry.get(country);
                            a.countByCountry.put(country, cntA+cntB);
                        }
                        else {
                            a.countByCountry.put(country, cntB);
                        }
                    }

                    for (String channel : b.countByChannel.keySet()) {
                        Long cntB = b.countByChannel.get(channel);
                        if (a.countByChannel.containsKey(channel)) {
                            Long cntA = a.countByChannel.get(channel);
                            a.countByChannel.put(channel, cntA+cntB);
                        }
                        else {
                            a.countByChannel.put(channel, cntB);
                        }
                    }

                    a.totalAmount = a.totalAmount.add(b.totalAmount);
                    for (Transaction t : b.topTransactions) {
                        a.topTransactions.add(t);
                    }
                    return a;
                },
                agg -> { return new Snapshot(agg.countByCountry, agg.countByChannel, agg.totalAmount, agg.topTransactions); }
        );
    }
}
