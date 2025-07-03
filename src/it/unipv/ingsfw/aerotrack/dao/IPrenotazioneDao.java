package it.unipv.ingsfw.aerotrack.dao;

import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import java.util.List;

/**
 * Interfaccia per operazioni CRUD sulle prenotazioni.
 */
public interface IPrenotazioneDao {
    boolean aggiungiPrenotazione(Prenotazione prenotazione);
    List<Prenotazione> getTuttePrenotazioni();
}