package it.unipv.ingsfw.aerotrack.testjunit;

import it.unipv.ingsfw.aerotrack.models.Passeggero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasseggeroTest {

    @Test
    public void testCostruttoreValido() {
        Passeggero p = new Passeggero("Chiara", "Viale", "AB123456");
        assertEquals("Chiara", p.getNome());
        assertEquals("Viale", p.getCognome());
        assertEquals("AB123456", p.getDocumento());
    }

    @Test
    public void testCostruttoreNomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> new Passeggero("", "Viale", "AB123456"));
    }

    @Test
    public void testCostruttoreCognomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> new Passeggero("Chiara", "", "AB123456"));
    }

    @Test
    public void testCostruttoreDocumentoVuoto() {
        assertThrows(IllegalArgumentException.class, () -> new Passeggero("Chiara", "Viale", ""));
    }
}