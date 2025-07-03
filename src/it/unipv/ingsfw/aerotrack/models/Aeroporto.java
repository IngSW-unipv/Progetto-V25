package it.unipv.ingsfw.aerotrack.models;

import it.unipv.ingsfw.aerotrack.utils.CalcolaDistanza;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Rappresenta un aeroporto con codice IATA, nome, coordinate geografiche e numero di piste.
 * Gestisce i voli in partenza e in arrivo, e lo stato corrente delle piste.
 */
public class Aeroporto {  
	
	// Attributi
	private final String codice;
    private final String nome;
    private final double latitudine;
    private final double longitudine;
    private final int numeroPiste;
    private final Volo[] piste; 
    private final List<Volo> voliInPartenza;
    private final List<Volo> voliInArrivo;
    
    
    /**
     * Costruttore per creare un nuovo aeroporto.
     * 
     * @param codice       Codice IATA (3 lettere)
     * @param nome         Nome aeroporto
     * @param latitudine   Latitudine [-90,+90]
     * @param longitudine  Longitudine [-180,+180]
     * @param numeroPiste  Numero piste (>0)
     * @throws IllegalArgumentException se parametri non validi
     */
    
	public Aeroporto (String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
		
		// Validazione dei parametri di input
        if (codice == null || codice.length() != 3) {
        	throw new IllegalArgumentException("Codice IATA invalido");  
        }
        
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome aeroporto vuoto");
        }
        
        if (latitudine < -90 || latitudine > 90) {
            throw new IllegalArgumentException("Latitudine fuori range");
        }
        
        if (longitudine < -180 || longitudine > 180) {
            throw new IllegalArgumentException("Longitudine fuori range");
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
		this.piste = new Volo[numeroPiste]; 
        this.voliInPartenza = new ArrayList<>();     
        this.voliInArrivo = new ArrayList<>();
	}
	
	
	/**
     * Restituisce il codice IATA dell'aeroporto.
     * 
     * @return Codice dell'aeroporto 
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
     * @return Stato piste (array di voli, null se libera)
     */
	public Volo[] getPiste() {
		return piste.clone();
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
   
   /** Libera la pista specificata */
   public void liberaPista(int indicePista) {
       if (indicePista < 0 || indicePista >= numeroPiste) 
    	   throw new IllegalArgumentException("Indice pista non valido");
       piste[indicePista] = null;
   }

   /** Occupa una pista con il volo dato. */
   public boolean occupaPista(int indicePista, Volo v) {
       if (indicePista < 0 || indicePista >= numeroPiste) 
    	   throw new IllegalArgumentException("Indice pista non valido");
       if (v == null) throw new IllegalArgumentException("Volo null");
       if (piste[indicePista] != null) 
    	   throw new IllegalStateException("Pista già occupata");
       piste[indicePista] = v;
       return true;
   }
   
   /** Aggiunge un volo in partenza */
   public void aggiungiVoloInPartenza(Volo v) {
       if (v != null && !voliInPartenza.contains(v)) {
           voliInPartenza.add(v);
       }
   }

   /**
    * Aggiunge un volo in arrivo.
    */
   public void aggiungiVoloInArrivo(Volo v) {
       if (v != null && !voliInArrivo.contains(v)) {
           voliInArrivo.add(v);
       }
   }
   
   
   // Metodi Utils
   
   /**
    * Calcola la distanza in km da un altro aeroporto
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
	   for (Volo v : piste) if (v == null) libere++;
	   return String.format("Aeroporto{codice='%s', nome='%s', coordinate=(%.4f, %.4f), piste=%d, pisteLibere=%d}",
	           codice, nome, latitudine, longitudine, numeroPiste, libere);
	}
   
   @Override
   public boolean equals(Object o) {
       if (this == o) 
    	   return true;
       if (!(o instanceof Aeroporto a)) 
    	   return false;
       return codice.equals(a.codice);
   }

   @Override
   public int hashCode() {
       return Objects.hash(codice);
   }

}

