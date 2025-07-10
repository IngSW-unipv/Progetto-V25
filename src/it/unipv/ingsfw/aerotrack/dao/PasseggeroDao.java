package it.unipv.ingsfw.aerotrack.dao;

import it.unipv.ingsfw.aerotrack.models.Passeggero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Singleton per la gestione dei passeggeri.
 * Implementa IPasseggeroDao.
 */
public class PasseggeroDao implements IPasseggeroDao {
    private static PasseggeroDao instance;

    private PasseggeroDao() {}

    public static PasseggeroDao getInstance() {
        if (instance == null) {
            instance = new PasseggeroDao();
        }
        return instance;
    }

    @Override
    public boolean aggiungiPasseggero(Passeggero passeggero) {
        if (passeggero == null) throw new IllegalArgumentException("Il passeggero non può essere null");
        String insertQuery = """
            INSERT INTO passeggeri (documento, nome, cognome)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, passeggero.getDocumento());
            ps.setString(2, passeggero.getNome());
            ps.setString(3, passeggero.getCognome());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
        	// Se già esistente, aggiorna!
            return aggiornaPasseggero(passeggero);
        }
    }
    
    /**
     * Aggiorna nome e cognome di un passeggero esistente.
     */
    public boolean aggiornaPasseggero(Passeggero passeggero) {
        String updateQuery = """
            UPDATE passeggeri SET nome = ?, cognome = ?
            WHERE documento = ?
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setString(1, passeggero.getNome());
            ps.setString(2, passeggero.getCognome());
            ps.setString(3, passeggero.getDocumento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento passeggero: " + e.getMessage());
            return false;
        }
    }


    @Override
    public Passeggero cercaPerDocumento(String documento) {
        String query = "SELECT * FROM passeggeri WHERE documento = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Passeggero(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("documento")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore ricerca passeggero: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Passeggero> getTuttiPasseggeri() {
        List<Passeggero> passeggeri = new ArrayList<>();
        String query = "SELECT * FROM passeggeri";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                passeggeri.add(new Passeggero(
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("documento")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero passeggeri: " + e.getMessage());
        }
        return passeggeri;
    }
}