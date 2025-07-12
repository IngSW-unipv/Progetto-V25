package it.unipv.ingsfw.aerotrack.services;

import it.unipv.ingsfw.aerotrack.dao.DBConnection;
import it.unipv.ingsfw.aerotrack.dao.PasseggeroDao;
import it.unipv.ingsfw.aerotrack.models.Passeggero;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PasseggeroService {
	private static PasseggeroService instance; 
	private final PasseggeroDao passeggeroDao = PasseggeroDao.getInstance();


    private PasseggeroService() {}

    /** @return istanza singleton */
    public static PasseggeroService getInstance() {
        if (instance == null) {
            synchronized (PasseggeroService.class) {
                if (instance == null) instance = new PasseggeroService();
            }
        }
        return instance;
    }

    
    /**
     * Aggiunge un nuovo passeggero se non esiste già.
     * Se esiste già, controlla che nome e cognome coincidano.
     * Se non coincidono, blocca la prenotazione.
     * @param passeggero Il passeggero da aggiungere.
     * @return true se aggiunto, false se già presente.
     */
    public boolean aggiungiPasseggero(Passeggero passeggero) {
    	Passeggero esistente = passeggeroDao.cercaPerDocumento(passeggero.getDocumento());
        if (esistente != null) {
            if (!esistente.getNome().equals(passeggero.getNome()) ||
                !esistente.getCognome().equals(passeggero.getCognome())) {
            	throw new IllegalArgumentException("Il codice fiscale risulta già associato a un'altra persona (" +
                        esistente.getNome() + " " + esistente.getCognome() + "). Impossibile procedere.");
            }
            return false; // già presente, dati coincidono
        }
        return passeggeroDao.aggiungiPasseggero(passeggero);
    }
    
    public void svuotaPasseggeri() {
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM passeggeri");
            System.out.println("Tabella passeggeri svuotata.");
        } catch (SQLException e) {
            System.err.println("Errore durante lo svuotamento della tabella passeggeri: " + e.getMessage());
        }
    }

    /**
     * Cerca un passeggero per documento.
     * @param documento Il documento del passeggero.
     * @return Il passeggero trovato o null.
     */
    public Passeggero cercaPerDocumento(String documento) {
        return passeggeroDao.cercaPerDocumento(documento);
    }

    /**
     * Restituisce la lista di tutti i passeggeri.
     * @return Lista passeggeri.
     */
    public List<Passeggero> getTuttiPasseggeri() {
        return passeggeroDao.getTuttiPasseggeri();
    }
}