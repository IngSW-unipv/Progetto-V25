package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.facade.AeroTrackFacade;
import it.unipv.ingsfw.aerotrack.models.*;

public class TestFacade {
    public static void main(String[] args) {
        System.out.println("=== TEST FACADE ===");
        AeroTrackFacade facade = new AeroTrackFacade();

        // *** SVUOTA LA TABELLA PRIMA DEL TEST ***
        facade.svuotaPrenotazioni();

        // INIZIALIZZAZIONE DEL CONTATORE DEI CODICI!
        facade.getPrenotazioni();
        
        // Aggiungi aeroporti e voli tramite controller
        facade.creaAeroporto("VCE", "Venezia Marco Polo", 45.51, 12.35, 2);
        facade.creaAeroporto("NAP", "Napoli Capodichino", 40.88, 14.29, 1);
        facade.creaVolo("IT500", "VCE", "NAP", 15.10, 800);

        // Prenota due passeggeri su IT500
        facade.prenotaVolo("Chiara", "Viale", "DOC123", "IT500");
        facade.prenotaVolo("Marco", "Neri", "DOC321", "IT500");

        // Visualizza prenotazioni attive
        System.out.println("\nPrenotazioni attive:");
        for (Prenotazione p : facade.getPrenotazioni()
        		.stream()
        		.filter(pr -> !pr.isCancellata())
        		.toList()) {
            System.out.println(p);
        }

        // Ricerca volo per codice e stampa dettagli con conteggio prenotazioni
        System.out.println("\nDettaglio volo IT500:");
        Volo v = facade.trovaVolo("IT500");
        long numPrenotazioni = facade.getPrenotazioni()
                .stream()
                .filter(p -> p.getVolo() != null && p.getVolo().getCodice().equals(v.getCodice()))
                .count();
            System.out.println("Volo " + v.getCodice() + ": " + v.getPartenza().getCodice() + " -> " + v.getDestinazione().getCodice()
                + ", Partenza: " + v.getOrarioPartenza() + ", Passeggeri: " + numPrenotazioni);
        }
}


// cosa ne pensii???