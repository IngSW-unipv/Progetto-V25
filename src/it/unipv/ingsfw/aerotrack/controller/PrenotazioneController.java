package it.unipv.ingsfw.aerotrack.controller;

import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import it.unipv.ingsfw.aerotrack.services.PrenotazioneService;

import java.util.List;

/**
 * Controller dedicato alla gestione delle prenotazioni.
 * Invocato dalle view/pannelli che gestiscono prenotazioni.
 */
public class PrenotazioneController {
    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController() {
        this(PrenotazioneService.getInstance());
    }

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }
    
    /**
     * Crea una nuova prenotazione.
     */
    public void creaPrenotazione(String nome, String cognome, String documento, String codiceVolo) {
        prenotazioneService.creaPrenotazione(nome, cognome, documento, codiceVolo);
    }

    /**
     * Ritorna tutte le prenotazioni.
     */
    public List<Prenotazione> getTuttePrenotazioni() {
        return prenotazioneService.getTuttePrenotazioni();
    }

    /**
     * Trova prenotazioni per documento passeggero.
     */
    public List<Prenotazione> trovaPrenotazioniPerDocumento(String documento) {
        return prenotazioneService.trovaPrenotazioniPerDocumento(documento);
    }
}