package it.unipv.ingsfw.aerotrack.controller;

import it.unipv.ingsfw.aerotrack.models.*;
import it.unipv.ingsfw.aerotrack.services.AeroportoService;
import it.unipv.ingsfw.aerotrack.services.VoloService;
import it.unipv.ingsfw.aerotrack.services.PrenotazioneService;

import java.util.*;


public class AeroTrackController {
	
	private final AeroportoService aeroportoService;
    private final VoloService voloService;
    private final PrenotazioneService prenotazioneService;

    public AeroTrackController() {
        this.aeroportoService = new AeroportoService();
        this.voloService = new VoloService();
        this.prenotazioneService = new PrenotazioneService();
    }

	public void aggiungiAeroporto(String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
		aeroportoService.aggiungiAeroporto(codice, nome, latitudine, longitudine, numeroPiste);
    }
	
	public Aeroporto cercaAeroporto(String codice) {
	    return aeroportoService.cercaAeroporto(codice);
	}

	public List<Aeroporto> getAeroporti() {
	    return aeroportoService.getTuttiAeroporti();
    }
		  
	public void creaVolo(String codiceVolo, String codicePartenza, String codiceDestinazione, double orario, double velocita) {
		voloService.creaVolo(codiceVolo, codicePartenza, codiceDestinazione, orario, velocita);
	}

	public Volo cercaVolo(String codiceVolo) {
        return voloService.cercaVolo(codiceVolo);
    }

    public List<Volo> getVoli() {
        return voloService.getTuttiVoli();
    }
		
	public void prenotaPasseggero(String nome, String cognome, String documento, String codiceVolo) {
		prenotazioneService.creaPrenotazione(nome, cognome, documento, codiceVolo);
    }
			
	public List<Prenotazione> getPrenotazioniAttive() {
        // Ricava TUTTE le prenotazioni e filtra solo quelle attive.
        return prenotazioneService.getTuttePrenotazioni()
                .stream()
                .filter(p -> !p.isCancellata())
                .toList();
    }

    public List<Prenotazione> getPrenotazioni() {
        return prenotazioneService.getTuttePrenotazioni();
    }
}    