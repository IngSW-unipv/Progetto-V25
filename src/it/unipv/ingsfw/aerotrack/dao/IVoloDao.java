package it.unipv.ingsfw.aerotrack.dao;

import it.unipv.ingsfw.aerotrack.models.Volo;
import java.util.List;

/**
 * Interfaccia per operazioni CRUD sui voli.
 */
public interface IVoloDao {
    boolean aggiungiVolo(Volo v);
    List<Volo> getTuttiVoli();
    Volo cercaPerCodice(String codice);
    boolean rimuoviVolo(String codice);
}