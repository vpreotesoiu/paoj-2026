package com.pao.laboratory08.exercise1;

public class Adresa implements Cloneable {
    private String oras;
    private String strada;

    // constructor(String oras, String strada)
    // getteri, setteri
    // toString() → "Adresa{oras='...', strada='...'}"

    public Adresa(String oras, String strada) {
        this.oras = oras;
        this.strada = strada;
    }

    public String getOras() {
        return oras;
    }

    public String getStrada() {
        return strada;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public void setStrada(String strada) {
        this.strada = strada;
    }

    @Override
    public String toString() {
        return "Adresa{oras='" + oras + "', strada='" + strada + "'}";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}