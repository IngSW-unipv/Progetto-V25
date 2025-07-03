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
     * Restituisce la lista delle prenotazioni del volo.
     * 
     * @return Lista delle prenotazioni
     */
    public List<Prenotazione> getPrenotazioni() {    
        return new ArrayList<>(prenotazioni); 
    }

    /**
     * Restituisce la velocità del volo.
     * 
     * @return Velocità in km/h
     */
    public double getVelocita() {
        return velocita;
    }
    
    public StatoVolo getStato() { 
        return stato; 
    }
    
    public int getPistaAssegnata() { 
        return pistaAssegnata; 
    }

    /**
     * Aggiunge una prenotazione al volo.
     * Controlla che la prenotazione non sia null prima di aggiungerla.
     * 
     * @param prenotazione Prenotazione da aggiungere
     */
    public void aggiungiPrenotazione(Prenotazione p) {
        // Verifica che la prenotazione non sia null
        if (p != null && !prenotazioni.contains(p)) {
            prenotazioni.add(p);	
        }
    }
	   
    /**
     * Cancella una prenotazione basandosi sul documento del passeggero.
     * Scorre tutte le prenotazioni e marca come cancellata quella corrispondente.
     * 
     * @param documento Documento del passeggero da cancellare
     */
    public void cancellaPrenotazione(String documento) {
        // Scorre tutte le prenotazioni del volo
        for (Prenotazione pr : prenotazioni) {
            // Controlla se il documento corrisponde
            if (pr.getPasseggero().getDocumento().equals(documento)) {
                // Marca la prenotazione come cancellata
                pr.cancella();
                break; // Esce dal loop una volta trovata
            }
        }
    }

    /**
     * Calcola la distanza tra aeroporto di partenza e destinazione.
     * Utilizza il metodo calcolaDistanza della classe Aeroporto.
     * 
     * @return Distanza in chilometri
     */
    public double getDistanzaKm() {
        return CalcolaDistanza.calcolaDistanza(partenza, destinazione);
    }
    
    /**
     * Calcola il tempo totale di volo includendo eventuali ritardi.
     * Formula: (distanza / velocità) + ritardo
     * 
     * @return Tempo di volo in ore
     */
    public double calcolaTempo() {
        return (getDistanzaKm() / velocita) + ritardo;
    }
	
    /**
     * Calcola il ritardo basandosi sui voli che occupano le piste.
     * 
     * @return Ritardo calcolato in ore
     */
    public double calcolaRitardo() {
        double orarioRichiesto = this.orarioPartenza;
        double minRitardo = 0;
        // Cerca il primo orario disponibile su ogni pista
        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            Volo occupante = partenza.getPiste()[i];
            double fineOccupazione = (occupante == null) ? orarioRichiesto : occupante.getOrarioPartenza() + 0.5;
            double ritardo = Math.max(0, fineOccupazione - orarioRichiesto);

            // Prendi il minimo ritardo tra tutte le piste
            if (i == 0 || ritardo < minRitardo) {
                minRitardo = ritardo;
            }
        }
        return minRitardo;
    }
    
    /**
     * Restituisce una rappresentazione testuale del volo.
     * Include codice, rotta, orario e stato delle prenotazioni.
     * 
     * @return Stringa descrittiva del volo
     */
    @Override
    public String toString() {
         return String.format("Volo %s: %s -> %s, Partenza: %.2f, Passeggeri: %d",
                 codice, partenza.getCodice(), destinazione.getCodice(), orarioPartenza, prenotazioni.size());
     }
    
    /**
     * Verifica se il volo ha ritardi.
     * 
     * @return true se il volo è in ritardo, false altrimenti
     */
    public boolean isInRitardo() {
        return ritardo > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (!(o instanceof Volo v)) 
            return false;
        return codice.equals(v.codice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codice);
    }
}