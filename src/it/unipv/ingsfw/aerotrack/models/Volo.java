package it.unipv.ingsfw.aerotrack.models;

import java.util.ArrayList;
import java.util.List;

public class Volo { //consideriamo thread per visualizzare tabellone in tempo reale?
	
	private String codice;
	private Aeroporto partenza;
	private Aeroporto destinazione;
	private double velocità;
	private double orarioPartenza;
	private List<Prenotazione> prenotazioni;
	private double ritardo;
	
	public Volo(String codice, Aeroporto partenza, Aeroporto destinazione) {
		this.codice = codice;
		this.partenza = partenza;
		this.destinazione = destinazione;
		this.prenotazioni = new ArrayList<>();
		for(int i=0; i<partenza.getNumeroPiste(); i++) {
			if(partenza.getPistaOccupata()[i]==null) { 
				partenza.occupaPista(i, this);           //così o facciamo metodo in Aeroporto? Forse meglio metodo in Aeroporto? ma mi fido di te
				this.ritardo=0;
				break;
			}
			if(i==partenza.getNumeroPiste()) this.ritardo=calcolaRitardo();
		}
		
	}
	
	public String getCodice() {
		return codice;
	}
	
	public Aeroporto getPartenza() {
		return partenza;
	}
	
	public Aeroporto getDestinazione() {
	        return destinazione;
	    }
	
	public double getOrarioPartenza() {
		return orarioPartenza;
	}
	
	public double getRitardo() {
		return ritardo;
	}

	public List<Prenotazione> getPrenotazioni() {
	        return prenotazioni;
	    }

	public void aggiungiPrenotazione(Prenotazione p) {
	    	prenotazioni.add(p);	
	    }
	    
	public void cancellaPrenotazione(String documento) {
        for (Prenotazione pr : prenotazioni) {
            if (pr.getPasseggero().getDocumento().equals(documento)) {
                pr.cancellata();
            }
        }
    }

	
    public double getDistanzaKm() {
        return partenza.calcolaDistanza(destinazione);
    }
    
    public double calcolaTempo() {
    	return (getDistanzaKm() / velocità) + ritardo;
    }
	
    public double calcolaRitardo() {
    	double minRitardo=0;
    	for(int i=0; i<partenza.getNumeroPiste(); i++) {
    		if(orarioPartenza-partenza.getPistaOccupata()[i].getOrarioPartenza()<minRitardo) minRitardo=orarioPartenza-partenza.getPistaOccupata()[i].getOrarioPartenza();
    	}
    	return minRitardo;
    }
    
    //  public void stampaDettagliVolo() {}
    

}
