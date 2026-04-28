package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.exception.RestaurantInvalidException;
import com.pao.project.fooddelivery.exception.RestaurantNegasitException;
import com.pao.project.fooddelivery.model.Produs;
import com.pao.project.fooddelivery.model.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantService {
    private List<Restaurant> restaurante;

    private RestaurantService() {
        this.restaurante = new ArrayList<Restaurant>();
    }

    private static class Holder {
        private static final RestaurantService INSTANCE = new RestaurantService();
    }

    public static RestaurantService getInstance() {
        return Holder.INSTANCE;
    }

    public void adaugaRestaurant(Restaurant r) {
        if (r == null) {
            throw new RestaurantInvalidException(r);
        }
        if (restaurante.contains(r)) {
            throw new IllegalArgumentException("Restaurantul deja exista in sistem!");
        }
        restaurante.add(r);
    }

    public void stergeRestaurant(Restaurant r) {
        if (r == null) {
            throw new RestaurantInvalidException(r);
        }
        if (!restaurante.remove(r)) {
            throw new RestaurantNegasitException(r);
        }
    }

    public List<Restaurant> getRestaurante() {
        List<Restaurant> restauranteSortate = new ArrayList<Restaurant>(restaurante);
        Collections.sort(restauranteSortate);
        return restauranteSortate;
    }

    public Restaurant cautaDupaNume(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele restaurantului nu poate fi vid!");
        }

        String numeCautat = nume.trim();
        for (Restaurant r : restaurante) {
            if (r.getNume().equalsIgnoreCase(numeCautat)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Nu exista nici un restaurant cu numele " + numeCautat + "!");
    }

    public List<Restaurant> cautaToateDupaNume(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele restaurantului nu poate fi vid!");
        }
        String numeCautat = nume.trim().toLowerCase();
        List<Restaurant> rezultate = new ArrayList<Restaurant>();

        for (Restaurant r: restaurante) {
            String numeRest = r.getNume().toLowerCase();
            if (numeRest.contains(numeCautat)) {
                rezultate.add(r);
            }
        }
        return rezultate;
    }

    public List<Produs> getMeniuRestaurant(String numeRest) {
        Restaurant r = cautaDupaNume(numeRest);
        return r.getProduse();
    }

    public List<Produs> cautaProduse(String numeRest, String numeProd) {
        Restaurant r = cautaDupaNume(numeRest);
        return r.cautaProdus(numeProd);
    }

    public List<Produs> filtreazaProduseDupaCategorie(String numeRest, String cat) {
        Restaurant r = cautaDupaNume(numeRest);
        return r.filtreazaCategorie(cat);
    }

    public boolean existaRestaurant(Restaurant r) {
        if (r == null) {
            return false;
        }
        return restaurante.contains(r);
    }

    public boolean existaRestaurantCuNume(String nume) {
        if (nume == null || nume.isBlank()) {
            return false;
        }

        String numeCautat = nume.trim();
        for (Restaurant r : restaurante) {
            if (r.getNume().equalsIgnoreCase(numeCautat)) {
                return true;
            }
        }
        return false;
    }
}
