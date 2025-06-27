package it.unipv.ingsfw.aerotrack.testjunit;

import static org.junit.jupiter.api.Assertions.*;
import it.unipv.ingsfw.aerotrack.models.*;
import org.junit.jupiter.api.Test;

public class VoloTest {

    @Test
    public void testGetCodice() {
        Aeroporto origine = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        Aeroporto destinazione = new Aeroporto("FCO", "Fiumicino", 41.80, 12.24, 4);
        Volo v = new Volo("AZ123", origine, destinazione, 10.0, 800);
        assertEquals("AZ123", v.getCodice());
        assertEquals(origine, v.getPartenza());
        assertEquals(destinazione, v.getDestinazione());
    }

    @Test
    public void testPrenotazioneVolo() {
        Aeroporto origine = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        Aeroporto destinazione = new Aeroporto("FCO", "Fiumicino", 41.80, 12.24, 4);
        Volo v = new Volo("AZ123", origine, destinazione, 10.0, 800);

        Passeggero p = new Passeggero("Laura", "Verdi", "AB222333");
        Prenotazione pr = new Prenotazione(p, v);

        v.aggiungiPrenotazione(pr);
        assertTrue(v.getPrenotazioni().contains(pr));
    }
}