package com.pao.project.fooddelivery.service;

import com.pao.project.fooddelivery.exception.RestaurantInvalidException;
import com.pao.project.fooddelivery.exception.RestaurantNegasitException;
import com.pao.project.fooddelivery.model.Produs;
import com.pao.project.fooddelivery.model.Restaurant;
import com.pao.project.fooddelivery.repository.ProdusRepository;
import com.pao.project.fooddelivery.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RestaurantService {
//    private List<Restaurant> restaurante;
    private RestaurantRepository restaurantRepository;
    private ProdusRepository produsRepository;

    private RestaurantService() {
//        this.restaurante = new ArrayList<Restaurant>();
        this.restaurantRepository = new RestaurantRepository();
        this.produsRepository = new ProdusRepository();
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
//        if (restaurante.contains(r)) {
//            throw new IllegalArgumentException("Restaurantul deja exista in sistem!");
//        }
//        restaurante.add(r);
        if (restaurantRepository.existsNume(r.getNume())) {
            throw new IllegalArgumentException("Restaurantul deja exista in sistem!");
        }
        restaurantRepository.save(r);
    }

    public void stergeRestaurant(Restaurant r) {
        if (r == null) {
            throw new RestaurantInvalidException(r);
        }
//        if (!restaurante.remove(r)) {
//            throw new RestaurantNegasitException(r);
//        }
        Optional<Restaurant> restGasit = restaurantRepository.findById(r.getId());
        if (restGasit.isEmpty()) {
            throw new RestaurantNegasitException(r);
        }

        restaurantRepository.delete(r.getId());
    }

    public List<Restaurant> getRestaurante() {
        List<Restaurant> restaurante = restaurantRepository.findAll();
        List<Restaurant> restauranteSortate = new ArrayList<Restaurant>(restaurante);
        Collections.sort(restauranteSortate);
        return restauranteSortate;
    }

    public Restaurant cautaDupaNume(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele restaurantului nu poate fi vid!");
        }

        String numeCautat = nume.trim();
//        for (Restaurant r : restaurante) {
//            if (r.getNume().equalsIgnoreCase(numeCautat)) {
//                return r;
//            }
//        }
        Optional<Restaurant> restGasit = restaurantRepository.findByNume(numeCautat);
        if (restGasit.isEmpty()) {
            throw new IllegalArgumentException("Nu exista nici un restaurant cu numele " + numeCautat + "!");
        }
        return restGasit.get();
//        throw new IllegalArgumentException("Nu exista nici un restaurant cu numele " + numeCautat + "!");
    }

    public List<Restaurant> cautaToateDupaNume(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele restaurantului nu poate fi vid!");
        }
        String numeCautat = nume.trim().toLowerCase();
        List<Restaurant> rezultate = new ArrayList<Restaurant>();

//        for (Restaurant r: restaurante) {
//            String numeRest = r.getNume().toLowerCase();
//            if (numeRest.contains(numeCautat)) {
//                rezultate.add(r);
//            }
//        }
        for (Restaurant r : restaurantRepository.findAll()) {
            String numeRest = r.getNume().toLowerCase();
            if (numeRest.contains(numeCautat)) {
                rezultate.add(r);
            }
        }
        return rezultate;
    }

    public List<Produs> getMeniuRestaurant(String numeRest) {
        Restaurant r = cautaDupaNume(numeRest);
//        return r.getProduse();
        return produsRepository.findByRestaurantId(r.getId()); // produsRepository se ocupa de meniuri
    }

    public List<Produs> cautaProduse(String numeRest, String numeProd) {
        if (numeProd == null || numeProd.isBlank()) {
            throw new IllegalArgumentException("Numele produsului nu poate sa fie vid!");
        }
        Restaurant r = cautaDupaNume(numeRest);
        String numePtCautat = numeProd.trim().toLowerCase(); // pt normalizare la cautare
        List<Produs> cautate = new ArrayList<Produs>();

        List<Produs> meniu = produsRepository.findByRestaurantId(r.getId());
        for (Produs p : meniu) {
            String nume = p.getNume().toLowerCase();
            if (nume.contains(numePtCautat)) {
                cautate.add(p);
            }
        }
        return cautate;
//        return r.cautaProdus(numeProd);
    }

    public List<Produs> filtreazaProduseDupaCategorie(String numeRest, String cat) {
        if (cat == null || cat.isBlank()) {
            throw new IllegalArgumentException("Categoria nu poate fi vida!");
        }

        Restaurant r = cautaDupaNume(numeRest);
        String catPtFiltrat = cat.trim().toUpperCase();
        List<Produs> filtrate = new ArrayList<Produs>();

        List<Produs> meniu = produsRepository.findByRestaurantId(r.getId());
        for (Produs p : meniu) {
            String catCrt = p.getCategorie();
            if (catCrt.equalsIgnoreCase(catPtFiltrat)) {
                filtrate.add(p);
            }
        }
        return filtrate;
        //        return r.filtreazaCategorie(cat);
    }

    public boolean existaRestaurant(Restaurant r) {
        if (r == null) {
            return false;
        }
//        return restaurante.contains(r);
        Optional<Restaurant> restCautat = restaurantRepository.findById(r.getId());
        return restCautat.isPresent();
    }

    public boolean existaRestaurantCuNume(String nume) {
        if (nume == null || nume.isBlank()) {
            return false;
        }

        String numeCautat = nume.trim();
//        for (Restaurant r : restaurante) {
//            if (r.getNume().equalsIgnoreCase(numeCautat)) {
//                return true;
//            }
//        }
//        return false;
        return restaurantRepository.existsNume(numeCautat);
    }
}
