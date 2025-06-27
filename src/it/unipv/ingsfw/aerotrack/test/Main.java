package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.models.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Prenotazione> prenotazioni = new ArrayList<>();
        List<Passeggero> passeggeri = new ArrayList<>();

        System.out.println("=== Sistema gestione Aeroporto ===");
        while (true) {
            System.out.println("\n1) Registra passeggero");
            System.out.println("2) Fai prenotazione");
            System.out.println("3) Cancella prenotazione");
            System.out.println("4) Visualizza prenotazioni");
            System.out.println("0) Esci");
            System.out.print("Scelta: ");
            int scelta = Integer.parseInt(sc.nextLine());
            if (scelta == 1) {
                System.out.print("Nome: ");
                String nome = sc.nextLine();
                System.out.print("Cognome: ");
                String cognome = sc.nextLine();
                System.out.print("Documento: ");
                String doc = sc.nextLine();
                Passeggero p = new Passeggero(nome, cognome, doc);
                passeggeri.add(p);
                System.out.println("Passeggero registrato con successo!");
            } else if (scelta == 2) {
                if (passeggeri.isEmpty()) {
                    System.out.println("Prima registra almeno un passeggero!");
                    continue;
                }
                System.out.println("Scegli passeggero (numero):");
                for (int i = 0; i < passeggeri.size(); i++)
                    System.out.println((i+1) + ") " + passeggeri.get(i));
                int idx = Integer.parseInt(sc.nextLine()) - 1;
                Passeggero p = passeggeri.get(idx);
                Prenotazione pr = new Prenotazione(p, null); // semplificato, senza volo
                prenotazioni.add(pr);
                System.out.println("Prenotazione fatta: " + pr.getCodicePrenotazione());
            } else if (scelta == 3) {
                System.out.print("Inserisci codice prenotazione da cancellare: ");
                String cod = sc.nextLine();
                Optional<Prenotazione> opt = prenotazioni.stream().filter(pr -> pr.getCodicePrenotazione().equals(cod)).findFirst();
                if (opt.isPresent()) {
                    opt.get().cancella();
                    System.out.println("Prenotazione cancellata!");
                } else {
                    System.out.println("Codice non trovato.");
                }
            } else if (scelta == 4) {
                for (Prenotazione pr : prenotazioni)
                    System.out.println(pr);
            } else if (scelta == 0) {
                System.out.println("Arrivederci!");
                break;
            } else {
                System.out.println("Scelta non valida.");
            }
        }
        sc.close();
    }
}