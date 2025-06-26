package it.unipv.ingsfw.aerotrack.models;

/**
 * Classe che rappresenta una prenotazione di un passeggero per un volo.
 * Gestisce le informazioni del passeggero e lo stato della prenotazione.
 */
public class Prenotazione {
	
	// Attributi
	private Passeggero passeggero;      // Passeggero associato alla prenotazione
	private Volo volo;
	private boolean cancellata;         // Stato della prenotazione: true se cancellata, false se attiva
    private String codicePrenotazione;  // Codice univoco della prenotazione (generato automaticamente)
    private static int contatoreCodici = 1000; // Contatore statico per generare codici prenotazione unici
   
    /**
     * Costruttore per creare una nuova prenotazione.
     * Inizializza la prenotazione come attiva e genera un codice univoco.
     * 
     * @param passeggero Passeggero che effettua la prenotazione
     * @throws IllegalArgumentException se il passeggero è null
     */
	public Prenotazione (Passeggero passeggero, Volo volo) {
		// Validazione del parametro
        if(passeggero == null) {
            throw new IllegalArgumentException("Il passeggero non può essere null");
        }
        
        // Inizializzazione degli attributi
		this.passeggero = passeggero;
		this.volo = volo;
		this.cancellata = false;
	    this.codicePrenotazione = "PR" + (contatoreCodici++);  // Generazione codice prenotazione univoco
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
        return String.format("Prenotazione %s - %s [%s]", 
                           codicePrenotazione,
                           passeggero.getNomeCompleto(),
                           stato);
    }
}