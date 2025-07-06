package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.models.*;
import it.unipv.ingsfw.aerotrack.services.*;

/**
 * Test manuale: crea aeroporti e voli, stampa su console lo stato del sistema.
 * Utile per debug e verifica veloce da linea di comando.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST DI BASE SU AEROPORTO E VOLO ===");

        AeroportoService aeroportoService = AeroportoService.getInstance();
        VoloService voloService = VoloService.getInstance();
        PrenotazioneService prenotazioneService = PrenotazioneService.getInstance();

        // SVUOTA TUTTE LE TABELLE per test pulito
        prenotazioneService.svuotaPrenotazioni();
        voloService.svuotaVoli();
        aeroportoService.svuotaAeroporti();
        
        // Creo aeroporti di test
        aeroportoService.aggiungiAeroporto("MXP", "Milano Malpensa", 45.63, 8.72, 2);
        aeroportoService.aggiungiAeroporto("FCO", "Roma Fiumicino", 41.80, 12.24, 4);

        System.out.println("Aeroporti registrati:");
        for (Aeroporto a : aeroportoService.getTuttiAeroporti()) {
            System.out.println(a);
        }

        // Creo un volo tra MXP e FCO
        voloService.creaVolo("AZ123", "MXP", "FCO", 12.45, 850);

        System.out.println("\nVoli registrati:");
        for (Volo v : voloService.getTuttiVoli()) {
            System.out.println(v);
        }

        // Test ricerca
        Volo v = voloService.cercaVolo("AZ123");
        System.out.println("\nDettagli volo trovato: " + v);
    }
}
