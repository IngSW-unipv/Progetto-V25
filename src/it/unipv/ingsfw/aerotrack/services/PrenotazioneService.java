package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
import java.util.stream.Collectors;
import it.unipv.ingsfw.aerotrack.dao.*;
import it.unipv.ingsfw.aerotrack.models.*;

public class PrenotazioneService {
	
	private PrenotazioneDao prenotazioneDao;
    private VoloDao voloDao;
    
    public PrenotazioneService() {
        this.prenotazioneDao = PrenotazioneDao.getInstance();
        this.voloDao = VoloDao.getInstance();
    }
    
    /**
     * Creo una nuova prenotazione.
     */
    public void creaPrenotazione(String nome, String cognome, String documento, String codiceVolo) {
        
        // Validazioni
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome non può essere vuoto");
        }
        
        if (cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Cognome non può essere vuoto");
        }
        
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento non può essere vuoto");
        }
        
        // Trova il volo specificato
        Volo volo = voloDao.cercaPerCodice(codiceVolo);
        if (volo == null) {
            throw new IllegalArgumentException("Volo non trovato con codice: " + codiceVolo);
        }

        // Crea passeggero e prenotazione
        Passeggero passeggero = new Passeggero(nome, cognome, documento);
        Prenotazione prenotazione = new Prenotazione(passeggero, volo);
        
        // Aggiunge la prenotazione al volo
        volo.aggiungiPrenotazione(prenotazione);
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
    	 if (documento == null || documento.trim().isEmpty()) {
             throw new IllegalArgumentException("Documento non può essere vuoto");
         }
    	 
    	 return prenotazioneDao.getTuttePrenotazioni()
                .stream()
                .filter(p -> p.getPasseggero().getDocumento().equalsIgnoreCase(documento))
                .collect(Collectors.toList());
    }
}
