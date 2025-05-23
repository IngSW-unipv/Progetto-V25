package it.unipv.ingsfw.aerotrack.models;

public class Passeggero {
	private String nome;
	private String cognome;
	private String documento;
	//  private List<Bagaglio> bagagli;   
	
	public Passeggero(String nome, String cognome, String documento) {
		this.nome = nome;
		this.cognome = cognome;
		this.documento = documento;
		//this.bagagli = new ArrayList<>();
	}
	
	public String getNomeCompleto() {
		return nome + "" + cognome;
	}
	
	public String getDocumento() {
		return documento;
	}
	
//	public void aggiungiBagaglio      MA quindi mettiamo bagagli come classe o no secondo te?

}