package it.unipv.ingsfw.aerotrack.services;

import it.unipv.ingsfw.aerotrack.dao.PasseggeroDao;
import it.unipv.ingsfw.aerotrack.models.Passeggero;

import java.util.List;

public class PasseggeroService {
    private final PasseggeroDao passeggeroDao = PasseggeroDao.getInstance();

    /**
     * Aggiunge un nuovo passeggero se non esiste già.
     * @param passeggero Il passeggero da aggiungere.
     * @return true se aggiunto, false se già presente.
     */
    public boolean aggiungiPasseggero(Passeggero passeggero) {
        if (passeggeroDao.cercaPerDocumento(passeggero.getDocumento()) != null) {
            return false; // già presente
        }
        return passeggeroDao.aggiungiPasseggero(passeggero);
    }

    /**
     * Cerca un passeggero per documento.
     * @param documento Il documento del passeggero.
     * @return Il passeggero trovato o null.
     */
    public Passeggero cercaPerDocumento(String documento) {
        return passeggeroDao.cercaPerDocumento(documento);
    }

    /**
     * Restituisce la lista di tutti i passeggeri.
     * @return Lista passeggeri.
     */
    public List<Passeggero> getTuttiPasseggeri() {
        return passeggeroDao.getTuttiPasseggeri();
    }
}