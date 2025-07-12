package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.dao.AeroportoDao;
import it.unipv.ingsfw.aerotrack.dao.VoloDao;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.models.Volo;

import java.util.List;

public class TestRelazioniVoliAeroporto {
    public static void main(String[] args) {
        AeroportoDao aeroportoDao = AeroportoDao.getInstance();
        VoloDao voloDao = VoloDao.getInstance();

        // Pulisci DB e inserisci aeroporti e voli a mano oppure assicurati che il DB abbia almeno 2 aeroporti e 2 voli con lo stesso aeroporto di partenza

        // Carica tutti i voli dal database
        List<Volo> voli = voloDao.getTuttiVoli();

        // Supponiamo che almeno 2 voli partano dallo stesso aeroporto (es: "MXP")
        String codiceAeroporto = "MXP";
        Aeroporto aeroporto = null;

        // Trova un aeroporto di partenza tra i voli
        for (Volo v : voli) {
            if (v.getPartenza().getCodice().equals(codiceAeroporto)) {
                aeroporto = v.getPartenza();
                break;
            }
        }

        if (aeroporto == null) {
            System.out.println("Aeroporto di test non trovato tra i voli caricati!");
            return;
        }

        // Stampa tutti i voli in partenza da quell'aeroporto
        System.out.println("Voli in partenza da " + aeroporto.getCodice() + ":");
        for (Volo v : aeroporto.getVoliInPartenza()) {
            System.out.println("- " + v.getCodice());
        }

        // Verifica che la lista voliInPartenza sia aggiornata e condivisa tra tutti i voli
        int inPartenzaNelAeroporto = aeroporto.getVoliInPartenza().size();
        int contatiManualmente = 0;
        for (Volo v : voli) {
            if (v.getPartenza().equals(aeroporto)) {
                contatiManualmente++;
            }
        }
        System.out.println("Numero voli in partenza (secondo l'aeroporto): " + inPartenzaNelAeroporto);
        System.out.println("Numero voli in partenza (contati manualmente): " + contatiManualmente);

        if (inPartenzaNelAeroporto == contatiManualmente) {
            System.out.println("TEST SUPERATO: la lista voliInPartenza è condivisa e sincronizzata!");
        } else {
            System.out.println("ERRORE: le liste non sono sincronizzate!");
        }

        // Verifica che le piste non siano tutte libere se ci sono voli sulla stessa pista
        System.out.println("Stato delle piste dell'aeroporto " + aeroporto.getCodice() + ":");

        for (int i = 0; i < aeroporto.getNumeroPiste(); i++) {
            List<Volo> pista = aeroporto.getPiste()[i];  // o getPiste().get(i) se è lista

            if (pista == null || pista.isEmpty()) {
                System.out.println("Pista " + i + ": LIBERA");
            } else {
                for (Volo v : pista) {
                    System.out.println("Pista " + i + ": Programmata per il volo " + v.getCodice());
                }
            }
        }

    }
}