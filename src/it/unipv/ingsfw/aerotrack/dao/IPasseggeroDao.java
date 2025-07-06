package it.unipv.ingsfw.aerotrack.dao;

import it.unipv.ingsfw.aerotrack.models.Passeggero;
import java.util.List;

/**
 * Interfaccia per operazioni CRUD sui passeggeri.
 */
public interface IPasseggeroDao {
    boolean aggiungiPasseggero(Passeggero passeggero);
    Passeggero cercaPerDocumento(String documento);
    List<Passeggero> getTuttiPasseggeri();
}