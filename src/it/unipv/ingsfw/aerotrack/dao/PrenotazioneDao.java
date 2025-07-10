package it.unipv.ingsfw.aerotrack.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * DAO Singleton per la gestione delle prenotazioni.
 * Implementa IPrenotazioneDao.  
 */
public class PrenotazioneDao implements IPrenotazioneDao {
    private static PrenotazioneDao instance;
    private final VoloDao voloDao = VoloDao.getInstance();
    private final PasseggeroDao passeggeroDao = PasseggeroDao.getInstance();

    private PrenotazioneDao() {}

    public static PrenotazioneDao getInstance() {
        if (instance == null) {
            instance = new PrenotazioneDao();
        }
        return instance;
    }

    /**
     * Aggiunge una nuova prenotazione.
     * 
     * @param prenotazione La prenotazione da aggiungere.
     * @return true se aggiunta, false altrimenti.
     */
    @Override
    public boolean aggiungiPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) 
        	throw new IllegalArgumentException("La prenotazione non può essere null");
        String insertQuery = """
            INSERT INTO prenotazioni (codice_prenotazione, documento_passeggero, codice_volo, cancellata)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, prenotazione.getCodicePrenotazione());
            ps.setString(2, prenotazione.getPasseggero().getDocumento());
            ps.setString(3, prenotazione.getVolo() != null ? prenotazione.getVolo().getCodice() : null);
            ps.setBoolean(4, prenotazione.isCancellata());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore inserimento prenotazione: " + e.getMessage());
            return false;
        }
    }
    
    public void aggiorna(Prenotazione p) throws SQLException {
    	try (Connection conn = DBConnection.startConnection("aerotrack");
    		     PreparedStatement stmt = conn.prepareStatement("UPDATE prenotazioni SET cancellata=? WHERE codice_prenotazione=?")) {
    		    stmt.setBoolean(1, p.isCancellata());
    		    stmt.setString(2, p.getCodicePrenotazione());
    		    stmt.executeUpdate();
    		};
    }

    /**
     * Restituisce tutte le prenotazioni.
     * 
     * @return lista delle prenotazioni.
     */
    @Override
    public List<Prenotazione> getTuttePrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        int maxCodiceNumerico = 1000;
        String query = "SELECT * FROM prenotazioni";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
            	Passeggero passeggero = passeggeroDao.cercaPerDocumento(rs.getString("documento_passeggero"));
                Volo volo = null;
                try {
                    volo = voloDao.cercaPerCodice(rs.getString("codice_volo"));
                } catch (Exception ignored) {}
                String codice = rs.getString("codice_prenotazione");
                boolean cancellata = rs.getBoolean("cancellata");
                prenotazioni.add(new Prenotazione(codice, passeggero, volo, cancellata));

                // aggiorna il massimo valore del codice
                if (codice != null && codice.startsWith("PR")) {
                    try {
                        int num = Integer.parseInt(codice.substring(2));
                        if (num >= maxCodiceNumerico) maxCodiceNumerico = num + 1;
                    } catch (NumberFormatException ignored) {}
                }
            }
            // aggiorna il contatore statico
            Prenotazione.aggiornaContatore(maxCodiceNumerico);
        } catch (SQLException e) {
            System.err.println("Errore recupero prenotazioni: " + e.getMessage());
        }
        return prenotazioni;
    }
}    