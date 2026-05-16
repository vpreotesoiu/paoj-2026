package com.pao.project.fooddelivery.repository;

import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantRepository implements Repository<Restaurant, Integer> {
    private Connection connection;

    public RestaurantRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Restaurant r) {
        String findAdr = "SELECT id FROM adrese WHERE strada=? AND numar=? AND oras=? AND cod_postal=?";
        String insertAdr = "INSERT INTO adrese(strada, numar, oras, cod_postal) VALUES (?, ?, ?, ?)";
        String insertRest = "INSERT INTO restaurante(nume, adresa_id, taxa_livrare, procent_comision_sofer, livrare_gratuita) VALUES (?, ?, ?, ?, ?)";
        try {
            Integer adrId = null;
            Adresa adr = r.getAdresa();
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

            if (adrId == null) { // trb creata adresa
                try (PreparedStatement psAdr = connection.prepareStatement(insertAdr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psAdr.setString(1, adr.strada());
                    psAdr.setInt(2, adr.numar());
                    psAdr.setString(3, adr.oras());
                    psAdr.setString(4, adr.codPostal());

                    psAdr.executeUpdate();
                    try (ResultSet rsAdr = psAdr.getGeneratedKeys()) {
                        if (rsAdr.next()) {
                            adrId = rsAdr.getInt(1);
                        }
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(insertRest, PreparedStatement.RETURN_GENERATED_KEYS)) {
                String nume = r.getNume();
                double taxa = r.getTaxaLivrare();
                double comision = r.getProcentComisionSofer();
                boolean livrareGratis = r.areLivrareaGratuita();
                ps.setString(1, nume);
                ps.setInt(2, adrId);
                ps.setDouble(3, taxa);
                ps.setDouble(4, comision);

                if (livrareGratis) {
                    ps.setInt(5, 1);
                }
                else {
                    ps.setInt(5, 0);
                }
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int restId = rs.getInt(1);
                        r.setId(restId);
                        salveazaProduse(r, restId);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea restaurantului in baza de date: ", e);
        }
    }

    private void salveazaProduse(Restaurant r, int restId) {
        String sql = "INSERT INTO produse(restaurant_id, nume, categorie, pret, calorii, proteine, carbohidrati, grasimi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        for (Produs p : r.getProduse()) {
            try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                String nume = p.getNume();
                String cat = p.getCategorie();
                double pret = p.getPret();
                ValoriNutritionale valNut = p.getValoriNutritionale();
                ps.setInt(1, restId);
                ps.setString(2, nume);
                ps.setString(3, cat);
                ps.setDouble(4, pret);
                // reconstruieste valorile nutritionale!!
                ps.setInt(5, valNut.calorii());
                ps.setDouble(6, valNut.proteine());
                ps.setDouble(7, valNut.carbohidrati());
                ps.setDouble(8, valNut.grasimi());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int prodId = rs.getInt(1);
                        p.setId(prodId);
                        // delegare salvare a celorlalte componente ale produsului in alte functii
                        salveazaIngrediente(p, prodId);
                        salveazaExtra(p, prodId);
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea produselor restaurantului: ", e);
            }
        }
    }

    private void salveazaIngrediente(Produs p, int prodId) {
        String sql = "INSERT INTO ingrediente(produs_id, nume, alergen, optional) VALUES (?, ?, ?, ?)";
        for (Ingredient ingr : p.getIngrediente()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, prodId);
                ps.setString(2, ingr.nume());
                if (ingr.isAlergen()) {
                    ps.setInt(3, 1);
                }
                else {
                    ps.setInt(3, 0);
                }
                if (ingr.isOptional()) {
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
                } else {
                    ps.setInt(3, 0);
                }
                ps.setDouble(4, ie.getPret());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea ingr. extra ale unui produs: ", e);
            }
        }
    }

    @Override
    public Optional<Restaurant> findById(Integer id) {
        String sql = "SELECT r.id, r.nume, r.taxa_livrare, r.procent_comision_sofer, r.livrare_gratuita, a.strada, a.numar, a.oras, a.cod_postal FROM restaurante r JOIN adrese a ON r.adresa_id=a.id WHERE r.id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Restaurant r = buildRestaurant(rs);
                    return Optional.of(r);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea restaurantului dupa id:", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Restaurant> findAll() {
        String sql = "SELECT r.id, r.nume, r.taxa_livrare, r.procent_comision_sofer, r.livrare_gratuita, a.strada, a.numar, a.oras, a.cod_postal FROM restaurante r JOIN adrese a ON r.adresa_id = a.id";
        List<Restaurant> restaurante = new ArrayList<Restaurant>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Restaurant r = buildRestaurant(rs);
                    restaurante.add(r);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea restaurantelor: ", e);
        }

        return restaurante;
    }

    public Optional<Restaurant> findByNume(String nume) { // folosit pt RestaurantService
        String sql = "SELECT r.id, r.nume, r.taxa_livrare, r.procent_comision_Sofer, r.livrare_gratuita, a.strada, a.numar , a.oras, a.cod_postal FROM restaurante r JOIN adrese a ON r.adresa_id=a.id WHERE r.nume=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nume);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Restaurant r = buildRestaurant(rs);
                    return Optional.of(r);
                }
            }
        }
        catch (SQLException e ) {
            throw new RuntimeException("Eroare la cautarea restaurantului dupa nume: ", e);
        }
        return Optional.empty();
    }

    public boolean existsNume(String nume) {
        String sql = "SELECT 1 from restaurante WHERE nume=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nume);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la verificarea existentei restaurantului (dupa nume):" ,e);
        }
    }

    @Override
    public void update(Restaurant r) {
        String findAdr = "SELECT id FROM adrese WHERE strada=? AND numar=? AND oras=? AND cod_postal=?";
        String insertAdr = "INSERT INTO adrese(strada, numar, oras, cod_postal) VALUES (?,?,?,?)";
        String sql = "UPDATE restaurante SET nume=?, adresa_id=?, taxa_livrare=?, procent_comision_sofer=?, livrare_gratuita=? WHERE id=?";

        try {
            Integer adrId = null;
            Adresa adr = r.getAdresa();
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

            if (adrId == null) {
                try (PreparedStatement psAdr = connection.prepareStatement(insertAdr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psAdr.setString(1, adr.strada());
                    psAdr.setInt(2, adr.numar());
                    psAdr.setString(3, adr.oras());
                    psAdr.setString(4, adr.codPostal());
                    psAdr.executeUpdate();
                    try (ResultSet rsAdr = psAdr.getGeneratedKeys()) {
                        if(rsAdr.next()) {
                            adrId = rsAdr.getInt(1);
                        }
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                String nume = r.getNume();
                double taxa = r.getTaxaLivrare();
                double comision = r.getProcentComisionSofer();
                boolean livrGrat = r.areLivrareaGratuita();
                int idRest = r.getId();

                ps.setString(1, nume);
                ps.setInt(2, adrId);
                ps.setDouble(3, taxa);
                ps.setDouble(4, comision);
                if (livrGrat) {
                    ps.setInt(5, 1);
                }
                else {
                    ps.setInt(5, 0);
                }

                ps.setInt(6, idRest);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizare restaurant: ", e);
        }
    }

    private void stergeProduse(int restId) throws SQLException {
        // trb sa stergem si copii care au fk la produse
        String stergeIngr = "DELETE FROM ingrediente WHERE produs_id IN (SELECT id FROM produse WHERE restaurant_id=?)";
        String stergeExtra = "DELETE FROM extra WHERE produs_id IN (SELECT id FROM produse WHERE restaurant_id=?)";
        String stergeProduse = "DELETE FROM produse WHERE restaurant_id=?";

        // stergem ingr
        try (PreparedStatement psIngr = connection.prepareStatement(stergeIngr)) {
            psIngr.setInt(1, restId);
            psIngr.executeUpdate();
        }

        // stergem ingr extra
        try (PreparedStatement psExtra = connection.prepareStatement(stergeExtra)) {
            psExtra.setInt(1, restId);
            psExtra.executeUpdate();
        }

        // stergem produsele insfarsit
        try (PreparedStatement psProd = connection.prepareStatement(stergeProduse)) {
            psProd.setInt(1, restId);
            psProd.executeUpdate();
        }
    }

    private List<Produs> citesteProduse(int restId) {
        String sql = "SELECT * FROM produse WHERE restaurant_id = ?";
        List<Produs> pds = new ArrayList<Produs>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, restId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idProd = rs.getInt("id");
                    String nume = rs.getString("nume");
                    String cat = rs.getString("categorie");
                    double pret = rs.getDouble("pret");
                    int cal = rs.getInt("calorii");
                    double proteine = rs.getDouble("proteine");
                    double carbohidrati = rs.getDouble("carbohidrati");
                    double grasimi = rs.getDouble("grasimi");

                    ValoriNutritionale valNut = new ValoriNutritionale(cal, proteine, carbohidrati, grasimi);
                    Produs p = new Produs(idProd, restId, nume, cat, pret, valNut);
                    List<Ingredient> is = citesteIngrediente(idProd);
                    for (Ingredient i : is) {
                        p.adaugaIngredient(i);
                    }

                    List<IngredientExtra> ies = citesteExtra(idProd);
                    for (IngredientExtra ie : ies ) {
                        p.adaugaIngredientExtraDisponibil(ie);
                    }
                    pds.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea produselor restaurantului: ", e);
        }
        return pds;
    }

    private List<Ingredient> citesteIngrediente(int prodId) {
        String sql = "SELECT * FROM ingrediente WHERE produs_id = ?";
        List<Ingredient> is = new ArrayList<Ingredient>();

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
                    is.add(i);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingredientelor produslui: ", e);
        }

        return is;
    }

    private List<IngredientExtra> citesteExtra(int prodId) {
        String sql = "SELECT * FROM extra WHERE produs_id = ?";
        List<IngredientExtra> ies = new ArrayList<IngredientExtra>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nume = rs.getString("nume");
                    boolean alergen;
                    if (rs.getInt("alergen") == 1) {
                        alergen = true;
                    } else {
                        alergen = false;
                    }
                    double pret = rs.getDouble("pret");

                    Ingredient i = new Ingredient(nume, alergen, false); // false pt ca nu se
                                                                                 // ia in considerare ca optional
                    IngredientExtra ie = new IngredientExtra(i, pret);
                    ies.add(ie);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingr. extra ale produsului: ", e);
        }

        return ies;
    }

    @Override
    public void delete(Integer id) {
        String verificaComenzi = "SELECT 1 FROM comenzi WHERE restaurant_id = ?";
        String stergeFav = "DELETE from favorite WHERE restaurant_id = ?";
        String stergeRest = "DELETE FROM restaurante WHERE id = ?";
        try {
            // verifica daca exista comenzi care depind de restaurantul respectiv
            try (PreparedStatement psVerif = connection.prepareStatement(verificaComenzi)) {
                psVerif.setInt(1, id);
                try (ResultSet rsVerif = psVerif.executeQuery()) {
                    if (rsVerif.next()) {
                        throw new RuntimeException("Restaurantul nu poate fi sters pentru ca exista comenzi asociate.");
                    }
                }
            }
            // sterge mai intai favoritele pt ca Restaurant depinde de ele
            try (PreparedStatement psFav = connection.prepareStatement(stergeFav)) {
                psFav.setInt(1, id);
                psFav.executeUpdate();
            }
            stergeProduse(id);
            // dupa aceea e ok sa stergi restaurantul
            try (PreparedStatement psRest = connection.prepareStatement(stergeRest)) {
                psRest.setInt(1, id);
                psRest.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea de restaurant: ", e);
        }
    }

    public boolean isEmpty() {
        String sql = "SELECT id FROM restaurante";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la verificarea daca tabela restaurante este goala: ", e);
        }
    }

    private Restaurant buildRestaurant(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nume = rs.getString("nume");
        double taxa = rs.getDouble("taxa_livrare");
        double comision = rs.getDouble("procent_comision_sofer");
        boolean livrareGratis;
        if (rs.getInt("livrare_gratuita") == 1) {
            livrareGratis = true;
        } else {
            livrareGratis = false;
        }

        String strada = rs.getString("strada");
        int nr = rs.getInt("numar");
        String oras = rs.getString("oras");
        String codPostal = rs.getString("cod_postal");
        Adresa adr = new Adresa(strada, nr, oras, codPostal);
        Restaurant r = new Restaurant(id, nume, adr, taxa, comision, livrareGratis);

        List<Produs> ps = citesteProduse(id);
        for (Produs p : ps) {
            r.adaugaProdus(p);
        }

        return r;
    }
}
