package it.unipv.ingsfw.aerotrack.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;
import java.util.stream.Collectors;
import it.unipv.ingsfw.aerotrack.dao.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Service Singleton per la logica di business delle prenotazioni.
 * Valida i dati e delega la persistenza al DAO.
 */
public class PrenotazioneService {
	
	private static PrenotazioneService instance;
	private final PrenotazioneDao prenotazioneDao;
    private final VoloDao voloDao;
    private final PasseggeroService passeggeroService = PasseggeroService.getInstance();
    
    private PrenotazioneService() {
        this.prenotazioneDao = PrenotazioneDao.getInstance();
        this.voloDao = VoloDao.getInstance();
    }

    public static PrenotazioneService getInstance() {
        if (instance == null) instance = new PrenotazioneService();
        return instance;
    }
    
    /**
     * Svuota la tabella prenotazioni. Da usare solo per test!
     */
    public void svuotaPrenotazioni() {
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM prenotazioni");
            System.out.println("Tabella prenotazioni svuotata.");
        } catch (SQLException e) {
            System.err.println("Errore durante lo svuotamento della tabella prenotazioni: " + e.getMessage());
        }
    }
    /**
     * Crea una nuova prenotazione.
     * 
     * @throws IllegalArgumentException se i dati non sono validi o il volo non esiste
     */
    public void creaPrenotazione(String nome, String cognome, String documento, String codiceVolo) {
        // Validazioni
    	boolean alreadyBooked = getTuttePrenotazioni().stream()
    	        .anyMatch(p -> p.getPasseggero().getDocumento().equals(documento) &&
    	                       p.getVolo().getCodice().equals(codiceVolo) &&
    	                       !p.isCancellata());
    	    if (alreadyBooked) {
    	        throw new RuntimeException("Questo passeggero ha già una prenotazione attiva su questo volo!");
    	    }
    	    
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome non può essere vuoto");
        }
        if (cognome == null || cognome.isEmpty()) {
            throw new IllegalArgumentException("Cognome non può essere vuoto");
        }
        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Documento non può essere vuoto");
        }
        Volo volo = voloDao.cercaPerCodice(codiceVolo);
        if (volo == null) {
            throw new IllegalArgumentException("Volo non trovato con codice: " + codiceVolo);
        }
        Passeggero passeggero = new Passeggero(nome, cognome, documento);
        passeggeroService.aggiungiPasseggero(passeggero);
        
        Prenotazione prenotazione = new Prenotazione(passeggero, volo);
        volo.aggiungiPrenotazione(prenotazione); 
        boolean ok = prenotazioneDao.aggiungiPrenotazione(prenotazione);
        if (!ok) throw new RuntimeException("Errore nel salvataggio della prenotazione su database");
    }
    
    /**
     * Restituisce tutte le prenotazioni.
     */
    public List<Prenotazione> getTuttePrenotazioni() {
        return prenotazioneDao.getTuttePrenotazioni();
    }
    
    /**
     * Trova prenotazioni per documento passeggero.
     */
    public List<Prenotazione> trovaPrenotazioniPerDocumento(String documento) {
        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Documento non può essere vuoto");
        }
        return prenotazioneDao.getTuttePrenotazioni()
               .stream()
               .filter(p -> p.getPasseggero().getDocumento().equalsIgnoreCase(documento))
               .collect(Collectors.toList());
    }
}