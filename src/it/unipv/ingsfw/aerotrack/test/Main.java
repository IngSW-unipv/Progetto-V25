package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.models.*;
import it.unipv.ingsfw.aerotrack.services.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST DI BASE SU AEROPORTO E VOLO ===");

        AeroportoService aeroportoService = new AeroportoService();
        VoloService voloService = new VoloService();

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

//cosa ne pensi??