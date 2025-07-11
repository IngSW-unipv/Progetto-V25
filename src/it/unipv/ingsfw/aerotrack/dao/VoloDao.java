package it.unipv.ingsfw.aerotrack.dao;

import java.sql.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Time;
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
                INSERT INTO voli (codice, partenza, destinazione, orario_partenza, velocita, pista_assegnata, ritardo, stato, data_volo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    partenza = VALUES(partenza),
                    destinazione = VALUES(destinazione),
                    orario_partenza = VALUES(orario_partenza),
                    velocita = VALUES(velocita),
                    pista_assegnata = VALUES(pista_assegnata),
                    ritardo = VALUES(ritardo),
                    stato = VALUES(stato),
                    data_volo = VALUES(data_volo)
                """;
            try (Connection conn = DBConnection.startConnection("aerotrack");
                 PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            	ps.setString(1, v.getCodice());
            	ps.setString(2, v.getPartenza().getCodice());
            	ps.setString(3, v.getDestinazione().getCodice());
            	ps.setTime(4, java.sql.Time.valueOf(v.getOrarioPartenza()));
            	ps.setDouble(5, v.getVelocita());
            	ps.setInt(6, v.getPistaAssegnata() + 1);
            	ps.setTime(7, java.sql.Time.valueOf(v.getRitardo())); 
            	ps.setString(8, v.getStato() != null ? v.getStato().name() : "PROGRAMMATO");
            	ps.setDate(9, java.sql.Date.valueOf(v.getDataVolo()));
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
        // Carica tutti gli aeroporti una sola volta in una mappa
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
                	LocalDate dataVolo = rs.getDate("data_volo").toLocalDate();
                	LocalTime orarioPartenza = rs.getTime("orario_partenza").toLocalTime();

                	int pista = rs.getInt("pista_assegnata");
                	Volo v = new Volo(
                		    rs.getString("codice"),
                		    partenza,
                		    destinazione,
                		    orarioPartenza,
                		    rs.getDouble("velocita"),
                		    dataVolo
                		);
                		v.setPistaAssegnata(rs.getInt("pista_assegnata"));

                		Time ritardoDb = rs.getTime("ritardo");
                		if (ritardoDb != null) {
                		    v.setRitardo(ritardoDb.toLocalTime());
                		} else {
                		    v.setRitardo(LocalTime.of(0, 0));
                		}

                		String statoDb = rs.getString("stato");
                		if (statoDb != null) {
                		    v.setStato(Volo.StatoVolo.valueOf(statoDb));
                		} else {
                		    v.setStato(Volo.StatoVolo.PROGRAMMATO);
                		}

                		listaVoli.add(v);

                }
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero voli: " + e.getMessage());
        }
        return listaVoli;
    }

    public boolean aggiornaVolo(Volo volo) {
        String query = "UPDATE voli SET ritardo = ?, stato = ?, data_volo = ? WHERE codice = ?";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTime(1, java.sql.Time.valueOf(volo.getRitardo()));
            stmt.setString(2, volo.getStato().name());
            stmt.setDate(3, Date.valueOf(volo.getDataVolo()));
            stmt.setString(4, volo.getCodice());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
                            rs.getTime("orario_partenza").toLocalTime(),
                            rs.getDouble("velocita"),
                            rs.getDate("data_volo").toLocalDate()
                        );
                	v.setPistaAssegnata(rs.getInt("pista_assegnata"));
                    v.setRitardo(rs.getTime("ritardo").toLocalTime());
                    v.setStato(Volo.StatoVolo.valueOf(rs.getString("stato")));
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