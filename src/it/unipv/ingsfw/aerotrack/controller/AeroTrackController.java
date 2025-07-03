package it.unipv.ingsfw.aerotrack.controller;

import java.util.List;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Controller principale del sistema, usato per orchestrare e gestire la navigazione
 * tra le diverse view/controller 
 */
public class AeroTrackController {
    private final AeroportoController aeroportoController;
    private final VoloController voloController;
    private final PrenotazioneController prenotazioneController;

    public AeroTrackController() {
        this.aeroportoController = new AeroportoController();
        this.voloController = new VoloController();
        this.prenotazioneController = new PrenotazioneController();
    }

    public AeroportoController getAeroportoController() {
        return aeroportoController;
    }

    public VoloController getVoloController() {
        return voloController;
    }

    public PrenotazioneController getPrenotazioneController() {
        return prenotazioneController;
    }
    
    //metodi di utilità rapida per le view testuali o GUI
    public void creaAeroporto(String codice, String nome, double lat, double lon, int piste) {
        aeroportoController.aggiungiAeroporto(codice, nome, lat, lon, piste);
    }
    public void creaVolo(String codice, String partenza, String destinazione, double orario, double velocita) {
        voloController.creaVolo(codice, partenza, destinazione, orario, velocita);
    }
    public void creaPrenotazione(String nome, String cognome, String documento, String codiceVolo) {
        prenotazioneController.creaPrenotazione(nome, cognome, documento, codiceVolo);
    }

    public List<Aeroporto> getAeroporti() {
        return aeroportoController.getTuttiAeroporti();
    }
    public List<Volo> getVoli() {
        return voloController.getTuttiVoli();
    }
    public List<Prenotazione> getPrenotazioniAttive() {
        return prenotazioneController.getTuttePrenotazioni()
                .stream()
                .filter(p -> !p.isCancellata())
                .toList();
    }

    public Volo cercaVolo(String codice) {
        return voloController.cercaVolo(codice);
    }
}