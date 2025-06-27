package it.unipv.ingsfw.aerotrack.facade;

import java.util.List;
import it.unipv.ingsfw.aerotrack.services.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Facade pattern per semplificare l'accesso al sistema AeroTrack.
 * Fornisce un'interfaccia unificata per tutte le operazioni principali.
 */
public class AeroTrackFacade {
    
    private AeroportoService aeroportoService;
    private VoloService voloService;
    private PrenotazioneService prenotazioneService;
    
    public AeroTrackFacade() {
        this.aeroportoService = new AeroportoService();
        this.voloService = new VoloService();
        this.prenotazioneService = new PrenotazioneService();
    }
    
    // ===== OPERAZIONI AEROPORTI =====
    public void creaAeroporto(String codice, String nome, double lat, double lon, int piste) {
        aeroportoService.aggiungiAeroporto(codice, nome, lat, lon, piste);
    }
    
    public List<Aeroporto> getAeroporti() {
        return aeroportoService.getTuttiAeroporti();
    }
    
    public Aeroporto trovaAeroporto(String codice) {
        return aeroportoService.cercaAeroporto(codice);
    }
    
    // ===== OPERAZIONI VOLI =====
    public void creaVolo(String codice, String codicePartenza, String codiceDestinazione, 
                        double orario, double velocita)  {
        voloService.creaVolo(codice, codicePartenza, codiceDestinazione, orario, velocita);
    }
    
    public List<Volo> getVoli() {
        return voloService.getTuttiVoli();
    }
    
    public Volo trovaVolo(String codice) {
        return voloService.cercaVolo(codice);
    }
    
    // ===== OPERAZIONI PRENOTAZIONI =====
    public void prenotaVolo(String nome, String cognome, String documento, String codiceVolo) {
        prenotazioneService.creaPrenotazione(nome, cognome, documento, codiceVolo);
    }
    
    public List<Prenotazione> getPrenotazioni() {
        return prenotazioneService.getTuttePrenotazioni();
    }
    
    /**
     * Inizializza il sistema con dati di esempio.
     */
    public void inizializzaDatiEsempio() {
        try {
            // Aeroporti principali italiani
            creaAeroporto("MXP", "Milano Malpensa", 45.6306, 8.7281, 2);
            creaAeroporto("LIN", "Milano Linate", 45.4454, 9.2767, 1);
            creaAeroporto("FCO", "Roma Fiumicino", 41.8003, 12.2389, 4);
            creaAeroporto("NAP", "Napoli Capodichino", 40.8860, 14.2908, 1);
            creaAeroporto("VCE", "Venezia Marco Polo", 45.5053, 12.3519, 1);
            
            // Voli di esempio
            creaVolo("AZ101", "MXP", "FCO", 8.5, 850);
            creaVolo("AZ102", "FCO", "MXP", 18.0, 850);
            creaVolo("FR123", "LIN", "NAP", 14.30, 800);
            creaVolo("LH456", "MXP", "VCE", 11.15, 750);
            
            // Prenotazioni di esempio
            prenotaVolo("Mario", "Rossi", "IT123456789", "AZ101");
            prenotaVolo("Anna", "Verdi", "IT987654321", "AZ101");
            prenotaVolo("Luigi", "Bianchi", "IT456789123", "FR123");
            
        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione dati: " + e.getMessage());
        }
    }
    
}



