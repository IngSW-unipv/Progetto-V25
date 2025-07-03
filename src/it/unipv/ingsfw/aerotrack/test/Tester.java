package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.services.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Test manuale: verifica la creazione di aeroporti, voli e prenotazioni.
 * Permette di vedere l'output da console e verificare la ricerca delle prenotazioni.
 */
public class Tester {

	public static void main(String[] args) {
        System.out.println("=== TEST COMPLETO: PRENOTAZIONI ===");

        AeroportoService aeroportoService = AeroportoService.getInstance();
        VoloService voloService = VoloService.getInstance();
        PrenotazioneService prenotazioneService = PrenotazioneService.getInstance();

        // Inizializza aeroporti e voli
        aeroportoService.aggiungiAeroporto("LIN", "Milano Linate", 45.45, 9.27, 2);
        aeroportoService.aggiungiAeroporto("NAP", "Napoli Capodichino", 40.88, 14.29, 2);
        voloService.creaVolo("FR100", "LIN", "NAP", 10.00, 780);

        // Crea due prenotazioni
        prenotazioneService.creaPrenotazione("Anna", "Verdi", "IT12345", "FR100");
        prenotazioneService.creaPrenotazione("Luca", "Bianchi", "IT54321", "FR100");

        System.out.println("\nPrenotazioni attuali:");
        for (Prenotazione p : prenotazioneService.getTuttePrenotazioni()) {
            System.out.println(p);
        }

        // Test ricerca prenotazione per documento
        System.out.println("\nPrenotazioni per documento IT12345:");
        for (Prenotazione p : prenotazioneService.trovaPrenotazioniPerDocumento("IT12345")) {
            System.out.println(p);
        }
    }
}

// cosa ne pensi??
