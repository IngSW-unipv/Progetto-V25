package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Data Access Object per la gestione degli aeroporti nel database.
 * Implementa il pattern Singleton per garantire un'unica istanza della connessione.
 * Gestisce tutte le operazioni CRUD (Create, Read, Update, Delete) sugli aeroporti.
 * 
 * Questa classe si occupa di:
 * - Creare e mantenere la connessione al database SQLite
 * - Inserire nuovi aeroporti nel database
 * - Recuperare aeroporti esistenti
 * - Aggiornare informazioni sugli aeroporti
 * - Eliminare aeroporti dal database
 * 
 * Il database utilizza SQLite e il file si chiama "aeroporti.db".
 * La tabella aeroporti contiene: codice, nome, latitudine, longitudine, numeroPiste.
 */
public class AeroportoDao {
	
	private static AeroportoDao instance;     // Istanza singleton della classe
	private Connection connection;            // Connessione al database SQLite
	
	
	 /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza la connessione al database e crea la tabella aeroporti se non esiste.
     * 
     * La tabella aeroporti contiene:
     * - codice: chiave primaria dell'aeroporto (es. "MXP") - 3 caratteri IATA
     * - nome: nome completo dell'aeroporto
     * - latitudine: coordinata geografica (range: -90 a +90)
     * - longitudine: coordinata geografica (range: -180 a +180)
     * - numeroPiste: numero di piste disponibili (intero positivo)
     * 
     * @throws RuntimeException se la connessione al database fallisce
     */
	private AeroportoDao() {
		try {
			// Connessione al database SQLite
            // Il file aeroporti.db verrà creato automaticamente se non esiste
			connection = DriverManager.getConnection("jdbc:sqlite:aeroporto.db");
			
		    // Crea lo statement per eseguire comandi SQL
			Statement stmt = connection.createStatement();
			
			// Query SQL per creare la tabella se non esiste già
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS aeroporti (
                    codice TEXT PRIMARY KEY,
                    nome TEXT NOT NULL,
                    latitudine REAL NOT NULL,
                    longitudine REAL NOT NULL,
                    numeroPiste INTEGER NOT NULL CHECK(numeroPiste > 0)
                )
                """;
            
            // Esegue la creazione della tabella
            stmt.executeUpdate(createTableQuery);
            
            // Chiude lo statement dopo l'uso per liberare risorse
            stmt.close();
            
            // Messaggio di conferma per il debug
            System.out.println("Database aeroporti inizializzato correttamente");
            
        } catch (SQLException e) {
            // In caso di errore durante l'inizializzazione
            System.err.println("Errore durante l'inizializzazione del database aeroporti");
            e.printStackTrace();
            throw new RuntimeException("Impossibile inizializzare il database aeroporti", e);
        }
    }
	
	/**
     * Restituisce l'istanza singleton della classe AeroportoDao.
     * Se l'istanza non esiste ancora, la crea (inizializzazione lazy).
     * 
     * @return L'unica istanza di AeroportoDao
     */
	public static AeroportoDao getInstance() {
		if (instance == null) {
			instance = new AeroportoDao();
		}
		return instance;
	}
	
	 /**
     * Metodo aggiungiAeroporto(): Aggiunge un nuovo aeroporto al database.
     * Se esiste già un aeroporto con lo stesso codice, lo sostituisce (INSERT OR REPLACE).
     * 
     * @param aeroporto L'aeroporto da aggiungere al database
     * @return true se l'operazione è riuscita, false altrimenti
     * @throws IllegalArgumentException se l'aeroporto è null
     */
	public void AggiungiAeroporto(Aeroporto a) {
		// Validazione del parametro di input
        if (a == null) {
            throw new IllegalArgumentException("L'aeroporto non può essere null");
        }
        
		try {
			 // Query SQL per inserire l'aeroporto
             // INSERT OR REPLACE sostituisce l'aeroporto se esiste già lo stesso codice
			 String insertQuery = """
	             INSERT OR REPLACE INTO aeroporti 
	             (codice, nome, latitudine, longitudine, numeroPiste) 
	             VALUES (?, ?, ?, ?, ?)
	             """;
	            
	         // Prepara lo statement con i parametri
			 PreparedStatement ps = connection.prepareStatement(insertQuery);
			 ps.setString(1, a.getCodice());           // Codice aeroporto
	         ps.setString(2, a.getNome());             // Nome aeroporto
	         ps.setDouble(3, a.getLatitudine());       // Latitudine
	         ps.setDouble(4, a.getLongitudine());      // Longitudine
	         ps.setInt(5, a.getNumeroPiste());         // Numero piste
	         
	         // Esegue l'inserimento
	         ps.executeUpdate();
	        
	        } catch (SQLException e) {
	            e.printStackTrace(); 
		}
	}
	
	/**
     * Metodo getTuttiAeroporti(): recupera tutti gli aeroporti presenti nel database.
     * Restituisce una lista vuota se non ci sono aeroporti.
     * 
     * @return Lista di tutti gli aeroporti nel database
     */ 
	public List<Aeroporto> getTuttiGliAeroporti(){
		List<Aeroporto> listaAeroporti = new ArrayList<>();
		
        try {
        	 // Crea lo statement per la query
            Statement stmt = connection.createStatement();
            
            // Esegue la query per selezionare tutti gli aeroporti
            ResultSet rs = stmt.executeQuery("SELECT * FROM aeroporti ORDER BY codice");
            
            // Itera sui risultati della query
            while (rs.next()) {
                // Estrae i dati dal ResultSet
                String codice = rs.getString("codice");
                String nome = rs.getString("nome");
                double latitudine = rs.getDouble("latitudine");
                double longitudine = rs.getDouble("longitudine");
                int numeroPiste = rs.getInt("numeroPiste");
                
                // Crea l'oggetto Aeroporto e lo aggiunge alla lista
                Aeroporto aeroporto = new Aeroporto(codice, nome, latitudine, longitudine, numeroPiste);
                listaAeroporti.add(aeroporto);
            }
            
           
 System.out.println("Recuperati " + listaAeroporti.size() + " aeroporti dal database");
            
        } catch (SQLException e) {
            // Gestisce gli errori SQL
            System.err.println("Errore durante il recupero degli aeroporti: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaAeroporti;
    }

	
	/**
     * Metodo cercaPerCodice(): cerca un aeroporto specifico tramite il suo codice IATA.
     * 
     * @param codice Il codice IATA dell'aeroporto da cercare (es. "MXP")
     * @return L'aeroporto trovato, null se non esiste
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
    public Aeroporto cercaPerCodice(String codice) {
        // Validazione del parametro
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Il codice aeroporto non può essere null o vuoto");
        }
       
	    try {
	    	// Prepara la query con parametro per evitare SQL injection
            String selectQuery = "SELECT * FROM aeroporti WHERE codice = ?";
            PreparedStatement ps = connection.prepareStatement(selectQuery);
            
            // Imposta il parametro (converte in maiuscolo per standardizzare)
            ps.setString(1, codice.toUpperCase());
            
            // Esegue la query
            ResultSet rs = ps.executeQuery();
            
            // Se trova l'aeroporto
            if (rs.next()) {
                // Estrae i dati dal ResultSet
                String codiceTrovato = rs.getString("codice");
                String nome = rs.getString("nome");
                double latitudine = rs.getDouble("latitudine");
                double longitudine = rs.getDouble("longitudine");
                int numeroPiste = rs.getInt("numeroPiste");
                
             // Crea e restituisce l'aeroporto
                return new Aeroporto(codiceTrovato, nome, latitudine, longitudine, numeroPiste);
            } else {
                // Chiude le risorse anche se non trova nulla
                rs.close();
                ps.close();
                
                // Messaggio informativo
                System.out.println("Aeroporto con codice " + codice + " non trovato");
            }
            
        } catch (SQLException e) {
            // Gestisce gli errori SQL
            System.err.println("Errore durante la ricerca dell'aeroporto: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Restituisce null se non trova l'aeroporto o in caso di errore
        return null;
    }
}               
	           
	                


//cosa ne pensi??
