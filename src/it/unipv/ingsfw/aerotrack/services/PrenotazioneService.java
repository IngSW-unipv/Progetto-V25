package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
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
    
    private PrenotazioneService() {
        this.prenotazioneDao = PrenotazioneDao.getInstance();
        this.voloDao = VoloDao.getInstance();
    }

    public static PrenotazioneService getInstance() {
        if (instance == null) instance = new PrenotazioneService();
        return instance;
    }
    
    /**
     * Crea una nuova prenotazione.
     * 
     * @throws IllegalArgumentException se i dati non sono validi o il volo non esiste
     */
    public void creaPrenotazione(String nome, String cognome, String documento, String codiceVolo) {
        // Validazioni
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