package com.pao.project.fooddelivery.model;

import java.util.Objects;

public final class Review {
    private int id;
    private int comandaId;
    private int soferId;
    private static final int RATING_MINIM = 1;
    private static final int RATING_MAXIM = 5;

    private final int rating;
    private final String comentariu;

    public Review(int comandaId, int soferId, int rating, String comentariu) {
        if (comandaId <= 0) {
            throw new IllegalArgumentException("Id-ul comenzii trebuie sa fie pozitiv!");
        }
        if (soferId <= 0) {
            throw new IllegalArgumentException("Id-ul soferului trebuie sa fie pozitiv!");
        }
        if (rating < RATING_MINIM || rating > RATING_MAXIM) {
            throw new IllegalArgumentException("Ratingul ar trebui sa fie intre " + RATING_MINIM + " si " + RATING_MAXIM + ".");
        }
        this.id = 0;
        this.comandaId = comandaId;
        this.soferId = soferId;
        this.rating = rating;
        if (comentariu == null) {
            this.comentariu = "";
        }
        else {
            this.comentariu = comentariu.trim();
        }
    }

    public Review(int id, int comandaId, int soferId, int rating, String comentariu) {
        if (comandaId <= 0) {
            throw new IllegalArgumentException("Id-ul comenzii trebuie sa fie pozitiv!");
        }
        if (soferId <= 0) {
            throw new IllegalArgumentException("Id-ul soferului trebuie sa fie pozitiv!");
        }
        if (rating < RATING_MINIM || rating > RATING_MAXIM) {
            throw new IllegalArgumentException("Ratingul ar trebui sa fie intre " + RATING_MINIM + " si " + RATING_MAXIM + ".");
        }
        this.id = id;
        this.comandaId = comandaId;
        this.soferId = soferId;
        this.rating = rating;
        if (comentariu == null) {
            this.comentariu = "";
        }
        else {
            this.comentariu = comentariu.trim();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComandaId() {
        return comandaId;
    }

    public int getSoferId() {
        return soferId;
    }

    public int getRating() {
        return rating;
    }

    public String getComentariu() {
        return comentariu;
    }

    @Override
    public String toString() {
        return "Review{rating=" + rating + ", comentariu='" + comentariu + "'}";
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
        Review r = (Review) o;
        return comandaId == r.comandaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, comentariu);
    }
}
