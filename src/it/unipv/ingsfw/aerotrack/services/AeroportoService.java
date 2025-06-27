package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
import it.unipv.ingsfw.aerotrack.dao.AeroportoDao;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

public class AeroportoService {
	
	private AeroportoDao aeroportoDao;
    
    public AeroportoService() {
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    /**
     * Aggiungo un nuovo aeroporto con validazione.
     */
    public void aggiungiAeroporto(String codice, String nome, double latitudine, 
                                 double longitudine, int numeroPiste) {
       
        if (codice == null || codice.length() != 3) {
            throw new IllegalArgumentException("Codice aeroporto deve essere di 3 caratteri");
        }
        
        if (numeroPiste <= 0 || numeroPiste > 10) {
            throw new IllegalArgumentException("Numero piste deve essere tra 1 e 10");
        }
        
        Aeroporto aeroporto = new Aeroporto(codice, nome, latitudine, longitudine, numeroPiste);
        aeroportoDao.AggiungiAeroporto(aeroporto);
    }
    
    /**
     * Cerco aeroporto per codice
     */
    public Aeroporto cercaAeroporto(String codice) {
        return aeroportoDao.cercaPerCodice(codice);
        }
      
    public List<Aeroporto> getTuttiAeroporti() {
        return aeroportoDao.getTuttiGliAeroporti();
    }
   
}


