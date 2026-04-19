package com.pao.project.fooddelivery.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Comanda {
    private static int cnt = 0;

    private int id;
    private Client client;
    private Restaurant restaurant;
    private Adresa adresaLivrare;
    private Map<ProdusPersonalizat, Integer> produse;
    private StatusComanda status;
    private MetodaPlata metodaPlata;
    private Sofer soferAsignat;

    public Comanda(Client client, Restaurant restaurant, Adresa adresaLivrare,
                   Map<ProdusPersonalizat, Integer> produse, MetodaPlata metodaPlata) {
        if (client == null) {
            throw new IllegalArgumentException("Clientul asociat comenzii nu trebuie sa fie vid!");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurantul asociat comenzii nu trebuie sa fie vid!");
        }
        if (adresaLivrare == null) {
            throw new IllegalArgumentException("Adresa de livrare nu trebuie sa fie vida!");
        }
        if (produse == null || produse.isEmpty()) {
            throw new IllegalArgumentException("Dictionarul de produse de inclus in restaurant trebuie sa contina produse!");
        }
        if (metodaPlata == null) {
            throw new IllegalArgumentException("Comanda trebuie sa fie efectuata cu o metoda de plata (campul metodei de plata este vid)!");
        }
        // validare produse din cos
        for (Map.Entry<ProdusPersonalizat, Integer> elem : produse.entrySet()) {
            ProdusPersonalizat pp = elem.getKey();
            Integer cant = elem.getValue();
            if (pp == null) {
                throw new IllegalArgumentException("Exista un produs personalizat vid in comanda!");
            }
            if (cant == null || cant <= 0) {
                throw new IllegalArgumentException("Cantitatea produsului trebuie sa fie pozitiva!");
            }
        }
        List<Produs> produseRestaurant = restaurant.getProduse();
        // validare restaurant
        for (ProdusPersonalizat pp : produse.keySet()) {
            if (!produseRestaurant.contains(pp.getProdus())) {
                throw new IllegalArgumentException("Produsul " + pp.getProdus().getNume() +
                        " nu apartine restaurantului " + restaurant.getNume() + "!");
            }
        }
        this.id = ++cnt;
        this.client = client;
        this.restaurant = restaurant;
        this.adresaLivrare = adresaLivrare;
        this.produse = new HashMap<ProdusPersonalizat, Integer>(produse);
        this.status = StatusComanda.NEPRELUATA;
        this.metodaPlata = metodaPlata;
        this.soferAsignat = null;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Adresa getAdresaLivrare() {
        return adresaLivrare;
    }

    public Map<ProdusPersonalizat, Integer> getProduse() {
        return Map.copyOf(produse);
    }

    public StatusComanda getStatus() {
        return status;
    }

    public MetodaPlata getMetodaPlata() {
        return metodaPlata;
    }

    public Sofer getSoferAsignat() {
        return soferAsignat;
    }

    public boolean areSoferAsignat() {
        return soferAsignat != null;
    }

    public void asigneazaSofer(Sofer s) {
        if (s == null) {
            throw new IllegalArgumentException("Soferul nu poate fi vid la asignare!");
        }
        if (status != StatusComanda.NEPRELUATA) {
            throw new IllegalStateException("Soferul nu poate fi asignat daca comanda nu este nepreluata!");
        }
        if (!s.isDisponibil()) {
            throw new IllegalStateException("Soferul asignat nu este disponibil!");
        }
        this.soferAsignat = s;
        s.setDisponibil(false);
    }

    public void avanseazaStatus() {
        this.status = status.avanseaza();
    }

    public boolean esteFinalizata() {
        return status == StatusComanda.FINALIZATA;
    }

    @Override
    public String toString() {
        return "Comanda{id=" + id + ", client='" + client.getEmail() + "', restaurant='" + restaurant.getNume() +
                "', adresaLivrare=" + adresaLivrare + ", produse=" + produse + ", status=" + status + ", metodaPlata="
                + metodaPlata + ", soferAsignat=" + (soferAsignat == null ? "neasignat" : soferAsignat.getEmail()) + "}";
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
        Comanda c = (Comanda) o;
        return id == c.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
