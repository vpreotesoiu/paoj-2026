package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.AdresaNegasitaException;
import com.pao.project.fooddelivery.exception.CardNegasitException;
import com.pao.project.fooddelivery.exception.RestaurantInvalidException;
import com.pao.project.fooddelivery.exception.RestaurantNegasitException;

import java.util.HashSet;
import java.util.Set;

public class Client extends Utilizator {
    private Set<Adresa> adrese;
    private Set<CardBancar> carduri;
    private Set<Restaurant> restauranteFavorite;

    public Client(String nume, String email, String parola) {
        super(nume, email, parola);
        this.adrese = new HashSet<Adresa>();
        this.carduri = new HashSet<CardBancar>();
        this.restauranteFavorite = new HashSet<Restaurant>();
    }

    public Set<Adresa> getAdrese() {
        return Set.copyOf(adrese);
    }

    public Set<CardBancar> getCarduri() {
        return Set.copyOf(carduri);
    }

    public Set<Restaurant> getRestauranteFavorite() {
        return Set.copyOf(restauranteFavorite);
    }


    public void adaugaAdresa(Adresa adresa) {
        if (adresa == null) {
            throw new IllegalArgumentException("Adresa de adaugat nu poate fi vida!");
        }
        if (!adrese.add(adresa)) {
            throw new IllegalArgumentException("Adresa exista deja in lista clientului!");
        }
    }
    public void adaugaCard(CardBancar cb) {
        if (cb == null) {
            throw new IllegalArgumentException("Cardul bancar de adaugat nu poate fi vid!");
        }
        if (!carduri.add(cb)) {
            throw new IllegalArgumentException("Cardul existas deja in lista clientului!");
        }
    }
    public void adaugaRestaurantFavorit(Restaurant r) {
        if (r == null) {
            throw new RestaurantInvalidException(r);
        }
        if (!restauranteFavorite.add(r)) {
            throw new IllegalArgumentException("Restaurantul exista deja in lista de favorite!");
        }
    }

    public void stergeAdresa(Adresa adresa) {
        if (!adrese.remove(adresa)) {
            throw new AdresaNegasitaException(adresa);
        }
    }

    public void stergeCard(CardBancar card) {
        if (!carduri.remove(card)) {
            throw new CardNegasitException(card);
        }
    }

    public void stergeFavorit(Restaurant restaurant) {
        if (!restauranteFavorite.remove(restaurant)) {
            throw new RestaurantNegasitException(restaurant);
        }
    }

    public boolean areLivrareGratuita(Restaurant r) {
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }
        return false;
    }

    @Override
    public String getRol() {
        return "Client";
    }

    @Override
    public String toString() {
        return "Client{nume='" + getNume() + "', email='" + getEmail() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Client o2)) {
            return false;
        }
        return getEmail().equals(o2.getEmail());
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }
}
