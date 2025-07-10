package it.unipv.ingsfw.aerotrack.testjunit;

import static org.junit.jupiter.api.Assertions.*;
import it.unipv.ingsfw.aerotrack.models.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class VoloTest {

    @Test
    public void testGetCodice() {
        Aeroporto origine = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        Aeroporto destinazione = new Aeroporto("FCO", "Fiumicino", 41.80, 12.24, 4);
        LocalDate dataVolo = LocalDate.of(2025, 7, 11);
        Volo v = new Volo("AZ123", origine, destinazione, 10.0, 800, dataVolo);
        assertEquals("AZ123", v.getCodice());
        assertEquals(origine, v.getPartenza());
        assertEquals(destinazione, v.getDestinazione());
        assertEquals(dataVolo, v.getDataVolo());
    }

    @Test
    public void testPrenotazioneVolo() {
        Aeroporto origine = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        Aeroporto destinazione = new Aeroporto("FCO", "Fiumicino", 41.80, 12.24, 4);
        LocalDate dataVolo = LocalDate.of(2025, 7, 11);
        Volo v = new Volo("AZ123", origine, destinazione, 10.0, 800, dataVolo);

        Passeggero p = new Passeggero("Paolo", "Palmero", "AB222333");
        Prenotazione pr = new Prenotazione(p, v);

        v.aggiungiPrenotazione(pr);
        assertTrue(v.getPrenotazioni().contains(pr));
    }
}