package it.unipv.ingsfw.aerotrack.test;

import java.util.List;

import it.unipv.ingsfw.aerotrack.models.Passeggero;
import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.utils.*;

public class VoloTestConsole {
	public void stampaDettagliVolo(Volo volo, List<Prenotazione> prenotazioni) {     
    	System.out.println("Volo " + volo.getCodice());
    	System.out.println("Da: " + volo.getPartenza().getNome() + "-> A: " + volo.getDestinazione().getNome());
    	System.out.println("Distanza: " + CalcolaDistanza.calcolaDistanza(volo.getPartenza(),volo.getDestinazione()) + "km");
    	System.out.println("Prenotazioni attive:");
    	for (Prenotazione pr: prenotazioni) {
    		if (!pr.isCancellata()) {
    			Passeggero p = pr.getPasseggero();
    			System.out.println(" - " + p.getNomeCompleto() + " (" + p.getDocumento() + ")");
    		}
    	}
	} 	

}
