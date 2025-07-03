package it.unipv.ingsfw.aerotrack.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe per la gestione della connessione al database per il progetto Aerotrack.
 * Contiene metodi per aprire, chiudere e verificare lo stato della connessione al database.
 * La connessione viene configurata tramite un file di proprietà.
 */
public class DBConnection {
	// Percorso del file di configurazione
    private static final String CONFIG_FILE = "properties/dbconfig.properties";

    /**
     * Metodo per ottenere una nuova connessione al database leggendo i parametri dal file di configurazione.
     * 
     * @param schema Il nome dello schema (database) da utilizzare
     * @return La connessione pronta all'uso, oppure null in caso di errore.
     */
    public static Connection startConnection(String schema) {
        Properties props = new Properties();

        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            // Carica le proprietà dal file di configurazione
            props.load(inputStream);

            String dbUrl = props.getProperty("db.url") + "/" + schema + "?serverTimezone=Europe/Rome";
            String dbUsername = props.getProperty("db.username");
            String dbPassword = props.getProperty("db.password");
            String dbDriver = props.getProperty("db.driver");

            // Carica il driver JDBC
            Class.forName(dbDriver);

            // Crea e restituisce la connessione
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Connessione al database avvenuta con successo!");
            return conn;

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Chiude la connessione, se aperta.
     * 
     * @param conn la connessione da chiudere
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connessione chiusa.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}