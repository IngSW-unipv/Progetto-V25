package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
import it.unipv.ingsfw.aerotrack.dao.AeroportoDao;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Service che gestisce la logica di business relativa agli aeroporti.
 * Si occupa di validare i dati e delegare la persistenza al DAO.
 */
public class AeroportoService {
	
	private final AeroportoDao aeroportoDao;
    
    public AeroportoService() {
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    /**
     * Aggiungo un nuovo aeroporto con validazione.dei parametri.
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public void aggiungiAeroporto(String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
        if (codice == null || codice.length() != 3) {
            throw new IllegalArgumentException("Codice aeroporto deve essere di 3 caratteri");
        }
        
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome aeroporto non può essere vuoto");
        }
        
        if (numeroPiste <= 0 || numeroPiste > 10) {
            throw new IllegalArgumentException("Numero piste deve essere tra 1 e 10");
        }
        
        codice = codice.toUpperCase();
        Aeroporto aeroporto = new Aeroporto(codice, nome, latitudine, longitudine, numeroPiste);
        aeroportoDao.aggiungiAeroporto(aeroporto);
    }    
    
    /**
     * Cerco aeroporto per codice
     */
    public Aeroporto cercaAeroporto(String codice) {
        return aeroportoDao.cercaPerCodice(codice);
        }
      
    /**
     * Restituisce tutti gli aeroporti dal database.
     */
    public List<Aeroporto> getTuttiAeroporti() {
        return aeroportoDao.getTuttiGliAeroporti();
    }
   
}


