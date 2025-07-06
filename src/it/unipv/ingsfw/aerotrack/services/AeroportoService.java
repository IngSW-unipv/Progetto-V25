package it.unipv.ingsfw.aerotrack.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import it.unipv.ingsfw.aerotrack.dao.AeroportoDao;
import it.unipv.ingsfw.aerotrack.dao.DBConnection;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.utils.CSVUtils;

/**
 * Service Singleton per la logica di business relativa agli aeroporti.
 * Si occupa di validare i dati e delegare la persistenza al DAO.
 */
public class AeroportoService {
	
	private static AeroportoService instance;
	private final AeroportoDao aeroportoDao;
    
	private AeroportoService() {
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    /** @return istanza singleton */
    public static AeroportoService getInstance() {
        if (instance == null) instance = new AeroportoService();
        return instance;
    }
    
    public void svuotaAeroporti() {
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM aeroporti");
            System.out.println("Tabella aeroporti svuotata.");
        } catch (SQLException e) {
            System.err.println("Errore durante lo svuotamento della tabella aeroporti: " + e.getMessage());
        }
    }
    
    /**
     * Aggiunge un nuovo aeroporto dopo validazione dei parametri.
     * 
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public void aggiungiAeroporto(String codice, String nome, double latitudine, double longitudine, int numeroPiste) {
        if (codice == null || codice.length() != 3) {
            throw new IllegalArgumentException("Codice aeroporto deve essere di 3 caratteri");
        }
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome aeroporto non può essere vuoto");
        }
        if (numeroPiste <= 0 || numeroPiste > 10) {
            throw new IllegalArgumentException("Numero piste deve essere tra 1 e 10");
        }
        codice = codice.toUpperCase();
        Aeroporto a = new Aeroporto(codice, nome, latitudine, longitudine, numeroPiste);
        aeroportoDao.aggiungiAeroporto(a);
    }    
    
    /**
     * Cerca aeroporto per codice.
     */
    public Aeroporto cercaAeroporto(String codice) {
        return aeroportoDao.cercaPerCodice(codice);
    }
    
    /**
     * Esporta tutti gli aeroporti su file CSV.
     */
    public void esportaAeroportiCSV(String filePath) throws IOException {
        CSVUtils.esportaAeroporti(getTuttiAeroporti(), filePath);
    }

    /**
     * Importa aeroporti da file CSV.
     * Gli aeroporti importati vengono aggiunti tramite il service (con validazione).
     */
    public void importaAeroportiCSV(String filePath) throws IOException {
        List<Aeroporto> importati = CSVUtils.importaAeroporti(filePath);
        for (Aeroporto a : importati) {
            aggiungiAeroporto(a.getCodice(), a.getNome(), a.getLatitudine(), a.getLongitudine(), a.getNumeroPiste());
        }
    }
    
    /**
     * Restituisce tutti gli aeroporti dal database.
     */
    public List<Aeroporto> getTuttiAeroporti() {
        return aeroportoDao.getTuttiAeroporti();
    }
    
    /**
     * Rimuove un aeroporto per codice.
     * 
     * @param codice Codice aeroporto.
     * @return true se rimosso, false altrimenti.
     */
    public boolean rimuoviAeroporto(String codice) {
        return aeroportoDao.rimuoviAeroporto(codice);
    }
}