package com.pao.project.fooddelivery.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditService {
    private static final String AUDIT_FILE = "audit.csv";

    private AuditService() {}

    private static class Holder {
        private static final AuditService INSTANCE = new AuditService();
    }

    public static AuditService getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void log(String actiune) {
        if (actiune == null || actiune.isBlank()) { // programare defensiva...
            throw new IllegalArgumentException("Actiunea de auditat nu poate fi vida!");
        }
        String actiuneDeAfis = actiune.trim();
        String ts = LocalDateTime.now().toString();

        try (FileWriter fw = new FileWriter(AUDIT_FILE, true)) {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(actiuneDeAfis + "," + ts);
        }
        catch (IOException ie) {
            throw new RuntimeException("Eroare la scrierea in fisierul csv de audit: ", ie);
        }
    }
}
