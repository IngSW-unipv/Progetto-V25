package it.unipv.ingsfw.aerotrack.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Data Access Object per la gestione delle prenotazioni nel database.
 */
public class PrenotazioneDao {
	
	private static PrenotazioneDao instance; 
	private Connection connection;
	private VoloDao voloDao;
	
	// Costruttore
	private PrenotazioneDao() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:aeroporti.db");
            voloDao = VoloDao.getInstance();
            
            // Crea la tabella "prenotazioni" se non esiste già
            Statement stmt = connection.createStatement();
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS prenotazioni (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    codice_prenotazione TEXT UNIQUE NOT NULL,
                    nome_passeggero TEXT NOT NULL,
                    cognome_passeggero TEXT NOT NULL,
                    documento_passeggero TEXT NOT NULL,
                    codice_volo TEXT NOT NULL,
                    cancellata BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY(codice_volo) REFERENCES voli(codice)
                )
                """;
            stmt.executeUpdate(createTableQuery);
            stmt.close();
            
        } catch (SQLException e) {
        	// Se c'è un errore nella creazione della tabella, lo stampa
            e.printStackTrace();
            throw new RuntimeException("Errore inizializzazione database prenotazioni", e);
        }
    }
	
	// Metodo per ottenere istanza della classe (Singleton)
	public static PrenotazioneDao getInstance() {
        if (instance == null) {
            instance = new PrenotazioneDao();
        }
        return instance;
    }
    
	// Metodo per aggiungere una nuova prenotazione al database
    public boolean aggiungiPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) {
            throw new IllegalArgumentException("La prenotazione non può essere null");
        }
        
        try {
        	// Query SQL per inserire una nuova prenotazione
            String insertQuery = """
                INSERT INTO prenotazioni 
                (codice_prenotazione, nome_passeggero, cognome_passeggero, 
                 documento_passeggero, codice_volo, cancellata) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setString(1, prenotazione.getCodicePrenotazione());
            ps.setString(2, prenotazione.getPasseggero().getNomeCompleto().split(" ")[0]);
            ps.setString(3, prenotazione.getPasseggero().getNomeCompleto().split(" ")[1]);
            ps.setString(4, prenotazione.getPasseggero().getDocumento());
            ps.setString(5, prenotazione.getVolo().getCodice());
            ps.setBoolean(6, prenotazione.isCancellata());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
        	// Stampa eventuali errori di SQL
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Prenotazione> getTuttePrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM prenotazioni");
            
            while (rs.next()) {
                String nomeCompleto = rs.getString("nome_passeggero") + " " + rs.getString("cognome_passeggero");
                String documento = rs.getString("documento_passeggero");
                String codiceVolo = rs.getString("codice_volo");
                boolean cancellata = rs.getBoolean("cancellata");
                
                Passeggero passeggero = new Passeggero(
                    rs.getString("nome_passeggero"), 
                    rs.getString("cognome_passeggero"), 
                    documento
                );
                
                Volo volo = voloDao.cercaPerCodice(codiceVolo);
                if (volo != null) {
                    Prenotazione prenotazione = new Prenotazione(passeggero, volo);
                    if (cancellata) {
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
}


