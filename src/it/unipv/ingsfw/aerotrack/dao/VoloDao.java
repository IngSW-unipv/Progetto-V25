package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Aeroporto;

public class VoloDao {
	private static VoloDao instance;
	private Connection connection;
	private AeroportoDao aeroportoDao;
	//Creazione della tabella voli con foreign key verso aeroporti
	
	private VoloDao() {
		try {
			 connection = DriverManager.getConnection("jdbc:sqlite:aeroporti.db");
	         aeroportoDao = AeroportoDao.getInstance();
	         Statement stmt = connection.createStatement();
	         stmt.executeUpdate("CREATE TABLE IF NOT EXISTS voli (codice TEXT PRIMARY KEY, partenza TEXT, destinazione TEXT, FOREIGN KEY(partenza) REFERENCES aeroporti(codice), FOREIGN KEY(destinazione) REFERENCES aeroporti(codice))");
	    } catch (SQLException e) {
	         e.printStackTrace();
	    }
	}
	
	public static VoloDao getInstance() {
		if (instance == null) {
			instance = new VoloDao();
		}
		return instance;
	}
	
	//Metodo aggiungiVolo() per inserire nuovi voli
	public void aggiungiVolo(Volo v) {
		try {
            PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO voli VALUES (?, ?, ?)");
            ps.setString(1, v.getCodice());
            ps.setString(2, v.getPartenza().getCodice());
            ps.setString(3, v.getDestinazione().getCodice());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    //Metodo getTuttiVoli() per ottenere tutti i voli esistenti
	public List<Volo> getTuttiVoli(){
		List<Volo> lista = new ArrayList<>();
	    try {
	    	Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM voli");
	        while (rs.next()) {
	        	Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
	            Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
	            lista.add(new Volo(rs.getString("codice"), partenza, destinazione));
	        } 
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return lista;
    }
	
	//* Metodo cercaPerCodice() per trovare un volo dato il codice
	public Volo cercaPerCodice(String codice) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM voli WHERE codice = ?");
            ps.setString(1, codice);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	Aeroporto partenza = aeroportoDao.cercaPerCodice(rs.getString("partenza"));
                Aeroporto destinazione = aeroportoDao.cercaPerCodice(rs.getString("destinazione"));
                return new Volo(rs.getString("codice"), partenza, destinazione);
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	public boolean rimuoviVolo(String codice) {
		Volo toRemove = cercaPerCodice(codice);
		if (toRemove != null) {
			voli.remove(toRemove);
			return true;
		}
		return false;
	}
}


//cosa ne pensi??Lo faresti diversamente??
