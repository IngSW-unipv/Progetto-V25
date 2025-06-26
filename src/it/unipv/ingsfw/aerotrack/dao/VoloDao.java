package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Data Access Object per la gestione dei voli nel database.
 * Implementa il pattern Singleton per garantire un'unica istanza della connessione.
 * Gestisce tutte le operazioni CRUD (Create, Read, Update, Delete) sui voli.
 * 
 * Questa classe si occupa di:
 * - Creare e mantenere la connessione al database SQLite
 * - Inserire nuovi voli nel database
 * - Recuperare voli esistenti
 * - Aggiornare informazioni sui voli
 * - Eliminare voli dal database 
 */
public class VoloDao {
	private static VoloDao instance;       // Istanza singleton della classe
	private Connection connection;         // Connessione al database SQLite
	private AeroportoDao aeroportoDao;     // Istanza del DAO degli aeroporti per recuperare i dati degli aeroporti
	
	
	/**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza la connessione al database e crea la tabella voli se non esiste, con foreign key verso aeroporti
     * 
     * La tabella voli contiene:
     * - codice: chiave primaria del volo (es. "AZ123")
     * - partenza: codice aeroporto di partenza (foreign key)
     * - destinazione: codice aeroporto di destinazione (foreign key)
     * - orario_partenza: orario programmato di partenza
     * - velocita: velocità media del volo in km/h
     * 
     * @throws RuntimeException se la connessione al database fallisce
     */
	private VoloDao() {
		try {
			 // Connessione al database SQLite 
			 connection = DriverManager.getConnection("jdbc:sqlite:aeroporti.db");
			
			 // Ottiene l'istanza del DAO aeroporti per le operazioni correlate 
			 aeroportoDao = AeroportoDao.getInstance();
	         
			 Statement stmt = connection.createStatement();    // Crea la tabella voli se non esiste già
			 
			 // Query SQL per creare la tabella con foreign key verso aeroporti
	         String createTableQuery = """
	             CREATE TABLE IF NOT EXISTS voli (
	                 codice TEXT PRIMARY KEY,
	                 partenza TEXT NOT NULL,
	                 destinazione TEXT NOT NULL,
	                 orario_partenza REAL DEFAULT 0.0,
	                 velocita REAL DEFAULT 800.0,
	                 FOREIGN KEY(partenza) REFERENCES aeroporti(codice),
	                 FOREIGN KEY(destinazione) REFERENCES aeroporti(codice)
	                )
	                """;
	         stmt.executeUpdate(createTableQuery);
	            
	            // Chiude lo statement dopo l'uso
	            stmt.close();
	            
	    } catch (SQLException e) {
	    	 // In caso di errore, stampa lo stack trace e lancia eccezione runtime
	         e.printStackTrace();      // Stampa l'errore nella console
	         throw new RuntimeException("Errore durante l'inizializzazione del database voli", e);
	    }
	}
	
	
	/**
     * Restituisce l'istanza singleton della classe VoloDao.
     * Se l'istanza non esiste ancora, la crea.
     * 
     * @return L'unica istanza di VoloDao
     */
	public static VoloDao getInstance() {
		if (instance == null) {
			instance = new VoloDao();
		}
		return instance;
	}
	
	
	/**
     * Metodo aggiungiVolo: aggiunge un nuovo volo al database.
     * Se esiste già un volo con lo stesso codice, lo sostituisce (INSERT OR REPLACE).
     * 
     * @param volo Il volo da aggiungere al database
     * @return true se l'operazione è riuscita, false altrimenti
     * @throws IllegalArgumentException se il volo è null
     */
	public boolean aggiungiVolo(Volo v) {
		// Validazione del parametro di input
        if (v == null) {
            throw new IllegalArgumentException("Il volo non può essere null");
        }
        
		try {
			 // Prepara la query SQL per inserire il volo
            String insertQuery = """
                INSERT OR REPLACE INTO voli 
                (codice, partenza, destinazione, orario_partenza, velocita) 
                VALUES (?, ?, ?, ?, ?)
                """;
            
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            
            // Imposta i parametri della query
            ps.setString(1, v.getCodice());                              // Codice volo
            ps.setString(2, v.getPartenza().getCodice());                // Aeroporto partenza
            ps.setString(3, v.getDestinazione().getCodice());            // Aeroporto destinazione
            ps.setDouble(4, v.getOrarioPartenza());                      // Orario partenza
            ps.setDouble(5, v.getVelocita());                            // Velocità
            
            // Esegue l'aggiornamento e verifica il risultato
            int rowsAffected = ps.executeUpdate();
            
            // Chiude il prepared statement
            ps.close();
            
            // Restituisce true se almeno una riga è stata modificata
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Stampa l'errore e restituisce false
            System.err.println("Errore durante l'inserimento del volo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
		
	
    /**
     * Metodo getTuttiVoli(): recupera tutti i voli esistenti
     *
     * @return Lista di tutti i voli nel database, vuota se non ci sono voli
     */
	public List<Volo> getTuttiVoli(){
		List<Volo> listaVoli = new ArrayList<>();
	    
		try {
			// Query per selezionare tutti i voli
			Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM voli");
	        // Itera sui risultati della query
	        while (rs.next()) {
	            // Recupera i dati del volo dal ResultSet
	        	 String codice = rs.getString("codice");
	             String codicePartenza = rs.getString("partenza");
	             String codiceDestinazione = rs.getString("destinazione");
	             double orarioPartenza = rs.getDouble("orario_partenza");
	             double velocita = rs.getDouble("velocita");
	             
	             // Carica gli aeroporti tramite il DAO degli aeroporti
	        	 Aeroporto partenza = aeroportoDao.cercaPerCodice(codicePartenza);
	             Aeroporto destinazione = aeroportoDao.cercaPerCodice(codiceDestinazione);
	             listaVoli.add(new Volo(codice, partenza, destinazione, orarioPartenza, velocita));
	        } 
	    } catch (SQLException e) {
	    	// Gestisce gli errori SQL
            System.err.println("Errore durante il recupero dei voli: " + e.getMessage());
	    	e.printStackTrace();
	    }
		
	    return listaVoli;
    }
	
	 /**
     *  Metodo cercaPerCodice(): per trovare un volo tramite il suo codice identificativo.
     * 
     * @param codice Il codice del volo da cercare (es. "AZ123")
     * @return Il volo trovato, null se non esiste
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
	public Volo cercaPerCodice(String codice) {
		// Validazione del parametro
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        }
        
		try {
			// Prepara la query con parametro
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM voli WHERE codice = ?"
			);
            ps.setString(1, codice.toUpperCase());
            
            // Esegue la query
            ResultSet rs = ps.executeQuery();
            
            // Se trova il volo
            if (rs.next()) {
            	// Recupera i dati
            	String codiceVolo = rs.getString("codice");
                String codicePartenza = rs.getString("partenza");
                String codiceDestinazione = rs.getString("destinazione");
                double orarioPartenza = rs.getDouble("orario_partenza");
                double velocita = rs.getDouble("velocita");
                 
                // Carica gli aeroporti
            	Aeroporto partenza = aeroportoDao.cercaPerCodice(codicePartenza);
                Aeroporto destinazione = aeroportoDao.cercaPerCodice(codiceDestinazione);
                
                return new Volo(codiceVolo, partenza, destinazione, orarioPartenza, velocita);
            }
        } catch (SQLException e) {
        	System.err.println("Errore durante la ricerca del volo: " + e.getMessage());
        	e.printStackTrace();
        }
        return null;
    }
	
	/**
     * Metodo rimuoviVolo(): rimuove un volo dal database tramite il suo codice.
     * 
     * @param codice Il codice del volo da rimuovere
     * @return true se il volo è stato rimosso con successo, false se non esisteva o in caso di errore
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
	public boolean rimuoviVolo(String codice) {
		Volo toRemove = cercaPerCodice(codice);
		// Validazione del parametro
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        }
        
        try {
            // Prima verifica se il volo esiste
            Volo voloEsistente = cercaPerCodice(codice);
            
            if (voloEsistente == null) {
                // Il volo non esiste
                System.out.println("Volo con codice " + codice + " non trovato");
                return false;
            }
            
            // Prepara la query di eliminazione
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM voli WHERE codice = ?"
            );
            ps.setString(1, codice.trim().toUpperCase());
            
            // Esegue l'eliminazione
            int rowsAffected = ps.executeUpdate();
            
            // Chiude il prepared statement
            ps.close();
            
            // Restituisce true se almeno una riga è stata eliminata
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("Volo " + codice + " rimosso con successo");
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione del volo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
	}   
	
}


//cosa ne pensi??Lo faresti diversamente??

// Trova tutti i voli che partono da un determinato aeroporto. metteresti anche questo??
