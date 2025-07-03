package it.unipv.ingsfw.aerotrack.controller;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.services.VoloService;

import java.util.List;

/**
 * Controller dedicato alla gestione dei voli.
 * Invocato dalle view/pannelli che gestiscono i voli.
 */
public class VoloController {
    private final VoloService voloService;

    public VoloController() {
        this(VoloService.getInstance());
    }

    public VoloController(VoloService voloService) {
        this.voloService = voloService;
    }

    /**
     * Crea un nuovo volo.
     */
    public void creaVolo(String codice, String partenza, String destinazione, double orario, double velocita) {
        voloService.creaVolo(codice, partenza, destinazione, orario, velocita);
    }

    /**
     * Cerca un volo per codice.
     */
    public Volo cercaVolo(String codice) {
        return voloService.cercaVolo(codice);
    }

    /**
     * Ritorna tutti i voli.
     */
    public List<Volo> getTuttiVoli() {
        return voloService.getTuttiVoli();
    }

    /**
     * Trova i voli per aeroporto di partenza.
     */
    public List<Volo> trovaVoliPerPartenza(String codiceAeroporto) {
        return voloService.trovaVoliPerPartenza(codiceAeroporto);
    }

    /**
     * Trova i voli per aeroporto di destinazione.
     */
    public List<Volo> trovaVoliPerDestinazione(String codiceAeroporto) {
        return voloService.trovaVoliPerDestinazione(codiceAeroporto);
    }

    /**
     * Rimuove un volo per codice.
     */
    public boolean rimuoviVolo(String codice) {
        return voloService.rimuoviVolo(codice);
    }
}