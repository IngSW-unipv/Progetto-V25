package it.unipv.ingsfw.aerotrack.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import it.unipv.ingsfw.aerotrack.models.*;


// Data Access Object per la gestione delle prenotazioni nel database.
 
public class PrenotazioneDao {
	
	private static PrenotazioneDao instance; 
	private Connection connection;
	private VoloDao voloDao;
    
	// Metodo per aggiungere una nuova prenotazione al database
    public boolean aggiungiPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) throw new IllegalArgumentException("La prenotazione non può essere null");
        String insertQuery = """
                INSERT INTO prenotazioni 
                (codice_prenotazione, nome_passeggero, cognome_passeggero, 
                 documento_passeggero, codice_volo, cancellata) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            String[] nomeCognome = prenotazione.getPasseggero().getNomeCompleto().split(" ");
            ps.setString(1, prenotazione.getCodicePrenotazione());
            ps.setString(2, nomeCognome[0]);
            ps.setString(3, nomeCognome.length > 1 ? nomeCognome[1] : "");
            ps.setString(4, prenotazione.getPasseggero().getDocumento());
            ps.setString(5, prenotazione.getVolo().getCodice());
            ps.setBoolean(6, prenotazione.isCancellata());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        
       
    // Restituisce tutte le prenotazioni dal database (sia attive che cancellate).
    public List<Prenotazione> getTuttePrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String query = "SELECT * FROM prenotazioni";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Passeggero passeggero = new Passeggero(
                    rs.getString("nome_passeggero"),
                    rs.getString("cognome_passeggero"),
                    rs.getString("documento_passeggero")
                );
                Volo volo = voloDao.cercaPerCodice(rs.getString("codice_volo"));
                if (volo != null) {
                    Prenotazione prenotazione = new Prenotazione(passeggero, volo);
                    if (rs.getBoolean("cancellata")) {
                        prenotazione.cancella();
                    }
                    prenotazioni.add(prenotazione);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prenotazioni;
    }  
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


