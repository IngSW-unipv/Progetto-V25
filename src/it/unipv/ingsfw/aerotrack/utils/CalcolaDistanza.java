package it.unipv.ingsfw.aerotrack.utils;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;

public class CalcolaDistanza {
	
	/*metodo statico che calcola la distanza tra due aeroporti usando la 
	 * formula dell’Haversine (distanza in km sulla superficie terrestre).
	 */
	
	private static final double R = 6371.0; // Raggio della Terra in km
			
	public static double calcolaDistanza(Aeroporto a1, Aeroporto a2) {
		double lat1 = Math.toRadians(a1.getLatitudine());
		double lon1 = Math.toRadians(a1.getLongitudine());
		double lat2 = Math.toRadians(a2.getLatitudine());
		double lon2 = Math.toRadians(a2.getLongitudine());
		
		double dLat = lat2 - lat1;
		double dLon = lon2 - lon1;
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(lat1) * Math.cos(lat2) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;
	}

}
