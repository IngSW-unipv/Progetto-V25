package it.unipv.ingsfw.aerotrack.facade;

import java.util.List;

import java.time.LocalDate;
import java.time.LocalTime;
import it.unipv.ingsfw.aerotrack.services.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Facade pattern per semplificare l'accesso al sistema AeroTrack.
 * Fornisce un'interfaccia unificata per tutte le operazioni principali.
 */
public class AeroTrackFacade {
    
    private final AeroportoService aeroportoService;
    private final VoloService voloService;
    private final PrenotazioneService prenotazioneService;
    
    // Costruttore: inizializza tutti i serviz.
    public AeroTrackFacade() {
        this.aeroportoService = AeroportoService.getInstance();
        this.voloService = VoloService.getInstance();
        this.prenotazioneService = PrenotazioneService.getInstance();
    }
    
    // ===== OPERAZIONI AEROPORTI =====
    
    /**
     * Crea un nuovo aeroporto.
     */
    public void creaAeroporto(String codice, String nome, double lat, double lon, int piste) {
        aeroportoService.aggiungiAeroporto(codice, nome, lat, lon, piste);
    }
    
    /**
     * Restituisce la lista di tutti gli aeroporti presenti nel sistema.
     */
    public List<Aeroporto> getAeroporti() {
        return aeroportoService.getTuttiAeroporti();
    }
    
    /**
     * Cerca un aeroporto tramite codice IATA.
     */
    public Aeroporto trovaAeroporto(String codice) {
        return aeroportoService.cercaAeroporto(codice);
    }
    
    // ===== OPERAZIONI VOLI =====
    
    /**
     * Crea un nuovo volo tra due aeroporti.
     */
    public void creaVolo(String codice, String codicePartenza, String codiceDestinazione, LocalTime orario, double velocita, LocalDate dataVolo)  {
        voloService.creaVolo(codice, codicePartenza, codiceDestinazione, orario, velocita, dataVolo);
    }
    
    /**
     * Restituisce la lista di tutti i voli.
     */
    public List<Volo> getVoli() {
        return voloService.getTuttiVoli();
    }
    
    /**
     * Cerca un volo tramite codice.
     */
    public Volo trovaVolo(String codice) {
        return voloService.cercaVolo(codice);
    }
    
    // ===== OPERAZIONI PRENOTAZIONI =====
    
    /**
     * Prenota un passeggero su un volo esistente.
     */
    public void prenotaVolo(String nome, String cognome, String documento, String codiceVolo) {
        prenotazioneService.creaPrenotazione(nome, cognome, documento, codiceVolo);
    }
    
    /**
     * Restituisce la lista di tutte le prenotazioni.
     */
    public List<Prenotazione> getPrenotazioni() {
        return prenotazioneService.getTuttePrenotazioni();
    }
    
    /**
     * Svuota la tabella delle prenotazioni (da usare nei test per avere dati puliti).
     */
    public void svuotaPrenotazioni() {
        prenotazioneService.svuotaPrenotazioni();
    }
    
    // ===== INIZIALIZZAZIONE DATI ESEMPIO =====
    public void inizializzaDatiEsempio() {
        try {
            // Aeroporti principali italiani
            creaAeroporto("MXP", "Milano Malpensa", 45.6306, 8.7281, 2);
            creaAeroporto("LIN", "Milano Linate", 45.4454, 9.2767, 1);
            creaAeroporto("FCO", "Roma Fiumicino", 41.8003, 12.2389, 4);
            creaAeroporto("NAP", "Napoli Capodichino", 40.8860, 14.2908, 1);
            creaAeroporto("VCE", "Venezia Marco Polo", 45.5053, 12.3519, 1);
            
            // Data di esempio (oggi)
            LocalDate dataEsempio = LocalDate.now();
            LocalTime oraEsempio = LocalTime.now();
            
            // Voli di esempio
            creaVolo("AZ101", "MXP", "FCO", oraEsempio, 850, dataEsempio);
            creaVolo("AZ102", "FCO", "MXP", oraEsempio, 850, dataEsempio);
            creaVolo("FR123", "LIN", "NAP", oraEsempio, 800, dataEsempio);
            creaVolo("LH456", "MXP", "VCE", oraEsempio, 750, dataEsempio);
            
            // Prenotazioni di esempio
            prenotaVolo("Chiara", "Viale", "IT123456789", "AZ101");
            prenotaVolo("Davide", "Bozzola", "IT987654321", "AZ101");
            prenotaVolo("Luigi", "Palmero", "IT456789123", "FR123");
            
        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione dati: " + e.getMessage());
        }
    }
    
}



