package it.unipv.ingsfw.aerotrack.models;

import it.unipv.ingsfw.aerotrack.utils.CalcolaDistanza;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe che rappresenta un aeroporto nel sistema di gestione voli Aerotrack.
 * Un aeroporto è caratterizzato da un codice identificativo, coordinate geografiche
 * e un numero di piste disponibili per i voli.
 */
public class Aeroporto {  
	
	// Attributi
	private final String codice; // Codice IATA, maiuscolo (es: MXP)
    private final String nome;
    private final double latitudine;
    private final double longitudine;
    private final int numeroPiste;
    private final Volo[] pistaOccupata; // Stato di occupazione delle piste
    private final List<Volo> voliInPartenza;
    private final List<Volo> voliInArrivo;
    
    
    /**
     * Costruttore per creare un nuovo aeroporto.
     * 
     * @param codice Codice IATA dell'aeroporto (deve essere di 3 caratteri)
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
        
        if (nome == null || nome.isEmpty()) {
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
        this.codice = codice.toUpperCase(); // i codici IATA ufficiali sono sempre scritti in maiuscolo;
		this.nome = nome;
		this.latitudine = latitudine;
		this.longitudine = longitudine;
		this.numeroPiste = numeroPiste;
		this.pistaOccupata = new Volo[numeroPiste]; 
        this.voliInPartenza = new ArrayList<>();     
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
    * Aggiunge un volo alla lista dei voli in partenza da questo aeroporto.
    */
   public void aggiungiVoloInPartenza(Volo v) {
       if (v != null && !voliInPartenza.contains(v)) {
           voliInPartenza.add(v);
       }
   }

   /**
    * Aggiunge un volo alla lista dei voli in arrivo a questo aeroporto.
    */
   public void aggiungiVoloInArrivo(Volo v) {
       if (v != null && !voliInArrivo.contains(v)) {
           voliInArrivo.add(v);
       }
   }
   
   
   // METODI PER GESTIONE PISTE
   
   /**
    * Occupa una pista con un volo.
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
   
    
   // Metodi Utils
   
   /**
    * Calcola la distanza tra questo aeroporto e un altro
    * utilizzando la formula di Haversine.
    */
   public double calcolaDistanza(Aeroporto altroAeroporto) {
       if (altroAeroporto == null) {
           throw new IllegalArgumentException("L'aeroporto di destinazione non può essere null");
       }
       return CalcolaDistanza.calcolaDistanza(this, altroAeroporto);
   }
   
   
   /**
    * Restituisce una rappresentazione testuale dell'aeroporto.
    */
   @Override
   public String toString() {
	   int libere = 0;
	   for (Volo v : pistaOccupata) if (v == null) libere++;
	   return String.format("Aeroporto{codice='%s', nome='%s', coordinate=(%.6f, %.6f), piste=%d, pisteLibere=%d}",
	           codice, nome, latitudine, longitudine, numeroPiste, libere);
	}
   
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       Aeroporto that = (Aeroporto) o;
       return codice.equals(that.codice);
   }

   @Override
   public int hashCode() {
       return codice.hashCode();
   }

}

//Aggiungieresti un metodo per liberare una pista (per liberare una pista quando un volo parte o atterra)??
