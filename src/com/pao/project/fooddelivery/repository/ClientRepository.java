package com.pao.project.fooddelivery.repository;

import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClientRepository implements Repository<Client, Integer> {
    private Connection connection;

    public ClientRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();;
    }

    @Override
    public void save(Client c) {
        String sql = "INSERT INTO clienti(nume, email, parola, este_premium) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getParola());
            if (c instanceof ClientPremium) {
                ps.setInt(4, 1);
            }
            else {
                ps.setInt(4, 0);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int clientId = rs.getInt(1);
                    c.setId(clientId);
                    salveazaAdrese(c, clientId);
                    salveazaCarduri(c, clientId);
                    salveazaFavorite(c, clientId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea clientului in baza de date: ", e);
        }
    }

    private void salveazaAdrese (Client c, int clientId) {
        // salveaza adresa la fiecare client
        String insertAdr = "INSERT INTO adrese(strada, numar, oras, cod_postal) VALUES (?, ?, ?, ?)";
        String findAdr = "SELECT id FROM adrese WHERE strada = ? AND numar = ? AND oras = ? AND cod_postal = ?";
        String insertRel = "INSERT INTO clienti_adrese(client_id, adresa_id) VALUES (?, ?)";
        for (Adresa adr : c.getAdrese()) {
            try {
                Integer adrId = null; // cautam id-ul corespunzator adresei clientului
                try (PreparedStatement psAdr = connection.prepareStatement(findAdr)) {
                    psAdr.setString(1, adr.strada());
                    psAdr.setInt(2, adr.numar());
                    psAdr.setString(3, adr.oras());
                    psAdr.setString(4, adr.codPostal());
                    try (ResultSet rsAdr = psAdr.executeQuery()) {
                        if (rsAdr.next()) {
                            adrId = rsAdr.getInt("id");
                        }
                    }
                }

                if (adrId == null) { // daca adresa respectiva nu exista in baza de date, o bagam
                    try (PreparedStatement psAdr = connection.prepareStatement(insertAdr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        psAdr.setString(1, adr.strada());
                        psAdr.setInt(2, adr.numar());
                        psAdr.setString(3, adr.oras());
                        psAdr.setString(4, adr.codPostal());
                        psAdr.executeUpdate();

                        try (ResultSet rsAdr = psAdr.getGeneratedKeys()) {
                            if (rsAdr.next()) {
                                adrId = rsAdr.getInt(1); // ia id-ul dupa ce a fost creata adresa
                            }
                        }
                    }
                }

                try (PreparedStatement psRel = connection.prepareStatement(insertRel)) {
                    psRel.setInt(1, clientId);
                    psRel.setInt(2, adrId);
                    psRel.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea adreselor clientului: ", e);
            }
        }
    }

    private void salveazaCarduri(Client c, int clientId) {
        String sql = "INSERT INTO carduri(client_id, numar, balanta) VALUES (?, ?, ?)";

        for (CardBancar cb : c.getCarduri()) {
            try (PreparedStatement psCb = connection.prepareStatement(sql)) {
                psCb.setInt(1, clientId);
                psCb.setString(2, cb.getNumar());
                psCb.setDouble(3, cb.getBalanta());
                psCb.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea cardurilor clientului: ", e);
            }
        }
    }

    private void salveazaFavorite(Client c, int clientId) {
        String findRestaurantId = "SELECT id FROM restaurante WHERE nume = ?";
        String insertFavorite = "INSERT INTO favorite(client_id, restaurant_id) VALUES (?, ?)";
        for (Restaurant r : c.getRestauranteFavorite()) {
            try {
                Integer restId = null;
                try (PreparedStatement psRest = connection.prepareStatement(findRestaurantId)) {
                    // cauta id-ul restaurantului dupa nume
                    psRest.setString(1, r.getNume());
                    try (ResultSet rsRest = psRest.executeQuery()) {
                        if (rsRest.next()) {
                            restId = rsRest.getInt("id");
                        }
                    }
                }

                // daca n-a fost gasit il adaugam

                if (restId != null) {
                    try (PreparedStatement psRest = connection.prepareStatement(insertFavorite)) {
                        psRest.setInt(1, clientId);
                        psRest.setInt(2, restId);
                        psRest.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea restaurantelor favorite: ", e);
            }
        }
    }

    @Override
    public Optional<Client> findById(Integer id) {
        String sql = "SELECT * FROM clienti WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Client c = buildClient(rs);
                    return Optional.of(c); // returneaza valoarea daca exista;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea clientului dupa id: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * from clienti";
        List<Client> clienti = new ArrayList<Client>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { // ce oribil arata asta
                while (rs.next()) {
                    Client c = buildClient(rs);
                    clienti.add(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea clientilor: ", e);
        }

        return clienti;
    }

    public Optional<Client> findByEmail(String email) {
        String sql = "SELECT * FROM clienti WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = buildClient(rs);
                    return Optional.of(c);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea clientului dupa email: ", e);
        }
        return Optional.empty();
    }

    public boolean existsEmail(String email) {
        String sql = "SELECT 1 FROM clienti WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // o sa returneze 1 care isi ia cast la boolean automat
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la verificarea email-ului daca exista: ", e);
        }
    }

    private Optional<Integer> findIdByEmail(String email) {
        String sql = "SELECT id FROM clienti WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idClient = rs.getInt("id");
                    return Optional.of(idClient);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea id-ului de la client: ", e);
        }
        return Optional.empty();
    }

    private void stergeLegaturiAdrese(int clientId) throws SQLException { // sterge adresele
                                                                          // salvate ale unui client
        String sql = "DELETE FROM clienti_adrese WHERE client_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

    private void stergeCarduri(int clientId) throws SQLException {
        String sql = "DELETE FROM carduri WHERE client_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

    private void stergeFavorite(int clientId) throws SQLException {
        String sql = "DELETE FROM favorite WHERE client_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Client c) {
        String sql = "UPDATE clienti SET nume=?, email=?, parola=?, este_premium=? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getParola());
            if (c instanceof ClientPremium) {
                ps.setInt(4, 1);
            }
            else {
                ps.setInt(4, 0);
            }
            ps.setInt(5, c.getId());
            ps.executeUpdate();

            int idClient = c.getId();
            stergeLegaturiAdrese(idClient);
            stergeCarduri(idClient);
            stergeFavorite(idClient);
            salveazaAdrese(c, idClient);
            salveazaCarduri(c, idClient);
            salveazaFavorite(c, idClient);
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea clientului: ", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM clienti WHERE id = ?";

        try {
            stergeLegaturiAdrese(id);
            stergeCarduri(id);
            stergeFavorite(id);

            try (PreparedStatement psDel = connection.prepareStatement(sql)) {
                psDel.setInt(1, id);
                psDel.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea clientului: ", e);
        }
    }

    private List<Adresa> citesteAdrese(int clientId) {
        String sql = "SELECT a.strada, a.numar, a.oras, a.cod_postal FROM adrese a JOIN clienti_adrese ca ON a.id = ca.adresa_id WHERE ca.client_id = ?";
        List<Adresa> adrs = new ArrayList<Adresa>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String strada = rs.getString("strada");
                    int numar = rs.getInt("numar");
                    String oras = rs.getString("oras");
                    String codPostal = rs.getString("cod_postal");
                    Adresa a = new Adresa(strada, numar, oras, codPostal);
                    adrs.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea adreselor din baza de date: ", e);
        }

        return adrs;
    }

    private Set<CardBancar> citesteCarduri(int clientId) {
        String sql = "SELECT id, numar, balanta FROM carduri WHERE client_id = ?";
        Set<CardBancar> cbs = new HashSet<CardBancar>();

        try (PreparedStatement psCb = connection.prepareStatement(sql)) {
            psCb.setInt(1, clientId);

            try (ResultSet rsCb = psCb.executeQuery()) {
                while (rsCb.next()) {
                    int idCard = rsCb.getInt("id");
                    String numarCard = rsCb.getString("numar");
                    double bal = rsCb.getDouble("balanta");

                    CardBancar cb = new CardBancar(idCard, numarCard, bal);

                    cbs.add(cb);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea cardurilor clientului: ", e);
        }
        return cbs;
    }

    private Set<Restaurant> citesteFavorite(int clientId) {
        String sql = "SELECT r.id, r.nume, r.taxa_livrare, r.procent_comision_sofer, r.livrare_gratuita, a.strada, a.numar, a.oras, a.cod_postal FROM restaurante r JOIN favorite f ON r.id = f.restaurant_id JOIN adrese a ON r.adresa_id = a.id WHERE f.client_id = ?";
        Set<Restaurant> favs = new HashSet<Restaurant>();
        try (PreparedStatement psFav = connection.prepareStatement(sql)) {
            psFav.setInt(1, clientId);

            try (ResultSet rsFav = psFav.executeQuery()) {
                while (rsFav.next()) {
                    int idRest = rsFav.getInt("id");
                    String strAdr = rsFav.getString("strada");
                    int numAdr = rsFav.getInt("numar");
                    String orasAdr = rsFav.getString("oras");
                    String cpAdr = rsFav.getString("cod_postal");
                    Adresa a = new Adresa(strAdr, numAdr, orasAdr, cpAdr);

                    String numeRest = rsFav.getString("nume");
                    double taxaRest = rsFav.getDouble("taxa_livrare");
                    double comRest = rsFav.getDouble("procent_comision_sofer");
                    boolean livrRest;
                    if (rsFav.getInt("livrare_gratuita") == 1) {
                        livrRest = true;
                    }
                    else {
                        livrRest = false;
                    }

                    Restaurant r = new Restaurant(idRest, numeRest, a, taxaRest, comRest, livrRest);
                    favs.add(r);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea favoritelor clientului: ", e);
        }

        return favs;
    }

    private Client buildClient(ResultSet rs) throws SQLException { // trateaza eroarea in exterior daca apare
        int id = rs.getInt("id");
        String nume = rs.getString("nume");
        String email = rs.getString("email");
        String parola = rs.getString("parola");
        boolean estePremium;
        if (rs.getInt("este_premium") == 1) {
            estePremium = true;
        }
        else {
            estePremium = false;
        }
        Client c;
        if (estePremium) {
            c = new ClientPremium(id, nume, email, parola);
        }
        else {
            c = new Client(id, nume, email, parola);
        }

        List<Adresa> adreseClient = citesteAdrese(id);
        for (Adresa adr : adreseClient) {
            c.adaugaAdresa(adr);
        }

        Set<CardBancar> carduriClient = citesteCarduri(id);
        for (CardBancar cb : carduriClient) {
            c.adaugaCard(cb);
        }

        Set<Restaurant> restFavorite = citesteFavorite(id);
        for (Restaurant r : restFavorite) {
            c.adaugaRestaurantFavorit(r);
        }

        return c;
    }
}
