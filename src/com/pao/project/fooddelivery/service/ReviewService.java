package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.model.Comanda;
import com.pao.project.fooddelivery.model.Review;
import com.pao.project.fooddelivery.model.Sofer;
import com.pao.project.fooddelivery.repository.ComandaRepository;
import com.pao.project.fooddelivery.repository.ReviewRepository;

import java.util.*;

public class ReviewService {
//    private Map<Comanda, Review> reviewsCmd;
//    private Map<Sofer, List<Review>> reviewsSoferi;

    private ReviewRepository reviewRepository;
    private ComandaRepository comandaRepository; // folosita doar pentru reviews comenzi

    private ReviewService() {
//        this.reviewsCmd = new HashMap<Comanda, Review>();
//        this.reviewsSoferi = new HashMap<Sofer, List<Review>>();
        this.reviewRepository = new ReviewRepository();
        this.comandaRepository = new ComandaRepository();
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
        // verificari pt daca review-ul chiar corespunde comenzii
        if (r.getComandaId() != c.getId()) {
            throw new IllegalArgumentException("Review-ul nu corespunde comenzii primite ca parametru!");
        }
        if (r.getSoferId() != c.getSoferAsignat().getId()) {
            throw new IllegalArgumentException("Review-ul nu este al soferului care a preluat comanda!");
        }
//        if (reviewsCmd.containsKey(c)) {
//            throw new IllegalStateException("Exista deja un review pentru comanda asta!");
//        }
//        reviewsCmd.put(c, r);
        if (reviewRepository.existsByComandaId(c.getId())) {
            throw new IllegalStateException("Exista deja un review pentru comanda asta!");
        }

//        Sofer s = c.getSoferAsignat();
//        if (!reviewsSoferi.containsKey(s)) {
//            reviewsSoferi.put(s, new ArrayList<Review>());
//        }
//        reviewsSoferi.get(s).add(r);
        reviewRepository.save(r);
    }

    public Review getReviewComanda(Comanda c) {
        if (c == null) {
            throw new IllegalArgumentException("Comanda nu poate fi vida!");
        }
//        if (!reviewsCmd.containsKey(c)) {
//            throw new IllegalArgumentException("Nu exista review pt aceasta comanda!");
//        }
//        return reviewsCmd.get(c);
        Optional<Review> reviewGasit = reviewRepository.findByComandaId(c.getId());
        if (reviewGasit.isEmpty()) {
            throw new IllegalArgumentException("Nu exista review pentru comanda asta!");
        }
        return reviewGasit.get();
    }

    public boolean areReview(Comanda c) {
        if (c == null) {
            return false;
        }
//        return reviewsCmd.containsKey(c);
        return reviewRepository.existsByComandaId(c.getId());
    }

    public List<Review> getReviewsSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
//        if (!reviewsSoferi.containsKey(s)) {
//            return new ArrayList<Review>();
//        }
//        return List.copyOf(reviewsSoferi.get(s));
        return reviewRepository.findBySoferId(s.getId());
    }

    public double getMedieRatingSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid!");
        }
//        if (!reviewsSoferi.containsKey(s) || reviewsSoferi.get(s).isEmpty()) {
//            return 0;
//        }

//        List<Review> reviews = reviewsSoferi.get(s);
        List<Review> reviews = reviewRepository.findBySoferId(s.getId());
        if (reviews.isEmpty()) {
            return 0;
        }
        double suma = 0;
//        for (Review r : reviews) {
//            suma += r.getRating();
//        }
//        return suma / reviews.size();
        for (Review r : reviews) {
            suma += r.getRating();
        }
        return suma / reviews.size(); // medie aritmetica
    }

    // asta face media cu AVG, ala de mai sus face fara dar returneaza double
    public List<String> getMedieRatingSoferiStrs() {
        return reviewRepository.getMedieRatingSoferi();
    }

    public Map<Comanda, Review> getReviewsComenzi() {
//        return Map.copyOf(reviewsCmd);
        Map<Comanda, Review> reviewsCmd = new HashMap<Comanda, Review>();
        List<Review> reviews = reviewRepository.findAll();
        for (Review r : reviews) {
            int cmdId = r.getComandaId();
            Optional<Comanda> cmdGasita = comandaRepository.findById(cmdId);

            if (cmdGasita.isPresent()) {
                Comanda c = cmdGasita.get();
                reviewsCmd.put(c, r);
            }
        }

        return Map.copyOf(reviewsCmd);
    }
}
