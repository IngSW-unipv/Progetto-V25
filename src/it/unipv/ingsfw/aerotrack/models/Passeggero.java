package it.unipv.ingsfw.aerotrack.models;

/**
 * Classe che rappresenta un passeggero nel sistema aeroportuale.
 */

public class Passeggero {
	
	// Attributi
	private final String nome;                 // Nome del passeggero
	private final String cognome;              // Cognome del passeggero
	private final String documento;            // Numero del documento di identità (passaporto, carta d'identità, etc.)
	
	
	
	/**
     * Costruttore per creare un nuovo passeggero.
     * Inizializza tutti i dati personali e crea la lista bagagli.
     * 
     * @param nome Nome del passeggero
     * @param cognome Cognome del passeggero  
     * @param documento Numero documento di identità
     * @throws IllegalArgumentException se i parametri non sono validi
     */
	public Passeggero(String nome, String cognome, String documento) {
		 // Validazione dei parametri
        if(nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        
        if(cognome == null || cognome.isEmpty()) {
            throw new IllegalArgumentException("Il cognome non può essere vuoto");
        }
        
        if(documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Il documento non può essere vuoto");
        }
		
		 // Inizializzazione degli attributi
		this.nome = nome;
		this.cognome = cognome;
		this.documento = documento.toUpperCase(); // Standardizza in maiuscolo;
	}
	
	//metodi 
	/**
     * Restituisce il nome completo (nome e cognome) del passeggero.
     * 
     * @return Nome completo (nome + spazio + cognome)
     */
	public String getNomeCompleto() {
		return nome + " " + cognome;
	}
	
	/**
     * Restituisce il numero del documento di identità.
     * 
     * @return Numero documento
     */
	public String getDocumento() {
		return documento;
	}
	
	
	/**
     * Restituisce una rappresentazione testuale del passeggero.
     * Include nome completo e documento.
     */
    @Override
    public String toString() {
        return String.format("Passeggero: %s (Doc: %s)", getNomeCompleto(), documento);
    }
}