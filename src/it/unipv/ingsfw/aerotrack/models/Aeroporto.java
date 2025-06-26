package it.unipv.ingsfw.aerotrack.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un aeroporto nel sistema di gestione voli.
 * Un aeroporto è caratterizzato da un codice identificativo, coordinate geografiche
 * e un numero di piste disponibili per i voli.
 * 
 * La classe implementa funzionalità per:
 * - Gestione delle piste occupate
 * - Calcolo della distanza tra aeroporti usando la formula di Haversine
 * - Controllo della disponibilità delle piste
 */


public class Aeroporto {  
	
	private String codice;                     // Codice IATA dell'Aeroporto
	private String nome;                       // Nome completo dell'aeroporto
	private double latitudine;                 // Latitudine dell'aeroporto in gradi decimali
	private double longitudine;                // Longitudine dell'aeroporto in gradi decimali
	private int numeroPiste;                   // Numero totale di piste disponibili nell'aeroporto
	private Volo[] pistaOccupata;              /* Rappresenta lo stato di occupazione delle piste.
	                                              Se pistaOccupata[i] è null, la pista i è libera.
                                                  Se contiene un oggetto Volo, la pista è occupata da quel volo.
                                               */
	
	private List<Volo> voliInPartenza;         // Lista dei voli in partenza da questo aeroporto
    private List<Volo> voliInArrivo;           // Lista dei voli in arrivo a questo aeroporto
     
    
    
    /**
     * Costruttore per creare un nuovo aeroporto.
     * 
     *  * @param codice Codice IATA dell'aeroporto (deve essere di 3 caratteri)
     * @param nome Nome completo dell'aeroporto
     * @param latitudine Latitudine in gradi decimali (range: -90 a +90)
     * @param longitudine Longitudine in gradi decimali (range: -180 a +180)
     * @param numeroPiste Numero di piste disponibili (deve essere > 0)
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    
	public Aeroporto (String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
		
		 // Validazione dei parametri di input
        if (codice == null || codice.length() != 3) {
        	/**Il codice IATA (International Air Transport Association) è un codice di tre lettere assegnato a ogni aeroporto nel mondo. 
        	 * Serve per identificare in modo univoco gli aeroporti nei biglietti aerei, nei sistemi di prenotazione, 
        	 * nelle etichette dei bagagli e nei pannelli informativi.
        	 */
            throw new IllegalArgumentException("Il codice aeroporto deve essere di 3 caratteri");  
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'aeroporto non può essere vuoto");
        }
        if (latitudine < -90 || latitudine > 90) {
            throw new IllegalArgumentException("La latitudine deve essere tra -90 e +90 gradi");
        }
        if (longitudine < -180 || longitudine > 180) {
            throw new IllegalArgumentException("La longitudine deve essere tra -180 e +180 gradi");
        }
        if (numeroPiste <= 0) {
            throw new IllegalArgumentException("Il numero di piste deve essere maggiore di 0");
        }
        
        // Inizializzazione degli attributi
        this.codice = codice.toUpperCase(); // Standardizza in maiuscolo (i codici IATA ufficiali sono sempre scritti in maiuscolo);
		this.nome = nome;
		this.latitudine = latitudine;
		this.longitudine = longitudine;
		this.numeroPiste = numeroPiste;
		this.pistaOccupata = new Volo[numeroPiste];  // Inizializza l'array delle piste come tutte libere (null)
        this.voliInPartenza = new ArrayList<>();     // Inizializza le liste dei voli
        this.voliInArrivo = new ArrayList<>();
	}
	
	
	/**
     * Restituisce il codice IATA dell'aeroporto.
     * 
     * @return Codice dell'aeroporto (es. "MXP")
     */
	public String getCodice() {
		return codice;
	}
	
	/**
     * Restituisce il nome completo dell'aeroporto.
     * 
     * @return Nome dell'aeroporto
     */
	public String getNome() {
		return nome;
	}
	
	/**
     * Restituisce la latitudine dell'aeroporto in gradi decimali.
     * 
     * @return Latitudine dell'aeroporto
     */
	public double getLatitudine() {
		return latitudine;
	}
	
	/**
     * Restituisce la longitudine dell'aeroporto in gradi decimali.
     * 
     * @return Longitudine dell'aeroporto
     */
	public double getLongitudine() {
		return longitudine;
	}
	
	/**
     * Restituisce il numero totale di piste dell'aeroporto.
     * 
     * @return Numero di piste disponibili
     */
	public int getNumeroPiste() {
		return numeroPiste;
	}
	
	/**
     * Restituisce l'array che rappresenta lo stato delle piste.
     * 
     * @return Array delle piste occupate
     */
	public Volo[] getPistaOccupata() {
		return pistaOccupata;
	}
	
	
	/**
    * Restituisce la lista dei voli in partenza da questo aeroporto.
    * 
    * @return Lista dei voli in partenza
    */
   public List<Volo> getVoliInPartenza() {
       return new ArrayList<>(voliInPartenza); 
   }
   
   /**
    * Restituisce la lista dei voli in arrivo a questo aeroporto.
    * 
    * @return Lista dei voli in arrivo
    */
   public List<Volo> getVoliInArrivo() {
       return new ArrayList<>(voliInArrivo);
   }
   
   /**
    * Occupa una pista con un volo.
    * Verifica che l'indice sia valido e che la pista sia libera.
    * 
    * @param indicePista Indice della pista da occupare (0-based)
    * @param volo Volo che occuperà la pista
    * @return true se l'operazione è riuscita, false altrimenti
    * @throws IllegalArgumentException se l'indice non è valido
    * @throws IllegalStateException se la pista è già occupata
    */
   public boolean occupaPista(int indicePista, Volo volo) {
       // Validazione dell'indice della pista
       if (indicePista < 0 || indicePista >= numeroPiste) {
           throw new IllegalArgumentException("Indice pista non valido: " + indicePista);
       }
       
       // Verifica che la pista sia libera
       if (pistaOccupata[indicePista] != null) {
           throw new IllegalStateException("La pista " + indicePista + " è già occupata");
       }
       
       // Verifica che il volo non sia null
       if (volo == null) {
           throw new IllegalArgumentException("Il volo non può essere null");
       }
       
       // Occupa la pista
       pistaOccupata[indicePista] = volo;
       return true;
   }
   
   /**
    * Restituisce una rappresentazione testuale dell'aeroporto.
    * 
    * @return Stringa che descrive l'aeroporto
    */
   @Override
   public String toString() {
       return String.format("Aeroporto{codice='%s', nome='%s', coordinate=(%.6f, %.6f), piste=%d, pisteLibere=%d}",
               codice, nome, latitudine, longitudine, numeroPiste);
   }
}
