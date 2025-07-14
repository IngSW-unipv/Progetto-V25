package it.unipv.ingsfw.aerotrack.testjunit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;


public class AeroportoTest {
	
	@Test
    public void testCostruttoreValido() {
        Aeroporto a = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        assertEquals("MXP", a.getCodice());
        assertEquals("Malpensa", a.getNome());
        assertEquals(2, a.getNumeroPiste());
    }

    @Test
    public void testCostruttoreCodiceErrato() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Aeroporto("M", "Malpensa", 45.63, 8.72, 2);
        });
    }
    
    @Test
    public void testNomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Aeroporto("MXP", "", 45.63, 8.72, 2);
        });
    }    
    
    @Test
    public void testEqualsAeroporto() {
        Aeroporto a1 = new Aeroporto("MXP", "Malpensa", 45.63, 8.72, 2);
        Aeroporto a2 = new Aeroporto("MXP", "Malpensa2", 45.63, 8.73, 3);
        assertEquals(a1, a2); // stesso codice = equals true
    }
}