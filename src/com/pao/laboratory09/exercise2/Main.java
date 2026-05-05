package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise2.TipTranzactie;
import com.pao.laboratory09.exercise2.Tranzactie;
import com.pao.laboratory09.exercise2.Status;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        // TODO: Implementează conform Readme.md
        //
        // 1. Citește N din stdin, apoi cele N tranzacții (id suma data tip)
        // 2. Scrie toate înregistrările în OUTPUT_FILE cu DataOutputStream (format binar, RECORD_SIZE=32 bytes/înreg.)
        //    - bytes 0-3:   id (int, little-endian via ByteBuffer)
        //    - bytes 4-11:  suma (double, little-endian via ByteBuffer)
        //    - bytes 12-21: data (String, 10 chars ASCII, paddat cu spații la dreapta)
        //    - byte 22:     tip (0=CREDIT, 1=DEBIT)
        //    - byte 23:     status (0=PENDING, 1=PROCESSED, 2=REJECTED)
        //    - bytes 24-31: padding (zerouri)
        // 3. Procesează comenzile din stdin până la EOF cu RandomAccessFile:
        //    - READ idx       → seek(idx * RECORD_SIZE), citește și afișează înregistrarea
        //    - UPDATE idx ST  → seek(idx * RECORD_SIZE + 23), scrie noul status (0/1/2)
        //                       afișează "Updated [idx]: STATUS"
        //    - PRINT_ALL      → citește și afișează toate înregistrările
        //
        // Format linie output:
        //   [idx] id=<id> data=<data> tip=<CREDIT|DEBIT> suma=<suma:.2f> RON status=<STATUS>

        List<Tranzactie> tranzactii = new ArrayList<Tranzactie>();

        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] split = line.split(" ");
            int id = Integer.parseInt(split[0]);
            double suma = Double.parseDouble(split[1]);
            String data = split[2];
            String tipStr = split[3];
            TipTranzactie tip;
            if (tipStr.equals("CREDIT")) {
                tip = TipTranzactie.CREDIT;
            } else {
                tip = TipTranzactie.DEBIT;
            }
            Tranzactie t = new Tranzactie(id, suma, data, tip, Status.PENDING);
            tranzactii.add(t);
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("output/lab09_ex2.bin"))) {
            for (Tranzactie t : tranzactii) {
                dos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(t.getId()).array());
                dos.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(t.getSuma()).array());
                String data = t.getData();
                byte[] bytes = new byte[10];
                byte[] dataBytes = data.getBytes();
                for (int i = 0; i < 10; ++i) {
                    if (i < data.length()) {
                        bytes[i] = dataBytes[i];
                    }
                    else {
                        bytes[i] = (byte) ' ';
                    }
                }
                dos.write(bytes);
                if (t.getTip() == TipTranzactie.CREDIT) {
                    dos.write(0);
                }
                else {
                    dos.write(1);
                }
                if (t.getStatus() == Status.PENDING) {
                    dos.write(0);
                }
                else if (t.getStatus() == Status.PROCESSED) {
                    dos.write(1);
                }
                else {
                    dos.write(2);
                }
                dos.write(new byte[8]);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile("output/lab09_ex2.bin", "rw")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    break;
                }

                String[] split = line.split(" ");

                String comanda = split[0];
                if (comanda.equals("READ")) {
                    int id = Integer.parseInt(split[1]);
                    raf.seek((long) id * 32); // fiecare intrare are cate 32 de bytes
                    byte[] byteID = new byte[4];
                    byte[] byteSuma = new byte[8];
                    byte[] byteData = new byte[10];
                    raf.readFully(byteID);
                    raf.readFully(byteSuma);
                    raf.readFully(byteData);
                    byte byteTip = raf.readByte();
                    byte byteStatus = raf.readByte();

                    raf.skipBytes(8); // de la padding

                    int readID = ByteBuffer.wrap(byteID).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    double readSuma = ByteBuffer.wrap(byteSuma).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                    String readData = new String(byteData).trim();
                    TipTranzactie readTip;
                    if (byteTip == 0) {
                        readTip = TipTranzactie.CREDIT;
                    }
                    else {
                        readTip = TipTranzactie.DEBIT;
                    }
                    Status readStatus;
                    if (byteStatus == 0) {
                        readStatus = Status.PENDING;
                    }
                    else if (byteStatus == 1) {
                        readStatus = Status.PROCESSED;
                    }
                    else {
                        readStatus = Status.REJECTED;
                    }
                    Tranzactie t = new Tranzactie(readID, readSuma, readData, readTip, readStatus);
                    System.out.println("[" + id + "] id=" + t.getId() +
                            " data=" + t.getData() +
                            " tip=" + t.getTip() +
                            " suma=" + String.format("%.2f", t.getSuma()) +
                            " RON status=" + t.getStatus());
                }
                else if (comanda.equals("UPDATE")) {
                    int id = Integer.parseInt(split[1]);
                    Status s;
                    if (split[2].equals("PENDING")) {
                        s = Status.PENDING;
                    }
                    else if (split[2].equals("PROCESSED")) {
                        s = Status.PROCESSED;
                    }
                    else {
                        s = Status.REJECTED;
                    }
                    raf.seek((long) id * 32 + 23); // status-ul va fi la byte-ul 32
                    if (s == Status.PENDING) {
                        raf.writeByte(0);
                    }
                    else if (s == Status.PROCESSED) {
                        raf.writeByte(1);
                    }
                    else {
                        raf.writeByte(2);
                    }
                    System.out.println("Updated [" + id + "]: " + s);
                }
                else {
                    for (int i = 0; i < n; ++i) {
                        raf.seek((long) i * 32); // citeste la indicele i
                        byte[] byteID = new byte[4];
                        byte[] byteSuma = new byte[8];
                        byte[] byteData = new byte[10];
                        raf.readFully(byteID);
                        raf.readFully(byteSuma);
                        raf.readFully(byteData);
                        byte byteTip = raf.readByte();
                        byte byteStatus = raf.readByte();

                        raf.skipBytes(8); // de la padding

                        int readID = ByteBuffer.wrap(byteID).order(ByteOrder.LITTLE_ENDIAN).getInt();
                        double readSuma = ByteBuffer.wrap(byteSuma).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                        String readData = new String(byteData).trim();
                        TipTranzactie readTip;
                        if (byteTip == 0) {
                            readTip = TipTranzactie.CREDIT;
                        }
                        else {
                            readTip = TipTranzactie.DEBIT;
                        }
                        Status readStatus;
                        if (byteStatus == 0) {
                            readStatus = Status.PENDING;
                        }
                        else if (byteStatus == 1) {
                            readStatus = Status.PROCESSED;
                        }
                        else {
                            readStatus = Status.REJECTED;
                        }
                        Tranzactie t = new Tranzactie(readID, readSuma, readData, readTip, readStatus);
                        System.out.println("[" + i + "] id=" + t.getId() +
                                " data=" + t.getData() +
                                " tip=" + t.getTip() +
                                " suma=" + String.format("%.2f", t.getSuma()) +
                                " RON status=" + t.getStatus());
                    }
                }
            }
        }
    }
}
