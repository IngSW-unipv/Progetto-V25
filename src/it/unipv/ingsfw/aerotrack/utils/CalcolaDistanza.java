package it.unipv.ingsfw.aerotrack.utils;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;

/**
 * Classe utility per calcolare la distanza (in km) tra due aeroporti.
 * Utilizza la formula di Haversine per calcolare la distanza 
 * più breve tra due punti sulla superficie terrestre
 * (dalle loro coordinate di latitudine e longitudine).
 */
public class CalcolaDistanza {
	
	private static final double R = 6371.0; // Raggio medio della Terra in km
	
	// Costruttore privato: la classe non deve essere istanziata
    private CalcolaDistanza() {}
    
	/** 
	 * Calcola la distanza in chilometri tra due aeroporti usando la formula di Haversine.
     * 
     * Formula:
     * a = sin²(Δlat/2) + cos lat1 ⋅ cos lat2 ⋅ sin²(Δlon/2)
     * c = 2 ⋅ atan2( √a, √(1−a) )
     * d = R ⋅ c
     * 
     * @param a1 Primo aeroporto (partenza)
     * @param a2 Secondo aeroporto (destinazione)
     * @return Distanza in chilometri tra i due aeroporti
     * @throws IllegalArgumentException se uno degli aeroporti è null
     */
			
	public static double calcolaDistanza(Aeroporto a1, Aeroporto a2) {
		 // Validazione dei parametri di input
        if (a1 == null) {
            throw new IllegalArgumentException("Il primo aeroporto non può essere null");
        }
        if (a2 == null) {
            throw new IllegalArgumentException("Il secondo aeroporto non può essere null");
        }
        
        // Estrazione delle coordinate geografiche dei due aeroporti
        // Conversione delle coordinate da gradi a radianti
		double lat1 = Math.toRadians(a1.getLatitudine());
		double lon1 = Math.toRadians(a1.getLongitudine());
		double lat2 = Math.toRadians(a2.getLatitudine());
		double lon2 = Math.toRadians(a2.getLongitudine());
		
		// Calcolo delle differenze di latitudine e longitudine
		double dLat = lat2 - lat1;
		double dLon = lon2 - lon1;
		
		// Applicazione della formula di Haversine
		// a = sin²(Δlat/2) + cos lat1 ⋅ cos lat2 ⋅ sin²(Δlon/2)
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(lat1) * Math.cos(lat2) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		
		// Calcolo dell'angolo centrale in radianti
        // c = 2 ⋅ atan2( √a, √(1−a) )
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		return R * c;      // Calcolo della distanza finale moltiplicando per il raggio terrestre
	}
}