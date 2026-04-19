package com.pao.project.fooddelivery.model;

import java.util.Objects;

public final class Review {
    private static final int RATING_MINIM = 1;
    private static final int RATING_MAXIM = 5;

    private final int rating;
    private final String comentariu;

    public Review(int rating, String comentariu) {
        if (rating < RATING_MINIM || rating > RATING_MAXIM) {
            throw new IllegalArgumentException("Ratingul ar trebui sa fie intre " + RATING_MINIM + " si " + RATING_MAXIM + ".");
        }
        this.rating = rating;
        if (comentariu == null) {
            this.comentariu = "";
        }
        else {
            this.comentariu = comentariu.trim();
        }
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
        return rating == r.rating && comentariu.equals(r.comentariu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, comentariu);
    }
}
