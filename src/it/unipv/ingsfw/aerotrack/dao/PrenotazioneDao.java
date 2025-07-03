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
        if (prenotazione == null) throw new IllegalArgumentException("La prenotazione non può essere null");
        String insertQuery = """
            INSERT INTO prenotazioni (codice_prenotazione, nome_passeggero, cognome_passeggero,
             documento_passeggero, codice_volo, cancellata)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DBConnection.startConnection("aerotrack");
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            String[] nomeCognome = prenotazione.getPasseggero().getNomeCompleto().split(" ");
            ps.setString(1, prenotazione.getCodicePrenotazione());
            ps.setString(2, nomeCognome[0]);
            ps.setString(3, nomeCognome.length > 1 ? nomeCognome[1] : "");
            ps.setString(4, prenotazione.getPasseggero().getDocumento());
            ps.setString(5, prenotazione.getVolo() != null ? prenotazione.getVolo().getCodice() : null);
            ps.setBoolean(6, prenotazione.isCancellata());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore inserimento prenotazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restituisce tutte le prenotazioni.
     * 
     * @return lista delle prenotazioni.
     */
    @Override
    public List<Prenotazione> getTuttePrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String query = "SELECT * FROM prenotazioni";
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Passeggero passeggero = new Passeggero(
                    rs.getString("nome_passeggero"),
                    rs.getString("cognome_passeggero"),
                    rs.getString("documento_passeggero")
                );
                Volo volo = null;
                try {
                    volo = voloDao.cercaPerCodice(rs.getString("codice_volo"));
                } catch (Exception ignored) {}
                Prenotazione prenotazione = new Prenotazione(passeggero, volo);
                if (rs.getBoolean("cancellata")) {
                    prenotazione.cancella();
                }
                prenotazioni.add(prenotazione);
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero prenotazioni: " + e.getMessage());
        }
        return prenotazioni;
    }
}