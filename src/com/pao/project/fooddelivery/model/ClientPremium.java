package com.pao.project.fooddelivery.model;

public class ClientPremium extends Client {

    public ClientPremium(String nume, String email, String parola) {
        super(nume, email, parola);
    }

    @Override
    public boolean areLivrareGratuita(Restaurant r) {
        if (r == null) {
            throw new IllegalArgumentException("Restaurantul nu poate fi vid!");
        }
        return r.areLivrareaGratuita();
    }

    @Override
    public String getRol() {
        return "Client Premium";
    }

    @Override
    public String toString() {
        return "ClientPremium{nume='" + getNume() + "', email='" + getEmail() + "'}";
    }
}
