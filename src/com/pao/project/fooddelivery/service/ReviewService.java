package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.Comanda;
import com.pao.project.fooddelivery.model.Review;
import com.pao.project.fooddelivery.model.Sofer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewService {
    private Map<Comanda, Review> reviewsCmd;
    private Map<Sofer, List<Review>> reviewsSoferi;

    private ReviewService() {
        this.reviewsCmd = new HashMap<Comanda, Review>();
        this.reviewsSoferi = new HashMap<Sofer, List<Review>>();
    }

    private static class Holder {
        private static final ReviewService INSTANCE = new ReviewService();
    }

    public static ReviewService getInstance() {
        return Holder.INSTANCE;
    }

    public void adaugaReview(Comanda c, Review r) {
        if (c == null) {
            throw new IllegalArgumentException("Comanda nu poate fi vida!");
        }
        if (r == null) {
            throw new IllegalArgumentException("Reviewul nu poate fi vid!");
        }
        if (!c.esteFinalizata()) {
            throw new IllegalStateException("Se poate adauga review doar la comenzi finalizate!");
        }
        if (!c.areSoferAsignat()) {
            throw new IllegalStateException("Comanda nu are sofer asignat, deci nu se poate lasa review!");
        }
        if (reviewsCmd.containsKey(c)) {
            throw new IllegalStateException("Exista deja un review pentru comanda asta!");
        }
        reviewsCmd.put(c, r);

        Sofer s = c.getSoferAsignat();
        if (!reviewsSoferi.containsKey(s)) {
            reviewsSoferi.put(s, new ArrayList<Review>());
        }
        reviewsSoferi.get(s).add(r);
    }

    public Review getReviewComanda(Comanda c) {
        if (c == null) {
            throw new IllegalArgumentException("Comanda nu poate fi vida!");
        }
        if (!reviewsCmd.containsKey(c)) {
            throw new IllegalArgumentException("Nu exista review pt aceasta comanda!");
        }
        return reviewsCmd.get(c);
    }

    public boolean areReview(Comanda c) {
        if (c == null) {
            return false;
        }
        return reviewsCmd.containsKey(c);
    }

    public List<Review> getReviewsSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        if (!reviewsSoferi.containsKey(s)) {
            return new ArrayList<Review>();
        }
        return List.copyOf(reviewsSoferi.get(s));
    }

    public double getMedieRatingSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
        if (!reviewsSoferi.containsKey(s) || reviewsSoferi.get(s).isEmpty()) {
            return 0;
        }

        List<Review> reviews = reviewsSoferi.get(s);
        double suma = 0;
        for (Review r : reviews) {
            suma += r.getRating();
        }
        return suma / reviews.size();
    }

    public Map<Comanda, Review> getReviewsComenzi() {
        return Map.copyOf(reviewsCmd);
    }
}
