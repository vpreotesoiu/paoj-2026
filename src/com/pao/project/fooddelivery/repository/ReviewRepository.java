package com.pao.project.fooddelivery.repository;

import com.pao.project.fooddelivery.model.Review;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewRepository implements Repository<Review, Integer> {
    private Connection connection;

    public ReviewRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Review r) {
        String sql = "INSERT INTO reviewuri(comanda_id, sofer_id, rating, comentariu) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int cmdId = r.getComandaId();
            int soferId = r.getSoferId();
            int rating = r.getRating();
            String comentariu = r.getComentariu();
            ps.setInt(1, cmdId);
            ps.setInt(2, soferId);
            ps.setInt(3, rating);
            ps.setString(4, comentariu);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idRev = rs.getInt(1);
                    r.setId(idRev);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea reviewului in baza de date: ", e);
        }
    }

    @Override
    public Optional<Review> findById(Integer id) {
        String sql = "SELECT * FROM reviewuri WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Review r = buildReview(rs);
                    return Optional.of(r);
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea reviewului dupa id: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM reviewuri";
        List<Review> rvs = new ArrayList<Review>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = buildReview(rs);
                    rvs.add(r);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea reviewurilor: ", e);
        }

        return rvs;
    }

    @Override
    public void update(Review r) {
        String sql = "UPDATE reviewuri SET comanda_id=?, sofer_id=?, rating=?, comentariu=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int cmdId = r.getComandaId();
            int soferId = r.getSoferId();
            int rating = r.getRating();
            String comentariu = r.getComentariu();
            int idRev = r.getId();
            ps.setInt(1, cmdId);
            ps.setInt(2, soferId);
            ps.setInt(3, rating);
            ps.setString(4, comentariu);
            ps.setInt(5, idRev);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea reviewului: ", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM reviewuri WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea reviewului: ", e);
        }
    }

    public Optional<Review> findByComandaId(int cmdId) {
        String sql = "SELECT * FROM reviewuri WHERE comanda_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cmdId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Review r = buildReview(rs);
                    return Optional.of(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea review-ului dupa comanda: ", e);
        }
        return Optional.empty();
    }

    public List<Review> findBySoferId(int soferId) {
        String sql = "SELECT * FROM reviewuri WHERE sofer_id=?";
        List<Review> rvs = new ArrayList<Review>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, soferId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = buildReview(rs);
                    rvs.add(r);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea reviewurilor unui sofer: ", e);
        }
        return rvs;
    }

    public boolean existsByComandaId(int cmdId) {
        String sql = "SELECT 1 FROM reviewuri WHERE comanda_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cmdId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la verificarea existentei unui review pentru o comanda: ", e);
        }
    }

    // join pe reviewuri, soferi
    public List<String> getMedieRatingSoferi() {
        String sql = """
                SELECT s.id AS sofer_id,
                s.nume AS sofer_nume,
                AVG(rv.rating) AS media_rating,
                COUNT(rv.id) AS nr_reviewuri
                FROM reviewuri rv
                JOIN soferi s ON s.id=rv.sofer_id
                GROUP BY rv.sofer_id, s.nume, s.id
                ORDER BY s.id
                """;
        List<String> linii = new ArrayList<String>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idSofer = rs.getInt("sofer_id");
                    String numeSof = rs.getString("sofer_nume");
                    double media = rs.getDouble("media_rating");
                    int nrRev = rs.getInt("nr_reviewuri");
                    String linie = "Sofer nr. " + idSofer + ", nume='" + numeSof + "', media_rating=" + media + ", nr_reviewuri=" + nrRev;
                    linii.add(linie);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea mediei ratingului pentru soferi: ", e);
        }
        return linii;
    }

    private Review buildReview(ResultSet rs) throws SQLException {
        int idRev = rs.getInt("id");
        int cmdId = rs.getInt("comanda_id");
        int soferId = rs.getInt("sofer_id");
        int rating = rs.getInt("rating");
        String comentariu = rs.getString("comentariu");

        Review r = new Review(idRev, cmdId, soferId, rating, comentariu);
        return r;
    }
}
