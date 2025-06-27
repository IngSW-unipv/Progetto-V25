package it.unipv.ingsfw.aerotrack.controller;

import it.unipv.ingsfw.aerotrack.models.*;
import java.util.*;


public class AeroTrackController {
	
	private List<Aeroporto> aeroporti;
	private List<Volo> voli;
	private List<Prenotazione> prenotazioni;
		
	public  AeroTrackController() {
		aeroporti = new ArrayList<>();
		voli = new ArrayList<>();
		prenotazioni = new ArrayList<>();
	}
		
	public void aggiungiAeroporto(String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
		Aeroporto a = new Aeroporto(codice, nome, latitudine, longitudine, numeroPiste);
		aeroporti.add(a);
	}
		
	public Volo creaVolo(String codiceVolo, String codicePartenza, String codiceDestinazione) {
		Aeroporto partenza = cercaAeroporto(codicePartenza);
		Aeroporto destinazione = cercaAeroporto(codiceDestinazione);
			
		if (partenza == null || destinazione == null) {
			throw new IllegalArgumentException("Aeroporto non trovato.");
		}
			
		Volo v = new Volo(codiceVolo, partenza, destinazione);
		voli.add(v);
		return v;	
	}
		
	public void prenotaPasseggero(String nome, String cognome, String documento, String codiceVolo) {
		Passeggero p = new Passeggero(nome, cognome, documento);
		Volo v = cercaVolo(codiceVolo);
			
		if(v == null) {
			throw new IllegalArgumentException("Volo non trovato.");
		}
			
		Prenotazione pr = new Prenotazione(p,v);
		prenotazioni.add(pr);
	}
		
	public Aeroporto cercaAeroporto(String codice) {
		for (Aeroporto a : aeroporti) {
			if (a.getCodice().equalsIgnoreCase(codice)) return a;
		}
		return null;
	}
		
	public Volo cercaVolo(String codiceVolo) {
		for (Volo v : voli) {
			if (v.getCodice().equalsIgnoreCase(codiceVolo)) return v;
		}
		return null;
	}
		
	public List<Prenotazione> getPrenotazioniAttive(){
		List<Prenotazione> attive = new ArrayList<>();
		for(Prenotazione pr : prenotazioni) {
			if (!pr.isCancellata()) attive.add(pr);
		}
		return attive;
	}

}


