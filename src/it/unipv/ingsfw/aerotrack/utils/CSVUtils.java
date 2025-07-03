package it.unipv.ingsfw.aerotrack.utils;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility per importazione/esportazione Aeroporti in formato CSV.
 */
public class CSVUtils {

    /**
     * Esporta una lista di aeroporti in formato CSV.
     * 
     * @param aeroporti la lista di aeroporti
     * @param filePath percorso del file CSV
     * @throws IOException in caso di errori di scrittura
     */
    public static void esportaAeroporti(List<Aeroporto> aeroporti, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("codice,nome,latitudine,longitudine,numeroPiste");
            for (Aeroporto a : aeroporti) {
                writer.printf("%s,%s,%.6f,%.6f,%d%n",
                        a.getCodice(), a.getNome(), a.getLatitudine(), a.getLongitudine(), a.getNumeroPiste());
            }
        }
    }

    /**
     * Importa una lista di aeroporti da un file CSV.
     * 
     * @param filePath percorso del file CSV
     * @return lista di aeroporti importati
     * @throws IOException in caso di errori di lettura
     */
    public static List<Aeroporto> importaAeroporti(String filePath) throws IOException {
        List<Aeroporto> aeroporti = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] campi = line.split(",");
                if(campi.length < 5) continue;
                String codice = campi[0];
                String nome = campi[1];
                double lat = Double.parseDouble(campi[2]);
                double lon = Double.parseDouble(campi[3]);
                int piste = Integer.parseInt(campi[4]);
                aeroporti.add(new Aeroporto(codice, nome, lat, lon, piste));
            }
        }
        return aeroporti;
    }
}