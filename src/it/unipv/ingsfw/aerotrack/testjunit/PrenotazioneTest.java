package it.unipv.ingsfw.aerotrack.testjunit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unipv.ingsfw.aerotrack.models.Passeggero;
import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Prenotazione;


public class PrenotazioneTest {
	  
	// Test creazione prenotazione
    @Test
    public void testCreaPrenotazione() {
        Passeggero p = new Passeggero("Chiara", "Viale", "AB123456");
        Volo v = null; // si può sostituire con un vero oggetto volo se necessario
        Prenotazione pr = new Prenotazione(p, v);
        assertEquals(p, pr.getPasseggero());
        assertFalse(pr.isCancellata());
        assertNotNull(pr.getCodicePrenotazione());
    }

    // Test cancellazione prenotazione
    @Test
    public void testCancellazione() {
        Passeggero p = new Passeggero("Davide", "Bozzola", "CD789012");
        Prenotazione pr = new Prenotazione(p, null);
        pr.cancella();
        assertTrue(pr.isCancellata());
    }
    
    @Test
    public void testPrenotazionePasseggeroNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Prenotazione(null, null);
        });
    }
}


