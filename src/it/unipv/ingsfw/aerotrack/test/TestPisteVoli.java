package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.models.Volo;

import java.util.ArrayList;
import java.util.List;

public class TestPisteVoli {
    public static void main(String[] args) {
        // Crea un aeroporto con 2 piste
        Aeroporto mxp = new Aeroporto("MXP", "Milano Malpensa", 45.63, 8.72, 5);
        Aeroporto fco = new Aeroporto("FCO", "Roma Fiumicino", 41.80, 12.24, 4);

        List<Volo> voli = new ArrayList<>();

        // Crea 3 voli in partenza da MXP allo stesso orario
        for (int i = 1; i <= 3; i++) {
            Volo v = new Volo("TEST" + i, mxp, fco, 12.00, 800.0);
            voli.add(v);
        }

        // Stampa le piste assegnate
        for (Volo v : voli) {
            String pista = (v.getPistaAssegnata() >= 0) ? String.valueOf(v.getPistaAssegnata() + 1) : "-";
            System.out.println("Volo " + v.getCodice() + " pista assegnata: " + pista);
        }
    }
}