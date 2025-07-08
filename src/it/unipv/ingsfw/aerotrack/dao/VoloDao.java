package it.unipv.ingsfw.aerotrack.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * DAO Singleton per la gestione dei voli nel database MySQL.
 * Implementa IVoloDao.
 * Gestisce tutte le operazioni CRUD sui voli.
 */
public class VoloDao implements IVoloDao {
    private static VoloDao instance;
    private final AeroportoDao aeroportoDao = AeroportoDao.getInstance();

    private VoloDao() {}

    public static VoloDao getInstance() {
        if (instance == null) {
            instance = new VoloDao();
        }
        return instance;
    }

    /**
     * Aggiunge o aggiorna un volo.
     * 
     * @param v Volo da aggiungere/aggiornare.
     * @return true se riuscita, false altrimenti.
     */
    @Override
    public boolean aggiungiVolo(Volo v) {
        if (v == null) throw new IllegalArgumentException("Il volo non può essere null");
        String insertQuery = """
            INSERT INTO voli (codice, partenza, destinazione, orario_partenza, velocita, pista_assegnata, ritardo, stato)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                partenza = VALUES(partenza),
                destinazione = VALUES(destinazione),
                orario_partenza = VALUES(orario_partenza),
                velocita = VALUES(velocita),
                pista_assegnata = VALUES(pista_assegnata),
                ritardo = VALUES(ritardo),
                stato = VALUES(stato)
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, v.getCodice());
            ps.setString(2, v.getPartenza().getCodice());
            ps.setString(3, v.getDestinazione().getCodice());
            ps.setDouble(4, v.getOrarioPartenza());
            ps.setDouble(5, v.getVelocita());
            ps.setInt(6, v.getPistaAssegnata() + 1);
            ps.setDouble(7, v.getRitardo());
            ps.setString(8, v.getStato() != null ? v.getStato().name() : "PROGRAMMATO");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore inserimento volo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restituisce tutti i voli nel database.
     * 
     * @return lista dei voli.
     */
    @Override
    public List<Volo> getTuttiVoli() {
        List<Volo> listaVoli = new ArrayList<>();
     // 1. Carica tutti gli aeroporti una sola volta in una mappa
        Map<String, Aeroporto> aeroporti = new HashMap<>();
        for (Aeroporto a : aeroportoDao.getTuttiAeroporti()) {
            aeroporti.put(a.getCodice(), a);
        } 
        
        String query = "SELECT * FROM voli";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
            	Aeroporto partenza = aeroporti.get(rs.getString("partenza"));
                Aeroporto destinazione = aeroporti.get(rs.getString("destinazione"));
                if (partenza != null && destinazione != null) {
                    Volo v = new Volo(
                            rs.getString("codice"),
                            partenza,
                            destinazione,
                            rs.getDouble("orario_partenza"),
                            rs.getDouble("velocita"),
                            rs.getInt("pista_assegnata"),
                            rs.getDouble("ritardo"),
                            Volo.StatoVolo.valueOf(rs.getString("stato"))
                        
                    );
                    // --- Ricostruisci le relazioni ---
                    partenza.aggiungiVoloInPartenza(v);
                    destinazione.aggiungiVoloInArrivo(v);
                    int pista = rs.getInt("pista_assegnata");
                    if (pista >= 0 && pista < partenza.getNumeroPiste()) {
                        try {
                            partenza.occupaPista(pista, v);
                        } catch (Exception ignore) {} // pista già occupata da un altro volo con stessa pista? ignora
                    }
                    listaVoli.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero voli: " + e.getMessage());
        }
        return listaVoli;
    }

    /**
     * Cerca un volo tramite codice.
     * 
     * @param codice Codice volo.
     * @return Volo trovato, null se non esiste.
     */
    @Override
    public Volo cercaPerCodice(String codice) {
        if (codice == null || codice.isEmpty())
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        String selectQuery = "SELECT * FROM voli WHERE codice = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setString(1, codice.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
                Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
                if (partenza != null && destinazione != null) {
                	Volo v = new Volo (
                			rs.getString("codice"),
                            partenza,
                            destinazione,
                            rs.getDouble("orario_partenza"),
                            rs.getDouble("velocita"),
                            rs.getInt("pista_assegnata"),
                            rs.getDouble("ritardo"),
                            Volo.StatoVolo.valueOf(rs.getString("stato"))
                        );
                        return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore ricerca volo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Rimuove un volo tramite codice.
     * 
     * @param codice Codice volo.
     * @return true se rimosso, false altrimenti.
     */
    @Override
    public boolean rimuoviVolo(String codice) {
        if (codice == null || codice.isEmpty())
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        String delQuery = "DELETE FROM voli WHERE codice = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(delQuery)) {
            ps.setString(1, codice.toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore rimozione volo: " + e.getMessage());
        }
        return false;
    }
}