package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.facade.AeroTrackFacade;
import it.unipv.ingsfw.aerotrack.models.*;

public class TestFacade {
    public static void main(String[] args) {
        System.out.println("=== TEST FACADE ===");
        AeroTrackFacade facade = new AeroTrackFacade();

        // Aggiungi aeroporti e voli tramite controller
        facade.creaAeroporto("VCE", "Venezia Marco Polo", 45.51, 12.35, 2);
        facade.creaAeroporto("NAP", "Napoli Capodichino", 40.88, 14.29, 1);
        facade.creaVolo("IT500", "VCE", "NAP", 15.10, 800);

        // Prenota due passeggeri su IT500
        facade.prenotaVolo("Chiara", "Viale", "DOC123", "IT500");
        facade.prenotaVolo("Marco", "Neri", "DOC321", "IT500");

        // Visualizza prenotazioni attive
        System.out.println("\nPrenotazioni attive:");
        for (Prenotazione p : facade.getPrenotazioni().stream().filter(pr -> !pr.isCancellata()).toList()) {
            System.out.println(p);
        }

        // Ricerca volo per codice
        System.out.println("\nDettaglio volo IT500:");
        Volo v = facade.trovaVolo("IT500");
        System.out.println(v);
    }
}


// cosa ne pensii???