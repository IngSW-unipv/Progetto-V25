package it.unipv.ingsfw.aerotrack.controller;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.services.AeroportoService;

import java.util.List;

/**
 * Controller dedicato alla gestione degli aeroporti.
 * Invocato dalle view/pannelli che gestiscono aeroporti.
 */
public class AeroportoController {
    private final AeroportoService aeroportoService;

    public AeroportoController() {
        this(AeroportoService.getInstance());
    }

    public AeroportoController(AeroportoService aeroportoService) {
        this.aeroportoService = aeroportoService;
    }
    
    /**
     * Aggiunge un nuovo aeroporto.
     */
    public void aggiungiAeroporto(String codice, String nome, double lat, double lon, int piste) {
        aeroportoService.aggiungiAeroporto(codice, nome, lat, lon, piste);
    }

    /**
     * Cerca un aeroporto per codice (null se non trovato).
     */
    public Aeroporto cercaAeroporto(String codice) {
        return aeroportoService.cercaAeroporto(codice);
    }

    /**
     * Ritorna la lista di tutti gli aeroporti.
     */
    public List<Aeroporto> getTuttiAeroporti() {
        return aeroportoService.getTuttiAeroporti();
    }

    /**
     * Rimuove un aeroporto per codice.
     * @return true se rimosso, false altrimenti
     */
    public boolean rimuoviAeroporto(String codice) {
        return aeroportoService.rimuoviAeroporto(codice);
    }
}