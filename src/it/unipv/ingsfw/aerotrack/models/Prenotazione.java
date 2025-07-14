package it.unipv.ingsfw.aerotrack.models;

import java.util.Objects;

/**
 * Classe che rappresenta una prenotazione di un passeggero per un volo.
 * Gestisce le informazioni del passeggero e lo stato della prenotazione.
 */
public class Prenotazione {
	
	// Attributi
	private final Passeggero passeggero;
    private final Volo volo;
    private boolean cancellata;
    private final String codicePrenotazione;
    private static int contatoreCodici = 1000;
    
    /**
     * Costruttore per creare una nuova prenotazione.
     *
     * @param passeggero Passeggero che effettua la prenotazione
     * @param volo Volo prenotato
     * @throws IllegalArgumentException se il passeggero è null
     */
    public Prenotazione(Passeggero passeggero, Volo volo) {
        if (passeggero == null) 
        	throw new IllegalArgumentException("Il passeggero non può essere null");
        this.passeggero = passeggero;
        this.volo = volo;
        this.cancellata = false;
        synchronized (Prenotazione.class) {
            this.codicePrenotazione = "PR" + (contatoreCodici++);
        }
    }
    
    /**
     * Costruttore per creare una nuova prenotazione.
     * 
     * @param passeggero Passeggero che effettua la prenotazione
     * @throws IllegalArgumentException se il passeggero è null
     */
	public Prenotazione (String codicePrenotazione, Passeggero passeggero, Volo volo, boolean cancellata) {
		// Validazione del parametro
        if(passeggero == null) {
            throw new IllegalArgumentException("Il passeggero non può essere null");
        }
        
        // Inizializzazione degli attributi
        this.codicePrenotazione = codicePrenotazione;
		this.passeggero = passeggero;
		this.volo = volo;
		this.cancellata = cancellata;
	}
	
	// Aggiorna il contatore al massimo valore trovato in DB
    public static void aggiornaContatore(int nuovoValore) {
        contatoreCodici = nuovoValore;
    }
    
	 /**
     * Verifica se la prenotazione è stata cancellata.
     * 
     * @return true se cancellata, false se attiva
     */
	public boolean isCancellata() {
		return cancellata;
	}
	
	/**
     * Restituisce il passeggero associato alla prenotazione.
     * 
     * @return Passeggero della prenotazione
     */
	public Passeggero getPasseggero() {
		return passeggero;
	}
	
	/**
     * Restituisce il codice univoco della prenotazione.
     * 
     * @return Codice prenotazione
     */
    public String getCodicePrenotazione() {
        return codicePrenotazione;
    }
    
	public Volo getVolo() {
		return volo;
	}
	
	 /**
     * Cancella la prenotazione.
     * Una volta cancellata, la prenotazione non può essere riattivata.
     */
    public void cancella() {
        this.cancellata = true;
    }

    /**
     * Restituisce una rappresentazione testuale della prenotazione.
     * Include codice prenotazione, nome passeggero e stato.
     * 
     * @return Stringa descrittiva della prenotazione
     */
    @Override
    public String toString() {
        String stato = cancellata ? "CANCELLATA" : "ATTIVA";
        return String.format("Prenotazione %s - %s [%s]", codicePrenotazione, passeggero.getNomeCompleto(), stato);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) 
        	return true;
        if (!(o instanceof Prenotazione p)) 
        	return false;
        return codicePrenotazione.equals(p.codicePrenotazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codicePrenotazione);
    }
}