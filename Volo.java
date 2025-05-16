package com.EsameJava.core;

public class Volo {
	
	private String codice;
	private Aeroporto partenza;
	private Aeroporto destinazione;
	private double velocità;
	//private List<Passeggero> passeggeri;
	
	public Volo(String codice, Aeroporto partenza, Aeroporto destinazione) {
		this.codice = codice;
		this.partenza = partenza;
		this.destinazione = destinazione;
		//this.prenotazioni = new ArrayList<>();
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

	/*public List<Prenotazione> getPrenotazioni() {
	        return prenotazioni;
	    }
	
	public void aggiungiPrenotazione(Prenotazione p) {
	    	prenotazioni.add(p);	
	    }
	    
	public void cancellaPrenotazione(String documento) {
        for (Prenotazione pr : prenotazioni) {
            if (pr.getPasseggero().getDocumento().equals(documento)) {
                pr.cancella();
            }
        }
    }
*/
	
    public double getDistanzaKm() {
        return partenza.calcolaDistanza(destinazione);
    }
    
    //  public void stampaDettagliVolo() {}
    
}
