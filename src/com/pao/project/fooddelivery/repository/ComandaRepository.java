package com.pao.project.fooddelivery.repository;

// AAAAAAAAAAAAAAAAAAAA E PREA MULT COD

import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ComandaRepository implements Repository<Comanda, Integer> {
    private Connection connection;
    private ClientRepository clientRepository;
    private SoferRepository soferRepository;
    private RestaurantRepository restaurantRepository;

    public ComandaRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.clientRepository = new ClientRepository();
        this.soferRepository = new SoferRepository();
        this.restaurantRepository = new RestaurantRepository();
    }

    @Override
    public void save(Comanda c) {
        String findAdr = "SELECT id FROM adrese WHERE strada=? AND numar=? AND oras=? AND cod_postal=?";
        String insertAdr = "INSERT INTO adrese(strada, numar, oras, cod_postal) VALUES (?, ?, ?, ?)";
        String insertCMD = "INSERT INTO comenzi(client_id, restaurant_id, adresa_id, sofer_id, status, metoda_plata, total_plata) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Integer adrId = null;
            Adresa adr = c.getAdresaLivrare();
            try (PreparedStatement psAdr = connection.prepareStatement(findAdr)) {
                String strada = adr.strada();
                int nr = adr.numar();
                String oras = adr.oras();
                String codPostal = adr.codPostal();
                psAdr.setString(1, strada);
                psAdr.setInt(2, nr);
                psAdr.setString(3, oras);
                psAdr.setString(4, codPostal);
                try (ResultSet rsAdr = psAdr.executeQuery()) {
                    if (rsAdr.next()) {
                        adrId = rsAdr.getInt("id");
                    }
                }
            }
            if (adrId == null) { // daca adresa nu e gasita o cream
                try (PreparedStatement psAdr = connection.prepareStatement(insertAdr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    String strada = adr.strada();
                    int nr = adr.numar();
                    String oras = adr.oras();
                    String codPostal = adr.codPostal();
                    psAdr.setString(1, strada);
                    psAdr.setInt(2, nr);
                    psAdr.setString(3, oras);
                    psAdr.setString(4, codPostal);
                    psAdr.executeUpdate();

                    try (ResultSet rsAdr = psAdr.getGeneratedKeys()) {
                        if (rsAdr.next()) {
                            adrId = rsAdr.getInt(1);
                        }
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(insertCMD, PreparedStatement.RETURN_GENERATED_KEYS)) {
                int clientId = c.getClient().getId();
                int restId = c.getRestaurant().getId();
                String statusStr = c.getStatus().name();
                String mpStr = c.getMetodaPlata().name();
                double total = calculeazaTotalProduse(c);

                ps.setInt(1, clientId);
                ps.setInt(2, restId);
                ps.setInt(3, adrId);

                if (c.areSoferAsignat()) {
                    int soferId = c.getSoferAsignat().getId();
                    ps.setInt(4, soferId);
                } else {
                    ps.setObject(4, null); // nu stiu un mod mai bun sa zic
                                                           // ca comanda n-are sofer asignat in bd
                }
                ps.setString(5, statusStr);
                ps.setString(6, mpStr);
                ps.setDouble(7, total);

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cmdId = rs.getInt(1);
                        c.setId(cmdId);
                        salveazaItemeComanda(c, cmdId);
                    }
                }
            }

        }catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea comenzii in baza de date: ", e);
        }
    }

    private void salveazaItemeExtra(ProdusPersonalizat pp, int itemId) {
        String findExtra = "SELECT id FROM extra WHERE produs_id=? AND nume=?";
        String insertExtra = "INSERT INTO iteme_extra(item_comanda_id, extra_id, cantitate) VALUES (?,?,?)";

        for (Map.Entry<IngredientExtra, Integer> item : pp.getIngredienteExtra().entrySet()) {
            IngredientExtra ie = item.getKey();
            Integer cant = item.getValue();

            try {
                Integer extraId = null;

                try (PreparedStatement psFind = connection.prepareStatement(findExtra)) {
                    int prodId = pp.getProdus().getId();
                    String nume = ie.getIngredient().nume();
                    psFind.setInt(1, prodId);
                    psFind.setString(2, nume);

                    try (ResultSet rsFind = psFind.executeQuery()) {
                        if (rsFind.next()) {
                            extraId = rsFind.getInt("id");
                        }
                    }
                }

                if (extraId != null) {
                    try (PreparedStatement ps = connection.prepareStatement(insertExtra)) {
                        ps.setInt(1, itemId);
                        ps.setInt(2, extraId);
                        ps.setInt(3, cant);
                        ps.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea ingr. extra din item: ", e);
            }
        }
    }

    private void salveazaItemeComanda(Comanda c, int cmdId) {
        String sql = "INSERT INTO iteme_comanda(comanda_id, produs_id, cantitate, pret_unitar_baza) VALUES (?, ? ,?, ?)";
        for (Map.Entry<ProdusPersonalizat, Integer> e : c.getProduse().entrySet()) {
            ProdusPersonalizat pp = e.getKey();
            Integer cant = e.getValue();
            try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                Produs p = pp.getProdus();
                int prodId = p.getId();
                double pretBaza = p.getPret();
                ps.setInt(1, cmdId);
                ps.setInt(2, prodId);
                ps.setInt(3, cant);
                ps.setDouble(4, pretBaza);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int itemId = rs.getInt(1);
                        salveazaItemeFara(pp, itemId);
                        salveazaItemeExtra(pp, itemId);
                    }
                }
            } catch (SQLException exc) {
                throw new RuntimeException("Eroare la salvrarea itemelor comenzii: ", exc);
            }
        }
    }

    private void salveazaItemeFara(ProdusPersonalizat pp, int prodId) {
        String findIngr = "SELECT id FROM ingrediente WHERE produs_id=? AND nume=?";
        String insertFara = "INSERT INTO iteme_fara(item_comanda_id, ingredient_id) VALUES (?, ?)";

        for (Ingredient ingr : pp.getIngredienteEliminate()) {
            try {
                Integer ingrId = null;

                try (PreparedStatement psFind = connection.prepareStatement(findIngr)) {
                    int crtProdId = pp.getProdus().getId();
                    String numeIngr = ingr.nume();
                    psFind.setInt(1, crtProdId);
                    psFind.setString(2,numeIngr);

                    try (ResultSet rsFind = psFind.executeQuery()) {
                        if (rsFind.next()) {
                            ingrId = rsFind.getInt("id");
                        }
                    }
                }

                if (ingrId != null) {
                    try (PreparedStatement psInsert = connection.prepareStatement(insertFara)) {
                        psInsert.setInt(1, prodId);
                        psInsert.setInt(2, ingrId);
                        psInsert.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Eroare la salvarea ingredientelor eliminate din produs: ", e);
            }
        }
    }

    @Override
    public Optional<Comanda> findById(Integer id) {
        String sql = "SELECT * FROM comenzi WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Comanda c = buildComanda(rs);
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea comenzii dupa id: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Comanda> findAll() {
        String sql = "SELECT * FROM comenzi";
        List<Comanda> cmds = new ArrayList<Comanda>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comanda c = buildComanda(rs);
                    cmds.add(c);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea comenzilor din baza de date: ", e);
        }

        return cmds;
    }

    private void stergeProduseComanda(int cmdId) throws SQLException {
        String stergeFara = "DELETE FROM iteme_fara WHERE item_comanda_id IN (SELECT id FROM iteme_comanda WHERE comanda_id=?)";
        String stergeExtra = "DELETE FROM iteme_extra WHERE item_comanda_id IN (SELECT id FROM iteme_comanda WHERE comanda_id=?)";
        String stergeIteme = "DELETE from iteme_comanda WHERE comanda_id=?";

        try (PreparedStatement psFara = connection.prepareStatement(stergeFara)) {
            psFara.setInt(1, cmdId);
            psFara.executeUpdate();
        }
        try (PreparedStatement psExtra = connection.prepareStatement(stergeExtra)) {
            psExtra.setInt(1, cmdId);
            psExtra.executeUpdate();
        }
        try (PreparedStatement psIteme = connection.prepareStatement(stergeIteme)) {
            psIteme.setInt(1, cmdId);
            psIteme.executeUpdate();
        }
    }

    private Integer gasesteIdAdresa(Adresa adr) {
        String sql = "SELECT id FROM adrese WHERE strada=? AND numar=? AND oras=? AND cod_postal=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String strada = adr.strada();
            int nr = adr.numar();
            String oras = adr.oras();
            String codPostal = adr.codPostal();
            ps.setString(1, strada);
            ps.setInt(2, nr);
            ps.setString(3, oras);
            ps.setString(4, codPostal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int adrId = rs.getInt("id");
                    return adrId;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea id-ului adresei: ", e);
        }
        return null;
    }

    private List<Ingredient> citesteIngredienteProdus(int prodId) {
        String sql = "SELECT * FROM ingrediente WHERE produs_id = ?";
        List<Ingredient> ingrediente = new ArrayList<Ingredient>();

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

                    boolean optional;
                    if (rs.getInt("optional") == 1) {
                        optional = true;
                    } else {
                      optional = false;
                    }

                    Ingredient i = new Ingredient(nume, alergen, optional);
                    ingrediente.add(i);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingr. produsului: ", e);
        }

        return ingrediente;
    }

    private List<IngredientExtra> citesteExtraProdus(int prodId) {
        String sql = "SELECT * FROM extra WHERE produs_id = ?";
        List<IngredientExtra> extra = new ArrayList<IngredientExtra>();

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

                    Ingredient i = new Ingredient(nume, alergen, false); // un ingredient de baza al unui
                                                                                 // ingredient extra nu este niciodata
                                                                                 // optional
                    IngredientExtra ie = new IngredientExtra(i, pret);
                    extra.add(ie);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingredientelor extra din produs: ", e);
        }

        return extra;

    }

    private Produs gasesteProdusDupaId(int prodId) {
        String sql = "SELECT * FROM produse WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prodId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nume = rs.getString("nume");
                    String cat = rs.getString("categorie");
                    double pret = rs.getDouble("pret");
                    int cal = rs.getInt("calorii");
                    double proteine = rs.getDouble("proteine");
                    double carbo = rs.getDouble("carbohidrati");
                    double grasimi = rs.getDouble("grasimi");
                    int restId = rs.getInt("restaurant_id");

                    ValoriNutritionale valNut = new ValoriNutritionale(cal, proteine, carbo, grasimi);
                    Produs p = new Produs(prodId, restId, nume, cat, pret, valNut);
                    List<Ingredient> ingrs = citesteIngredienteProdus(prodId);
                    for (Ingredient ingr : ingrs) {
                        p.adaugaIngredient(ingr);
                    }
                    List<IngredientExtra> ingrsExtr = citesteExtraProdus(prodId);
                    for (IngredientExtra ingrExtr : ingrsExtr) {
                        p.adaugaIngredientExtraDisponibil(ingrExtr);
                    }

                    return p;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea produsului dupa id: ", e);
        }
        return null;
    }

    private double calculeazaTotalProduse(Comanda c) {
        double total = 0;

        for (Map.Entry<ProdusPersonalizat, Integer> item : c.getProduse().entrySet()) {
            ProdusPersonalizat pp = item.getKey();
            Integer cant = item.getValue();
            total += pp.calculeazaPretTotal() * cant;
        }

        return total;
    }

    @Override
    public void update(Comanda c) {
        String findAdr = "SELECT id FROM adrese WHERE strada = ? AND numar = ? AND oras = ? AND cod_postal = ?";
        String insertAdr = "INSERT INTO adrese(strada, numar, oras, cod_postal) VALUES (?, ?, ?, ?)";
        String sql = "UPDATE comenzi SET client_id=?, restaurant_id=?, adresa_id=?, sofer_id=?, status=?, metoda_plata=?, total_plata=? where id = ?";
        try {
            Integer adrId = null;
            Adresa adr = c.getAdresaLivrare();

            // ia id-ul adresei la care a fost livrata comanda
            try (PreparedStatement psAdr = connection.prepareStatement(findAdr)) {
                String strada = adr.strada();
                int nr = adr.numar();
                String oras = adr.oras();
                String codPostal = adr.codPostal();

                psAdr.setString(1, strada);
                psAdr.setInt(2, nr);
                psAdr.setString(3, oras);
                psAdr.setString(4, codPostal);

                try (ResultSet rsAdr = psAdr.executeQuery()) {
                    if (rsAdr.next()) {
                        adrId = rsAdr.getInt("id");
                    }
                }
            }

            // daca nu a fost gasita o adresa o inseram
            if (adrId == null) {
                try (PreparedStatement psAdr = connection.prepareStatement(insertAdr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    String strada = adr.strada();
                    int nr = adr.numar();
                    String oras = adr.oras();
                    String codPostal = adr.codPostal();

                    psAdr.setString(1, strada);
                    psAdr.setInt(2, nr);
                    psAdr.setString(3, oras);
                    psAdr.setString(4, codPostal);
                    psAdr.executeUpdate();
                    try (ResultSet rsAdr = psAdr.getGeneratedKeys()) {
                        if (rsAdr.next()) {
                            adrId = rsAdr.getInt(1);
                        }
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int clientId = c.getClient().getId();
                int restId = c.getRestaurant().getId();
                String statusStr = c.getStatus().name();
                String metStr = c.getMetodaPlata().name();
                double total = calculeazaTotalProduse(c);
                int cmdId = c.getId();

                ps.setInt(1, clientId);
                ps.setInt(2, restId);
                ps.setInt(3, adrId);
                if (c.areSoferAsignat()) {
                    int soferId = c.getSoferAsignat().getId();
                    ps.setInt(4, soferId);
                }
                else {
                    ps.setObject(4, null);
                }
                ps.setString(5, statusStr);
                ps.setString(6, metStr);
                ps.setDouble(7, total);
                ps.setInt(8, cmdId);
                ps.executeUpdate();
            }

            int cmdId = c.getId();
            stergeProduseComanda(cmdId);
            salveazaItemeComanda(c, cmdId);
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea unei comenzi: ", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String stergeReview = "DELETE FROM reviewuri WHERE comanda_id = ?";
        String sql = "DELETE from comenzi WHERE id = ?";
        try {
            // mai intai sterge review-ul comenzii caci review-ul depinde de comanda
            try (PreparedStatement psRev = connection.prepareStatement(stergeReview)) {
                psRev.setInt(1, id);
                psRev.executeUpdate();
            }

            stergeProduseComanda(id);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea de comanda: ", e);
        }
    }

    private List<Ingredient> citesteItemeFara(int itemId) {
        String sql = "SELECT i.nume, i.alergen, i.optional FROM iteme_fara f JOIN ingrediente i ON f.ingredient_id=i.id WHERE f.item_comanda_id = ?";

        List<Ingredient> ingrs = new ArrayList<Ingredient>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);

            try(ResultSet rs = ps.executeQuery()) {
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
            throw new RuntimeException("Eroare la citirea ingredientelor eliminate din item: ", e);
        }

        return ingrs;
    }

    private Map<IngredientExtra, Integer> citesteItemeExtra(int itemId) {
        String sql = "SELECT e.nume, e.alergen, e.pret, ie.cantitate FROM iteme_extra ie JOIN extra e ON ie.extra_id=e.id WHERE ie.item_comanda_id=?";

        Map<IngredientExtra, Integer> extra = new HashMap<IngredientExtra, Integer>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nume = rs.getString("nume");
                    double pret = rs.getDouble("pret");
                    int cant = rs.getInt("cantitate");
                    boolean alergen;
                    if (rs.getInt("alergen") == 1) {
                        alergen = true;
                    }
                    else {
                        alergen = false;
                    }

                    Ingredient i = new Ingredient(nume, alergen, false);
                    IngredientExtra ie = new IngredientExtra(i, pret);
                    extra.put(ie, cant);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea ingr. extra din item: ", e);
        }

        return extra;
    }

    private Map<ProdusPersonalizat, Integer> citesteProduseComanda(int cmdId) {
        String sql = "SELECT * FROM iteme_comanda WHERE comanda_id=?";
        Map<ProdusPersonalizat, Integer> produse = new HashMap<ProdusPersonalizat, Integer>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cmdId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("id");
                    int prodId = rs.getInt("produs_id");
                    int cant = rs.getInt("cantitate");

                    Produs p = gasesteProdusDupaId(prodId);
                    ProdusPersonalizat pp = new ProdusPersonalizat(p);

                    List<Ingredient> ingrFara = citesteItemeFara(itemId);
                    for (Ingredient i : ingrFara) {
                        pp.eliminaIngredientOptional(i);
                    }

                    Map<IngredientExtra, Integer> extrauri = citesteItemeExtra(itemId);
                    for (Map.Entry<IngredientExtra, Integer> elem : extrauri.entrySet()) {
                        IngredientExtra ie = elem.getKey();
                        Integer cantExtr = elem.getValue();
                        pp.adaugaExtra(ie, cantExtr);
                    }
                    produse.put(pp, cant);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea produselor din comanda: ", e);
        }
        return produse;
    }

    private Adresa citesteAdresaDupaId(int adrId) {
        String sql = "SELECT * FROM adrese WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, adrId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String strada = rs.getString("strada");
                    int nr = rs.getInt("numar");
                    String oras = rs.getString("oras");
                    String codPostal = rs.getString("cod_postal");

                    Adresa adr = new Adresa(strada, nr, oras, codPostal);
                    return adr;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea adresei dupa id: ", e);
        }
        return null;
    }

    // join pe comenzi, restaurante, soferi, reviewuri
    public List<String> getDetaliiIstoricComenziClient(int clientId) {
        String sql = """
                SELECT c.id AS comanda_id,
                r.nume AS restaurant_nume,
                s.nume AS sofer_nume,
                rv.rating AS review_rating,
                rv.comentariu AS review_comentariu,
                c.status AS status
                FROM comenzi c
                JOIN restaurante r ON r.id=c.restaurant_id
                LEFT JOIN soferi s ON s.id=c.sofer_id
                LEFT JOIN reviewuri rv ON c.id=rv.comanda_id
                WHERE c.client_id=? AND c.status=?
                ORDER BY c.id
                """;
        List<String> linii = new ArrayList<String>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setString(2, StatusComanda.FINALIZATA.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCmd = rs.getInt("comanda_id");
                    String numeRest = rs.getString("restaurant_nume");
                    String numeSofer = rs.getString("sofer_nume");


                    Integer rating = (Integer) rs.getObject("review_rating");
                    String comentariu = rs.getString("review_comentariu");
                    String reviewStr;
                    if (rating != null) {
                        reviewStr = "rating: " + rating + ", comentariu: " + comentariu;
                    }
                    else {
                        reviewStr = "nu are review";
                    }

                    String status = rs.getString("status");

                    String soferStr;
                    if (numeSofer != null) {
                        soferStr = numeSofer;
                    }
                    else {
                        soferStr = "nu are sofer";
                    }

                    String linie = "Comanda nr. " + idCmd + ", restaurant='" + numeRest + "', sofer='" + soferStr + "', status=" + status + ", review={" + reviewStr + "}";
                    linii.add(linie);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea istoricului comenzilor ale clientului: ", e);
        }
        return linii;
    }

    // join pe comenzi, clienti, restaurante, adrese
    public List<String> getDetaliiComenziDisponibile() {
        String sql = """
                SELECT c.id AS comanda_id,
                cl.nume AS client_nume,
                r.nume AS restaurant_nume,
                a.strada AS strada,
                a.numar AS numar,
                a.oras AS oras,
                a.cod_postal AS cod_postal,
                c.status AS status
                FROM comenzi c
                JOIN clienti cl ON cl.id=c.client_id
                JOIN restaurante r ON r.id=c.restaurant_id
                JOIN adrese a ON a.id=c.adresa_id
                WHERE c.status=? AND c.sofer_id IS NULL
                ORDER BY c.id
                """;

        List<String> linii = new ArrayList<String>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, StatusComanda.NEPRELUATA.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCmd = rs.getInt("comanda_id");
                    String numeClient = rs.getString("client_nume");
                    String numeRest = rs.getString("restaurant_nume");
                    String strada = rs.getString("strada");
                    int nr = rs.getInt("numar");
                    String oras = rs.getString("oras");
                    String codPostal = rs.getString("cod_postal");
                    String status = rs.getString("status");

                    String linie = "Comanda nr. " + idCmd + ", client='" + numeClient + "', restaurant='" + numeRest + "', adresa='" + strada + " nr. " + nr + ", " + oras + ", " + codPostal + "', status=" + status;
                    linii.add(linie);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea comenzilor disponibile pt livrare: ", e);
        }
        return linii;
    }

    private Comanda buildComanda(ResultSet rs) throws SQLException {
        int idCmd = rs.getInt("id");
        int idClient = rs.getInt("client_id");
        int idRest = rs.getInt("restaurant_id");
        int idAdr = rs.getInt("adresa_id");
        int idSofer = rs.getInt("sofer_id");
        boolean areSofer = !rs.wasNull();

        String statusStr = rs.getString("status");
        String mpStr = rs.getString("metoda_plata");
        Optional<Client> clientGasit = clientRepository.findById(idClient);
        if (clientGasit.isEmpty()) {
            throw new RuntimeException("Clientul comenzii nu exista in baza de date!");
        }
        Client cl = clientGasit.get();

        Optional<Restaurant> restGasit = restaurantRepository.findById(idRest);
        if (restGasit.isEmpty()) {
            throw new RuntimeException("Restaurantul comenzii nu exista in baza de date!");
        }
        Restaurant r = restGasit.get();

        Adresa adr = citesteAdresaDupaId(idAdr);
        Sofer s = null;
        if (areSofer) {
            Optional<Sofer> soferGasit = soferRepository.findById(idSofer);
            if (soferGasit.isEmpty()) {
                throw new RuntimeException("Soferul comenzii nu exista in baza de date!");
            }
            s = soferGasit.get();
        }

        Map<ProdusPersonalizat, Integer> produse = citesteProduseComanda(idCmd);
        StatusComanda status = StatusComanda.valueOf(statusStr);
        MetodaPlata mp = MetodaPlata.valueOf(mpStr);
        Comanda c = new Comanda(idCmd, cl, r, adr, produse, mp, status, s);
        return c;
    }
}
