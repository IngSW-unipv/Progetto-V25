package it.unipv.ingsfw.aerotrack.models;

import it.unipv.ingsfw.aerotrack.utils.CalcolaDistanza;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un volo nel sistema di gestione aeroportuale.
 * Un volo è caratterizzato da codice identificativo, aeroporti di partenza e destinazione,
 * orari, velocità e lista delle prenotazioni dei passeggeri.
 */
public class Volo { 
	
    /**
     * Enum per rappresentare i possibili stati di un volo
     */
    public enum StatoVolo {
        PROGRAMMATO,    // Volo programmato ma non ancora in partenza
        IN_PARTENZA,    // Volo in fase di partenza
        IN_VOLO,        // Volo in corso
        ATTERRATO,      // Volo arrivato a destinazione
        CANCELLATO,     // Volo cancellato
        RITARDATO,      // Volo in ritardo
        IN_ATTESA       // Tutte le piste sono occupate
    }
    
    // Attributi  
    private final String codice;
    private final Aeroporto partenza;
    private final Aeroporto destinazione;
    private final double velocita;
    private final double orarioPartenza;
    private final List<Prenotazione> prenotazioni;
    private double ritardo;
    private StatoVolo stato;
    private int pistaAssegnata = -1;
	
	
    /**
     * Costruttore principale per creare un nuovo volo.
     * Inizializza tutti gli attributi e gestisce l'assegnazione della pista.
     * 
     * @param codice Codice identificativo del volo
     * @param partenza Aeroporto di partenza
     * @param destinazione Aeroporto di destinazione
     * @param orarioPartenza Orario programmato di partenza
     * @param velocita Velocità del volo
     */
    public Volo(String codice, Aeroporto partenza, Aeroporto destinazione, double orarioPartenza, double velocita) {
        if (codice == null || codice.isEmpty())
            throw new IllegalArgumentException("Codice volo null");
        if (partenza == null || destinazione == null)
            throw new IllegalArgumentException("Aeroporto partenza/destinazione null");
        if (velocita <= 0)
            throw new IllegalArgumentException("Velocità non valida");
        
        // Inizializzazione degli attributi
        this.codice = codice;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.orarioPartenza = orarioPartenza;
        this.velocita = velocita;
        this.prenotazioni = new ArrayList<>();
        this.ritardo = 0;                        
        this.pistaAssegnata = -1;
        
        // Aggiorna le liste degli aeroporti
        partenza.aggiungiVoloInPartenza(this);
        destinazione.aggiungiVoloInArrivo(this);
  
        // Assegna una pista libera, altrimenti stato IN_ATTESA e ritardo
        boolean pistaTrovata = false;
        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            if (partenza.getPiste()[i] == null) {
                partenza.occupaPista(i, this);
                this.pistaAssegnata = i;
                this.stato = StatoVolo.PROGRAMMATO;
                pistaTrovata = true;
                break;
            }    
        }
        if (!pistaTrovata) {
            this.ritardo = calcolaRitardo();
            this.stato = StatoVolo.IN_ATTESA;
        }
    }
	
    /**
     * Restituisce il codice del volo.
     * 
     * @return Codice identificativo del volo
     */
    public String getCodice() {       
        return codice;
    }
	
    /**
     * Restituisce l'aeroporto di partenza.
     * 
     * @return Aeroporto di partenza
     */
    public Aeroporto getPartenza() {
        return partenza;
    }
	
    /**
     * Restituisce l'aeroporto di destinazione.
     * 
     * @return Aeroporto di destinazione
     */
    public Aeroporto getDestinazione() {
        return destinazione;
    }
	
     /**
     * Restituisce l'orario di partenza programmato.
     * 
     * @return Orario di partenza
     */
    public double getOrarioPartenza() {
        return orarioPartenza;
    }

    /**
     * Restituisce il ritardo accumulato dal volo.
     * 
     * @return Ritardo in ore
     */
    public double getRitardo() {
        return ritardo;
    }
	
    /**
    
