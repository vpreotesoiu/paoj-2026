package com.pao.project.fooddelivery.repository;

import com.pao.project.fooddelivery.model.Ingredient;
import com.pao.project.fooddelivery.model.IngredientExtra;
import com.pao.project.fooddelivery.model.Produs;
import com.pao.project.fooddelivery.model.ValoriNutritionale;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdusRepository implements Repository<Produs, Integer> {
    private Connection connection;

    public ProdusRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Produs p) {
        String sql = "INSERT INTO produse(restaurant_id, nume, categorie, pret, calorii, proteine, carbohidrati, grasimi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int restId = p.getRestaurantId();
            String nume = p.getNume();
            String cat = p.getCategorie();
            double pret = p.getPret();
            ValoriNutritionale valNut = p.getValoriNutritionale();

            ps.setInt(1, restId);
            ps.setString(2, nume);
            ps.setString(3, cat);
            ps.setDouble(4, pret);
            int cal = valNut.calorii();
            double prot = valNut.proteine();
            double cbhd = valNut.carbohidrati();
            double grasimi = valNut.grasimi();
            ps.setInt(5, cal);
            ps.setDouble(6, prot);
            ps.setDouble(7, cbhd);
            ps.setDouble(8, grasimi);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int prodId = rs.getInt(1);
                    p.setId(prodId);
                    salveazaIngrediente(p, prodId);
                    salveazaExtra(p, prodId);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea produsului in baza de date: ", e);
        }
    }

    @Override
    public Optional<Produs> findById(Integer id) {
        String sql = "SELECT * FROM produse WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produs p = buildProdus(rs);
                    return Optional.of(p);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea produsului dupa id: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Produs> findAll() {
        String sql = "SELECT * FROM produse";
        List<Produs> produse = new ArrayList<Produs>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produs p = buildProdus(rs);
                    produse.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea produselor: ", e);
        }
        return produse;
    }

    public List<Produs> findByRestaurantId(int restId) { // ca sa iei meniul unui restaurant practic
        String sql = "SELECT * FROM produse WHERE restaurant_id = ?";
        List<Produs> produse = new ArrayList<Produs>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, restId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produs p = buildProdus(rs);
                    produse.add(p);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea produselor restaurantului: ", e);
        }
        return produse;
    }

    @Override
    public void update(Produs p) {
        String sql = "UPDATE produse SET restaurant_id=?, nume=?, categorie=?, pret=?, calorii=?, proteine=?, carbohidrati=?, grasimi=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int restId = p.getRestaurantId();
            String nume = p.getNume();
            String cat = p.getCategorie();
            double pret = p.getPret();
            ValoriNutritionale valNut = p.getValoriNutritionale();
            int prodId = p.getId();
            ps.setInt(1, restId);
            ps.setString(2, nume);
            ps.setString(3, cat);
            ps.setDouble(4, pret);
            int cal = valNut.calorii();
            double prot = valNut.proteine();
            double cbhd = valNut.carbohidrati();
            double grasimi = valNut.grasimi();
            ps.setInt(5, cal);
            ps.setDouble(6, prot);
            ps.setDouble(7, cbhd);
            ps.setDouble(8, grasimi);
            ps.setInt(9, prodId);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea de produs: ", e);
        }
        int prodId = p.getId();

        if (!existsInComenzi(prodId)) {
            try {
                stergeIngredienteSiExtra(prodId);
                salveazaIngrediente(p, prodId);
                salveazaExtra(p, prodId);
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la actualizarea de ingrediente: ", e);
            }
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM produse WHERE id = ?";

        if (existsInComenzi(id)) {
            throw new RuntimeException("Produsul nu poate fi sters pentru ca apare in minim o comanda.");
        }
        try {
            stergeIngredienteSiExtra(id);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea produsului: ", e);
        }
    }

    private boolean existsInComenzi(int prodId) { // verifica daca produsul a aparut in vreo comanda pana acum
        String sql = "SELECT 1 FROM iteme_comanda WHERE produs_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prodId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la verificarea produsului daca apare in comenzi: ", e);
        }
    }

    private void salveazaIngrediente(Produs p, int prodId) {
        String sql = "INSERT INTO ingrediente(produs_id, nume, alergen, optional) VALUES (?, ?, ?, ?)";

        for (Ingredient i : p.getIngrediente()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, prodId);
                ps.setString(2, i.nume());

                if (i.isAlergen()) {
                    ps.setInt(3, 1);
                }
                else {
                    ps.setInt(3, 0);
                }
                if (i.isOptional()) {
                    ps.setInt(4, 1);
                }
                else {
                    ps.setInt(4, 0);
                }
                ps.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea ingredientelor produsului: ", e);
            }
        }
    }

    private void salveazaExtra(Produs p, int prodId) {
        String sql = "INSERT INTO extra(produs_id, nume, alergen, pret) VALUES (?, ?, ?, ?)";
        for (IngredientExtra ie : p.getIngredienteExtraDisponibile()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                Ingredient i = ie.getIngredient();

                ps.setInt(1, prodId);
                ps.setString(2, i.nume());
                if (i.isAlergen()) {
                    ps.setInt(3, 1);
                }
                else {
                    ps.setInt(3, 0);
                }

                double pret = ie.getPret();
                ps.setDouble(4, pret);
                ps.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea ingredientelor extra ale unui produs: ", e);
            }
        }
    }

    private void stergeIngredienteSiExtra(int prodId) throws SQLException {
        String stergeIngr = "DELETE FROM ingrediente WHERE produs_id=?";
        String stergeIe = "DELETE FROM extra WHERE produs_id=?";

        // sterge ingrediente
        try (PreparedStatement psIngr = connection.prepareStatement(stergeIngr)) {
            psIngr.setInt(1, prodId);
            psIngr.executeUpdate();
        }

        try (PreparedStatement psExtra = connection.prepareStatement(stergeIe)){
            psExtra.setInt(1, prodId);
            psExtra.executeUpdate();
        }
    }

    private List<Ingredient> citesteIngrediente(int prodId) {
        String sql = "SELECT * FROM ingrediente WHERE produs_id = ?";
        List<Ingredient> ingrs = new ArrayList<Ingredient>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nume = rs.getString("nume");

                    boolean alergen;
                    if (rs.getInt("alergen") == 1) {
                        alergen = true;
                    }
                    else {
                        alergen = false;
                    }
                    boolean optional;
                    if (rs.getInt("optional") == 1) {
                        optional = true;
                    }
                    else {
                        optional = false;
                    }

                    Ingredient i = new Ingredient(nume, alergen, optional);
                    ingrs.add(i);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingredientelor produsului: ", e);
        }

        return ingrs;
    }

    private List<IngredientExtra> citesteExtra(int prodId) {
        String sql = "SELECT * FROM extra WHERE produs_id = ?";
        List<IngredientExtra> ies = new ArrayList<IngredientExtra>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nume = rs.getString("nume");
                    double pret = rs.getDouble("pret");

                    boolean alergen;
                    if (rs.getInt("alergen") == 1) {
                        alergen = true;
                    }
                    else {
                        alergen = false;
                    }

                    Ingredient i = new Ingredient(nume, alergen, false);
                    IngredientExtra ie = new IngredientExtra(i, pret);
                    ies.add(ie);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingredientelor extra ale produsului: ", e);
        }

        return ies;
    }

    private Produs buildProdus(ResultSet rs) throws SQLException {
        int prodId = rs.getInt("id");
        int restId = rs.getInt("restaurant_id");
        String nume = rs.getString("nume");
        String cat = rs.getString("categorie");
        double pret = rs.getDouble("pret");
        int cal = rs.getInt("calorii");
        double prot = rs.getDouble("proteine");
        double cbhd = rs.getDouble("carbohidrati");
        double grasimi = rs.getDouble("grasimi");

        ValoriNutritionale valNut = new ValoriNutritionale(cal, prot, cbhd, grasimi);
        Produs p = new Produs(prodId, restId, nume, cat, pret, valNut);
        List<Ingredient> ingrs = citesteIngrediente(prodId);
        for (Ingredient ingr : ingrs) {
            p.adaugaIngredient(ingr);
        }
        List<IngredientExtra> ies = citesteExtra(prodId);
        for (IngredientExtra ie : ies) {
            p.adaugaIngredientExtraDisponibil(ie);
        }
        return p;
    }
}
