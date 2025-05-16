package com.EsameJava.core;

public class Prenotazione {
	
	private Passeggero passeggero;
	private boolean cancellata;
	
	public Prenotazione (Passeggero passeggero) {
		this.passeggero = passeggero;
		this.cancellata = false;
	}
	
	public void cancellata() {
		this.cancellata = true;
	}
	
	public boolean isCancellata() {
		return cancellata;
	}
	
	public Passeggero getPasseggero() {
		return passeggero;
	}

}
