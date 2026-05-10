package com.pao.project.fooddelivery.repository;

import com.pao.project.fooddelivery.model.Sofer;
import com.pao.project.fooddelivery.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoferRepository implements Repository<Sofer, Integer> {
    private Connection connection;

    public SoferRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Sofer s) {
        String sql = "INSERT INTO soferi(nume, email, parola, balanta, disponibil) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            String nume = s.getNume();
            String email = s.getEmail();
            String parola = s.getParola();
            double bal = s.getBalanta();
            boolean disp = s.isDisponibil();

            ps.setString(1, nume);
            ps.setString(2, email);
            ps.setString(3, parola);
            ps.setDouble(4, bal);
            if (disp) {
                ps.setInt(5, 1);
            }
            else {
                ps.setInt(5, 0);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int soferId = rs.getInt(1);
                    s.setId(soferId);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea soferului in baza de date: ", e);
        }
    }

    @Override
    public Optional<Sofer> findById(Integer id) {
        String sql = "SELECT * FROM soferi WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idSofer = id;
            ps.setInt(1, idSofer);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sofer s = buildSofer(rs);
                    return Optional.of(s);
                }
            }

        }catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea soferului dupa id: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Sofer> findAll() {
        String sql = "SELECT * FROM soferi";
        List<Sofer> soferi = new ArrayList<Sofer>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sofer s = buildSofer(rs);
                    soferi.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citire de soferi: ", e);
        }
        return soferi;
    }

    public Optional<Sofer> findByEmail(String email) {
        String sql = "SELECT * FROM soferi where email=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sofer s = buildSofer(rs);
                    return Optional.of(s);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea soferului dupa mail: ", e);
        }
        return Optional.empty();
    }

    public boolean existsEmail(String email) {
        String sql = "SELECT 1 FROM soferi WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea email-ului daca exista: ", e);
        }
    }

    @Override
    public void update(Sofer s) {
        String sql = "UPDATE soferi SET nume=?, email=?, parola=?, balanta=?, disponibil=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String nume = s.getNume();
            String email = s.getEmail();
            String parola = s.getParola();
            double bal = s.getBalanta();
            boolean disp = s.isDisponibil();
            int idSofer = s.getId();
            ps.setString(1, nume);
            ps.setString(2, email);
            ps.setString(3, parola);
            ps.setDouble(4, bal);
            if (disp) {
                ps.setInt(5, 1);
            }
            else {
                ps.setInt(5, 0);
            }
            ps.setInt(6, idSofer);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea de sofer: ", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM soferi WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea soferului: ", e);
        }
    }

    private Sofer buildSofer(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nume = rs.getString("nume");
        String email = rs.getString("email");
        String parola = rs.getString("parola");
        double bal = rs.getDouble("balanta");
        boolean disp;
        if (rs.getInt("disponibil") == 1) {
            disp = true;
        } else {
            disp = false;
        }

        return new Sofer(id, nume, email, parola, bal, disp);
    }
}
