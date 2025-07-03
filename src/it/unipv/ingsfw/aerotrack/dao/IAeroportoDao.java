package it.unipv.ingsfw.aerotrack.dao;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import java.util.List;

/**
 * Interfaccia per operazioni CRUD sugli aeroporti.
 */
public interface IAeroportoDao {
    void aggiungiAeroporto(Aeroporto a);
    List<Aeroporto> getTuttiAeroporti();
    Aeroporto cercaPerCodice(String codice);
    boolean rimuoviAeroporto(String codice);
}