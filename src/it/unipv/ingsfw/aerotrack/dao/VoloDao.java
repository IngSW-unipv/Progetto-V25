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
 */
public class VoloDao {
	private static VoloDao instance;       // Istanza singleton della classe
	private Connection connection;         // Connessione al database SQLite
	private AeroportoDao aeroportoDao;     // Istanza del DAO degli aeroporti per recuperare i dati degli aeroporti
	
	
	// Costruttore per implementare il pattern Singleton.
	private VoloDao() {
		try {
			 connection = DriverManager.getConnection("jdbc:sqlite:aeroporti.db");
			 aeroportoDao = AeroportoDao.getInstance();
			 try (Statement stmt = connection.createStatement()) {     // Crea la tabella voli se non esiste già
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
		        }     
	    } catch (SQLException e) {
	    	 // In caso di errore, stampa lo stack trace e lancia eccezione runtime
	         e.printStackTrace();      // Stampa l'errore nella console
	         throw new RuntimeException("Errore durante l'inizializzazione del database voli", e);
	    }
	}
	
	
	/**
     * Restituisce l'istanza singleton della classe VoloDao.
     * Se l'istanza non esiste ancora, la crea.
     */
	public static synchronized VoloDao getInstance() {
		if (instance == null) {
			instance = new VoloDao();
		}
		return instance;
	}
	
	
	/**
     * Metodo aggiungiVolo: inserisce o aggiorna un volo (INSERT OR REPLACE).
     * 
     * @param volo Il volo da aggiungere al database
     * @return true se l'operazione è riuscita, false altrimenti
     * @throws IllegalArgumentException se il volo è null
     */
	public boolean aggiungiVolo(Volo v) {
		if (v == null) throw new IllegalArgumentException("Il volo non può essere null");
        String insertQuery = """
            INSERT OR REPLACE INTO voli
            (codice, partenza, destinazione, orario_partenza, velocita)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, v.getCodice());
            ps.setString(2, v.getPartenza().getCodice());
            ps.setString(3, v.getDestinazione().getCodice());
            ps.setDouble(4, v.getOrarioPartenza());
            ps.setDouble(5, v.getVelocita());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento del volo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }	
	

    // Metodo getTuttiVoli(): restituisce tutti i voli esistenti.
	public List<Volo> getTuttiVoli(){
		List<Volo> listaVoli = new ArrayList<>();
		String query = "SELECT * FROM voli";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
                Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
                if (partenza != null && destinazione != null) {
                    Volo v = new Volo(
                            rs.getString("codice"),
                            partenza,
                            destinazione,
                            rs.getDouble("orario_partenza"),
                            rs.getDouble("velocita")
                    );
                    listaVoli.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dei voli: " + e.getMessage());
            e.printStackTrace();
        }
        return listaVoli;
    }
	
	// Metodo trovaVoliPerPartenza(): trava i voli in partenza.
	public List<Volo> trovaVoliPerPartenza(String codiceAeroporto) {
	    List<Volo> listaVoli = new ArrayList<>();
	    String query = "SELECT * FROM voli WHERE partenza = ?";
	    try (PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setString(1, codiceAeroporto);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
	            Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
	            if (partenza != null && destinazione != null) {
	                Volo v = new Volo(
	                    rs.getString("codice"),
	                    partenza,
	                    destinazione,
	                    rs.getDouble("orario_partenza"),
	                    rs.getDouble("velocita")
	                );
	                listaVoli.add(v);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return listaVoli;
	}
		
	 /**
     *  Metodo cercaPerCodice(): cerca un volo con il suo codice identificativo.
     * 
     * @param codice Il codice del volo da cercare (es. "AZ123")
     * @return Il volo trovato, null se non esiste
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
	public Volo cercaPerCodice(String codice) {
        if (codice == null || codice.isEmpty()) 
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        String selectQuery = "SELECT * FROM voli WHERE codice = ?";
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, codice.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
                Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
                if (partenza != null && destinazione != null) {
                    return new Volo(
                            rs.getString("codice"),
                            partenza,
                            destinazione,
                            rs.getDouble("orario_partenza"),
                            rs.getDouble("velocita")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca del volo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
	
	/**
     * Metodo rimuoviVolo(): rimuove un volo tramite il suo codice.
     * 
     * @param codice Il codice del volo da rimuovere
     * @return true se il volo è stato rimosso con successo, false se non esisteva o in caso di errore
     * @throws IllegalArgumentException se il codice è null o vuoto
     */
	public boolean rimuoviVolo(String codice) {
		Volo toRemove = cercaPerCodice(codice);
		// Validazione del parametro
        if (codice == null || codice.isEmpty()) 
            throw new IllegalArgumentException("Il codice volo non può essere null o vuoto");
        String delQuery = "DELETE FROM voli WHERE codice = ?";
        try (PreparedStatement ps = connection.prepareStatement(delQuery)) {
            ps.setString(1, codice.trim().toUpperCase());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Volo " + codice + " rimosso con successo");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione del volo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
	
    /**
    * Chiude la connessione al database.
    * Chiamare solo quando l'applicazione termina!
    */
   public void close() {
       try {
           if (connection != null && !connection.isClosed()) {
               connection.close();
               System.out.println("Connessione al database voli chiusa.");
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }
}


//cosa ne pensi??Lo faresti diversamente??

