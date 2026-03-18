package com.pao.laboratory03.exercise.model;

public enum Subject {
    PAOJ("Programare orientata pe obiecte in Java", 5),
    BD("Baze de date", 4),
    SO("Sisteme de operare", 6),
    RC("Retele de calculatoare", 5);

    private String fullName;
    private int credits;

    private Subject(String fullName, int credits) {
	    this.fullName = fullName;
	    this.credits = credits;
    }
    public String getFullName() {
	    return fullName;
    }
    public int getCredits() {
	    return credits;
    }

    @Override
    public String toString() {
        return this.name() + " (" + fullName + ", " + credits + " credite)";
    }
}
