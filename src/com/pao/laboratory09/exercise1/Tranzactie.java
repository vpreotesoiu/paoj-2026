package com.pao.laboratory09.exercise1;

import java.io.Serial;
import java.io.Serializable;

public class Tranzactie implements Serializable {
    private int id;
    private double suma;
    private String data;
    private String contSursa;
    private String contDestinatie;
    private TipTranzactie tip;
    transient String note;

    public Tranzactie(int id, double suma, String data, String contSursa, String contDestinatie, TipTranzactie tip) {
        if (!data.matches("^\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Data trebuie sa fie formatata yyyy-mm-dd!");
        }
        this.id = id;
        this.suma = suma;
        this.data = data;
        this.contSursa = contSursa;
        this.contDestinatie = contDestinatie;
        this.tip = tip;
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

    public String getContSursa() {
        return contSursa;
    }

    public String getContDestinatie() {
        return contDestinatie;
    }

    public TipTranzactie getTip() {
        return tip;
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
        return "[" + id + "] " + data + " " + tip + ": " + suma + " RON | " + contSursa + " -> " + contDestinatie;
    }
}
