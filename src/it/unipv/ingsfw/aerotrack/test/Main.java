package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.models.*;
import it.unipv.ingsfw.aerotrack.services.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        PasseggeroService passeggeroService = PasseggeroService.getInstance();

        // SVUOTA TUTTE LE TABELLE per test pulito
        prenotazioneService.svuotaPrenotazioni();
        voloService.svuotaVoli();
        passeggeroService.svuotaPasseggeri(); 
        aeroportoService.svuotaAeroporti();

        
        // Creo aeroporti di test
        aeroportoService.aggiungiAeroporto("MXP", "Milano Malpensa", 45.63, 8.72, 2);
        aeroportoService.aggiungiAeroporto("FCO", "Roma Fiumicino", 41.80, 12.24, 3);
        aeroportoService.aggiungiAeroporto("LIN", "Milano Linate", 45.45, 9.27, 1);
        aeroportoService.aggiungiAeroporto("NAP", "Napoli Capodichino", 40.88, 14.29, 1);

        System.out.println("Aeroporti registrati:");
        for (Aeroporto a : aeroportoService.getTuttiAeroporti()) {
            System.out.println(a);
        }
        
        LocalDate dataTest = LocalDate.now();

        // Creo voli di test
        voloService.creaVolo("AZ123", "MXP", "FCO", LocalTime.of(14, 30), 850, dataTest);   
        voloService.creaVolo("AZ124", "MXP", "FCO", LocalTime.of(14, 45), 850, dataTest);
        voloService.creaVolo("AZ125", "MXP", "FCO", LocalTime.of(14, 50), 850, dataTest);
        voloService.creaVolo("AZ126", "FCO", "MXP", LocalTime.of(14, 30), 900, dataTest);
        voloService.creaVolo("AZ127", "MXP", "FCO", LocalTime.of(20, 00), 800, dataTest);
        voloService.creaVolo("AZ128", "LIN", "NAP", LocalTime.of(10, 00), 850, dataTest);

        System.out.println("\nVoli registrati:");
        for (Volo v : voloService.getTuttiVoli()) {
            System.out.println(v + " - Data: " + v.getDataVolo());
        }
        
        // Aggiungi prenotazioni di test
        prenotazioneService.creaPrenotazione("Chiara", "Viale", "ABC123", "AZ123");
        prenotazioneService.creaPrenotazione("Davide", "Bozzola", "DEF456", "AZ123");
        prenotazioneService.creaPrenotazione("Paolo", "Palmero", "GHI789", "AZ124");


        // Ricarica le prenotazioni e associa ai voli 
        List<Volo> voli = voloService.getTuttiVoli();
        List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni();
        for (Prenotazione p : prenotazioni) {
            if (p.getVolo() != null) {
                for (Volo v : voli) {
                    if (v.getCodice().equals(p.getVolo().getCodice())) {
                        v.aggiungiPrenotazione(p);
                        break;
                    }
                }
            }
        }

        System.out.println("\nVoli registrati:");
        for (Volo v : voli) {
            System.out.println(v + " - Data: " + v.getDataVolo());
        }

        // Test ricerca
        Volo v = voloService.cercaVolo("AZ123");
        if (v != null) {
            // aggiorna le prenotazioni anche dell'oggetto trovato
            for (Prenotazione p : prenotazioni) {
                if (p.getVolo() != null && v.getCodice().equals(p.getVolo().getCodice())) {
                    v.aggiungiPrenotazione(p);
                }
            }
            System.out.println("\nDettagli volo trovato: " + v + " - Data: " + v.getDataVolo());
        }
    }
}