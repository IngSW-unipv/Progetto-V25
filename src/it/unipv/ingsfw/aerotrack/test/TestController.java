package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.controller.AeroTrackController;
import it.unipv.ingsfw.aerotrack.models.*;

public class TestController {
    public static void main(String[] args) {
        System.out.println("=== TEST CONTROLLER (FACADE) ===");
        AeroTrackController controller = new AeroTrackController();

        // Aggiungi aeroporti e voli tramite controller
        controller.aggiungiAeroporto("VCE", "Venezia Marco Polo", 45.51, 12.35, 2);
        controller.aggiungiAeroporto("NAP", "Napoli Capodichino", 40.88, 14.29, 1);
        controller.creaVolo("IT500", "VCE", "NAP", 15.10, 800);

        // Prenota due passeggeri su IT500
        controller.prenotaPasseggero("Chiara", "Viale", "DOC123", "IT500");
        controller.prenotaPasseggero("Marco", "Neri", "DOC321", "IT500");

        // Visualizza prenotazioni attive
        System.out.println("\nPrenotazioni attive:");
        for (Prenotazione p : controller.getPrenotazioniAttive()) {
            System.out.println(p);
        }

        // Ricerca volo per codice
        System.out.println("\nDettaglio volo IT500:");
        Volo v = controller.cercaVolo("IT500");
        System.out.println(v);
    }
}


// cosa ne pensii???