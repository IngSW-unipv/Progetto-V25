package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
import java.util.stream.Collectors;
import it.unipv.ingsfw.aerotrack.dao.*;
import it.unipv.ingsfw.aerotrack.models.*;

public class VoloService {
	
	private VoloDao voloDao;
    private AeroportoDao aeroportoDao;
    
    public VoloService() {
        this.voloDao = VoloDao.getInstance();
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    /**
     * Creo un nuovo volo con validazione.
     */
    public void creaVolo(String codice, String codicePartenza, String codiceDestinazione, 
                        double orario, double velocita) {
        
        // Validazioni
        if (codice == null || codice.trim().isEmpty()) {
            throw new IllegalArgumentException("Codice volo non può essere vuoto");
        }
        
        if (codicePartenza.equals(codiceDestinazione)) {
            throw new IllegalArgumentException("Aeroporto di partenza e destinazione non possono essere uguali");
        }
        
        if (velocita <= 0 || velocita > 1200) {
            throw new IllegalArgumentException("Velocità deve essere tra 1 e 1200 km/h");
        }
    }
    
    /**
     * Cerca un volo per codice.
     */
    public Volo cercaVolo(String codice) {
        Volo volo = voloDao.cercaPerCodice(codice);
        return volo;
    }
    
    /**
     * Restituisce tutti i voli.
     */
    public List<Volo> getTuttiVoli() {
        return voloDao.getTuttiVoli();
    }
    
    /**
     * Trova voli per aeroporto di partenza.
     */
    public List<Volo> trovaVoliPerPartenza(String codiceAeroporto) {
        return voloDao.getTuttiVoli().stream()
                .filter(volo -> volo.getPartenza().getCodice().equalsIgnoreCase(codiceAeroporto))
                .collect(Collectors.toList());
    }
    
    /**
     * Trova voli per aeroporto di destinazione.
     */
    public List<Volo> trovaVoliPerDestinazione(String codiceAeroporto) {
        return voloDao.getTuttiVoli().stream()
                .filter(volo -> volo.getDestinazione().getCodice().equalsIgnoreCase(codiceAeroporto))
                .collect(Collectors.toList());
    }
   
    /**
     * Rimuove un volo dal sistema.
     */
    public boolean rimuoviVolo(String codice) {
        return voloDao.rimuoviVolo(codice);
    }

}
