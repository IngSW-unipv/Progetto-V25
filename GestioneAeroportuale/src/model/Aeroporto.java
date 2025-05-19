package model;

import ProvaEsame.Aeroporto;
import ProvaEsame.Volo;

public class Aeroporto {
	
    private String codice;
	private String nome;
	private double latitudine;
	private double longitudine;
	private int numeroPiste; // Sia per decollo che per atterraggio o dividiamo?
	private Volo[] pistaOccupata;  
	
	public Aeroporto (String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
		this.codice = codice;
		this.nome = nome;
		this.latitudine = latitudine;
		this.longitudine = longitudine;
		this.numeroPiste = numeroPiste;
	}
	
	public String getCodice() {
		return codice;
	}
	
	public String getNome() {
		return nome;
	}
	
	public double getLatitudine() {
		return latitudine;
	}
	
	public double getLongitudine() {
		return longitudine;
	}
	
	public int getNumeroPiste() {
		return numeroPiste;
	}
	
	public Volo[] getPistaOccupata() {
		return pistaOccupata;
	}
	
	public void occupaPista(int i, Volo volo) {
		pistaOccupata[i]=volo;
	}
	
	//Calcolo distanza con formula Haversine
	public double calcolaDistanza(Aeroporto altro) {
		double R = 6371.0; // Raggio della Terra in km
        double dLat = Math.toRadians(altro.latitudine - this.latitudine);
        double dLon = Math.toRadians(altro.longitudine - this.longitudine);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(this.latitudine)) * Math.cos(Math.toRadians(altro.latitudine)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

}
		

}