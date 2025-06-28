package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Data Access Object per la gestione degli aeroporti nel database.
 * Implementa il pattern Singleton per garantire un'unica istanza della connessione.
 * Gestisce tutte le operazioni CRUD (Create, Read, Update, Delete) sugli aeroporti.
 */
public class AeroportoDao {
	
	private static AeroportoDao instance;     // Istanza singleton della classe
	private Connection connection;            // Connessione al database SQLite
	
	
	// Costruttore per Singleton
	private AeroportoDao() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:aeroporti.db");
			try (Statement stmt = connection.createStatement()) {
	            String createTableQuery = """
	                CREATE TABLE IF NOT EXISTS aeroporti (
	                    codice TEXT PRIMARY KEY,
	                    nome TEXT NOT NULL,
	                    latitudine REAL NOT NULL,
	                    longitudine REAL NOT NULL,
	                    numeroPiste INTEGER NOT NULL CHECK(numeroPiste > 0)
	                )
	                """;
	            stmt.executeUpdate(createTableQuery);
	        }
  
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
     * Se l'istanza non esiste ancora, la crea.
     * 
     * @return istanza di AeroportoDao
     */
	public static synchronized AeroportoDao getInstance() {
		if (instance == null) {
			instance = new AeroportoDao();
		}
		return instance;
	}

	/**
     * Chiude la connessione al database.
     * Chiamare solo quando l'applicazione termina!
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione al database aeroporti chiusa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	 
     // Metodo aggiungiAeroporto(): aggiunge o aggiorna un aeroporto (INSERT OR REPLACE).
    
	public void aggiungiAeroporto(Aeroporto a) {
        if (a == null) throw new IllegalArgumentException("L'aeroporto non può essere null");
        String insertQuery = """
            INSERT OR REPLACE INTO aeroporti
            (codice, nome, latitudine, longitudine, numeroPiste)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, a.getCodice());
            ps.setString(2, a.getNome());
            ps.setDouble(3, a.getLatitudine());
            ps.setDouble(4, a.getLongitudine());
            ps.setInt(5, a.getNumeroPiste());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore inserimento aeroporto: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	/**
     * Metodo getTuttiAeroporti(): restituisce tutti gli aeroporti presenti nel database.
     * 
     * @return Lista di tutti gli aeroporti nel database
     */ 
	public List<Aeroporto> getTuttiAeroporti(){
		List<Aeroporto> listaAeroporti = new ArrayList<>();
		String query = "SELECT * FROM aeroporti ORDER BY codice";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aeroporto aeroporto = new Aeroporto(
                        rs.getString("codice"),
                        rs.getString("nome"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getInt("numeroPiste"));
                listaAeroporti.add(aeroporto);
            }
            System.out.println("Recuperati " + listaAeroporti.size() + " aeroporti dal database");
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero degli aeroporti: " + e.getMessage());
            e.printStackTrace();
        }
        return listaAeroporti;
    }
        
	
	/**
     * Metodo cercaPerCodice(): cerca un aeroporto con il suo codice IATA.
     * 
     * @param codice Il codice IATA dell'aeroporto da cercare (es. "MXP")
     * @return L'aeroporto trovato, null se non esiste
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
    public Aeroporto cercaPerCodice(String codice) {
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Il codice aeroporto non può essere null o vuoto");
        }
       
        String selectQuery = "SELECT * FROM aeroporti WHERE codice = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, codice.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Aeroporto(
                        rs.getString("codice"),
                        rs.getString("nome"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getInt("numeroPiste"));
            } else {
                System.out.println("Aeroporto con codice " + codice + " non trovato");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca dell'aeroporto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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
	           
	                


//cosa ne pensi??
