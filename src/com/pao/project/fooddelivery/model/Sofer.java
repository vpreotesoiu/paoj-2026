package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.ComisionInvalidException;

import java.util.Objects;

public class Sofer extends Utilizator {
    private int id;
    private double balanta;
    private boolean disponibil;

    public Sofer(String nume, String email, String parola) {
        super(nume, email, parola);
        this.id = 0;
        this.balanta = 0;
        this.disponibil = true;
    }

    public Sofer(int id, String nume, String email, String parola, double balanta, boolean disponibil) {
        super(nume, email, parola);
        this.id = id;
        this.balanta = balanta;
        this.disponibil = disponibil;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalanta() {
        return balanta;
    }

    public boolean isDisponibil() {
        return disponibil;
    }

    public void adaugaComision(double comision) {
        if (comision <= 0) {
            throw new ComisionInvalidException(comision);
        }
        this.balanta += comision;
    }

    public void setDisponibil(boolean disponibil) {
        this.disponibil = disponibil;
    }

    @Override
    public String getRol() {
        return "Sofer";
    }

    @Override
    public String toString() {
        return "Sofer{nume='" + getNume() + "', email='" + getEmail() + "', balanta=" + balanta + ", disponibil=" +
                (disponibil ? "da" : "nu") + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Sofer s = (Sofer) o;
        return getEmail().equals(s.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

}
