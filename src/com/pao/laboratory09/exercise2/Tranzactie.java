package com.pao.laboratory09.exercise2;

import java.io.Serial;
import java.io.Serializable;

public class Tranzactie implements Serializable {
    private int id;
    private double suma;
    private String data;
    private TipTranzactie tip;
    private Status status;
    transient String note;

    public Tranzactie(int id, double suma, String data, TipTranzactie tip, Status status) {
        if (!data.matches("^\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Data trebuie sa fie formatata yyyy-mm-dd!");
        }
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.tip = tip;
        this.status = status;
        this.note = null;
    }

    public int getId() {
        return id;
    }

    public double getSuma() {
        return suma;
    }

    public String getData() {
        return data;
    }

    public TipTranzactie getTip() {
        return tip;
    }

    public Status getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    void setNote(String note) {
        this.note = note;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "[" + id + "] " + "data=<" + data + "> tip=<" + tip + "> suma=<" + suma + "> RON status=<" + status + ">";
        // [idx] id=<id> data=<data> tip=<CREDIT|DEBIT> suma=<suma:.2f> RON status=<PENDING|PROCESSED|REJECTED>
    }
}
