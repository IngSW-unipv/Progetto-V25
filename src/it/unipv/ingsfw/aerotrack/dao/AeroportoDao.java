package it.unipv.ingsfw.aerotrack.dao;

import java.util.ArrayList;
import java.util.List;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import java.sql.*;   //Connessione al database SQLite (aeroporti.db)....da creare

public class AeroportoDao {
	
	private static AeroportoDao instance;
	private Connection connection;
	
	private AeroportoDao() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:aeroporto.db");
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS aeroporti (codice TEXT PRIMARY KEY, nome TEXT, latitudine REAL, longitudine REAL, numeroPiste INTEGER)");
        } catch (SQLException e) {
            e.printStackTrace();
		}
	}
	
	public static AeroportoDao getInstance() {
		if (instance == null) {
			instance = new AeroportoDao();
		}
		return instance;
	}
	
	//Metodo aggiungiAeroporto() – salva un aeroporto
	public void AggiungiAeroporto(Aeroporto a) {
		try {
			 PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO aeroporti VALUES ()");
			 ps.setString(1, a.getCodice());
	         ps.setString(2, a.getNome());
	         ps.setDouble(3, a.getLatitudine());
	         ps.setDouble(4, a.getLongitudine());
	         ps.setInt(5, a.getNumeroPiste());
	         ps.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace(); 
		}
	}
	
	//Metodo getTuttiAeroporti() – recupera tutti gli aeroporti
	public List<Aeroporto> getTuttiGliAeroporti(){
		List<Aeroporto> lista = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM aeroporti");
            while (rs.next()) {
                lista.add(new Aeroporto(
                    rs.getString("codice"),
                    rs.getString("nome"),
                    rs.getDouble("latitudine"),
                    rs.getDouble("longitudine"),
                    rs.getInt("numeroPiste")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

	//Metodo cercaPerCodice() – trova un aeroporto per codice
	public Aeroporto cercaPerCodice(String codice) {
		  try {
	            PreparedStatement ps = connection.prepareStatement("SELECT * FROM aeroporti WHERE codice = ?");
	            ps.setString(1, codice);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                return new Aeroporto(
	                    rs.getString("codice"),
	                    rs.getString("nome"),
	                    rs.getDouble("latitudine"),
	                    rs.getDouble("longitudine"),
	                    rs.getInt("numeroPiste")
	                );
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	}
}


//cosa ne pensi??