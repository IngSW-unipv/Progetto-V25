package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Data Access Object per la gestione degli aeroporti nel database MySQL.
 * Gestisce tutte le operazioni CRUD (Create, Read, Update, Delete) sugli aeroporti.
 * Implementa IAeroportoDao.
 */
public class AeroportoDao implements IAeroportoDao {
	
	private static AeroportoDao instance;

    private AeroportoDao() {}

    public static AeroportoDao getInstance() {
        if (instance == null) instance = new AeroportoDao();
        return instance;
    }

	/**
     * Aggiunge o aggiorna un aeroporto.
     * 
     * @param a Aeroporto da aggiungere/aggiornare.
     */
    @Override
    public void aggiungiAeroporto(Aeroporto a) {
        if (a == null) throw new IllegalArgumentException("L'aeroporto non può essere null");
        String insertQuery = """
            INSERT INTO aeroporti (codice, nome, latitudine, longitudine, numeroPiste)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nome = VALUES(nome),
                latitudine = VALUES(latitudine),
                longitudine = VALUES(longitudine),
                numeroPiste = VALUES(numeroPiste)
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, a.getCodice());
            ps.setString(2, a.getNome());
            ps.setDouble(3, a.getLatitudine());
            ps.setDouble(4, a.getLongitudine());
            ps.setInt(5, a.getNumeroPiste());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore inserimento aeroporto: " + e.getMessage());
        }
    }

    /**
     * Restituisce tutti gli aeroporti nel database.
     * 
     * @return lista di Aeroporti.
     */
    @Override
    public List<Aeroporto> getTuttiAeroporti() {
        List<Aeroporto> listaAeroporti = new ArrayList<>();
        String query = "SELECT * FROM aeroporti ORDER BY codice";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aeroporto a = new Aeroporto(
                        rs.getString("codice"),
                        rs.getString("nome"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getInt("numeroPiste"));
                listaAeroporti.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero aeroporti: " + e.getMessage());
        }
        return listaAeroporti;
    }

    /**
     * Cerca un aeroporto tramite codice.
     * 
     * @param codice Codice IATA.
     * @return Aeroporto trovato, null se non esiste.
     */
    @Override
    public Aeroporto cercaPerCodice(String codice) {
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Il codice aeroporto non può essere null o vuoto");
        }
        String selectQuery = "SELECT * FROM aeroporti WHERE codice = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setString(1, codice.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Aeroporto(
                        rs.getString("codice"),
                        rs.getString("nome"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getInt("numeroPiste"));
            }
        } catch (SQLException e) {
            System.err.println("Errore ricerca aeroporto: " + e.getMessage());
        }
        return null;
    }

    /**
     * Rimuove un aeroporto tramite codice.
     * 
     * @param codice Codice aeroporto.
     * @return true se rimosso, false altrimenti.
     */
    @Override
    public boolean rimuoviAeroporto(String codice) {
        if (codice == null || codice.isEmpty())
            throw new IllegalArgumentException("Il codice aeroporto non può essere null o vuoto");
        String delQuery = "DELETE FROM aeroporti WHERE codice = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(delQuery)) {
            ps.setString(1, codice.toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione dell'aeroporto: " + e.getMessage());
        }
        return false;
    }
}
	
	