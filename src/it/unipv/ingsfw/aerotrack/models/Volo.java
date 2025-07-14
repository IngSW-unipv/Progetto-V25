package it.unipv.ingsfw.aerotrack.models;

import it.unipv.ingsfw.aerotrack.utils.CalcolaDistanza;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Volo{
    
    /**
     * Enum per rappresentare i possibili stati di un volo
     */
    public enum StatoVolo {
        PROGRAMMATO,    // Volo programmato ma non ancora in partenza
        IN_PARTENZA,    // Volo in fase di partenza
        IN_VOLO,        // Volo in corso
        ATTERRATO,      // Volo arrivato a destinazione
        CANCELLATO,     // Volo cancellato
        IN_RITARDO,      // Volo in ritardo
        IN_ATTESA,		// VOlo in attesa di partire dopo ritardo
    }
    
    // Attributi 
    private final String codice;
    private final Aeroporto partenza;
    private final Aeroporto destinazione;
    private final LocalTime orarioPartenza;
    private final List<Prenotazione> prenotazioni; 
    private final double velocita;
    private LocalTime ritardo; 
    private StatoVolo stato;
    private int pistaAssegnata;
    private final LocalDate dataVolo;

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
    public Volo(String codice, Aeroporto partenza, Aeroporto destinazione, LocalTime orarioPartenza, double velocita, LocalDate data) {
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
        this.ritardo = LocalTime.of(0, 0);
        this.dataVolo = data;
        this.stato = StatoVolo.PROGRAMMATO;
        this.prenotazioni = new ArrayList<>();
        this.pistaAssegnata = -1;
        
        // Cerca tra le piste dell'aeroporto di partenza la prima pista libera nell'orario di partenza
        this.pistaAssegnata = assegnaPista(partenza, true);
        if (pistaAssegnata == -1) {
            this.ritardo = calcolaRitardo();
            this.stato = StatoVolo.IN_RITARDO;
        }

        // Cerca tra le piste dell'aeroporto di destinazione la prima pista libera nell'orario di arrivo
        assegnaPista(destinazione, false);
  
     // Aggiorna le liste degli aeroporti
        partenza.aggiungiVoloInPartenza(this);
        destinazione.aggiungiVoloInArrivo(this);  
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
    public LocalTime getOrarioPartenza() {
        return orarioPartenza;
    }
    
    /**
     * Restituisce la data di partenza programmato.
     * 
     * @return Data di partenza
     */
    public LocalDate getDataVolo() {
        return dataVolo;
    }

    /**
     * Restituisce la velocità del volo.
     * 
     * @return Velocità in km/h
     */
    public double getVelocita() {
        return velocita;
    }

    /**
     * Restituisce il ritardo accumulato dal volo.
     * 
     * @return Ritardo 
     */
    public LocalTime getRitardo() {
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
     * Restituisce lo stato del volo del volo.
     * 
     * @return stato volo tra quelli definiti nella enum
     */
    public StatoVolo getStato() {
        return stato;
    }
    
    /**
     * Restituisce la pista di decollo dell'aeroporto di partenza.
     * 
     * @return indice della pista di partenza per il decollo
     */
    public int getPistaAssegnata() { 
        return pistaAssegnata; 
    }

    // === SETTER ===
    public void setStato(StatoVolo stato) {
        this.stato = stato;
    }

    public void setRitardo(LocalTime ritardo) {
        this.ritardo = ritardo;
    }
    
    public void setPistaAssegnata(int pista) {
        this.pistaAssegnata = pista;
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
     * Calcola il tempo totale di volo.
     * @return Tempo di volo in minuti
     */
    public long calcolaTempo() {
        double ore = getDistanzaKm() / velocita; // tempo in ore
        long minuti = (long)(ore * 60);          // converto ore in minuti (interi)
        return minuti;
    }
    
    
    /**
     * Assegna la pista di partenza o atterraggio a seconda del valore isPartenza
     * @return numero pista assegnata in partenza e null in destinazione
     */
    private int assegnaPista(Aeroporto aeroporto, boolean isPartenza) {
        // Orario di riferimento: partenza o arrivo a seconda del contesto
        LocalTime mioOrario = isPartenza ? orarioPartenza
                                         : orarioPartenza.plusMinutes(30 + calcolaTempo());

        for (int i = 0; i < aeroporto.getNumeroPiste(); i++) {
            boolean libera = true;

            for (Volo v : aeroporto.getPiste()[i]) {
            	if(v.getDataVolo().isEqual(dataVolo)) {
            		if(v.getDataVolo().isEqual(dataVolo)) {
             		   // Conflitto con altri voli in partenza sulla stessa pista
            			if (v.getPartenza().equals(aeroporto)) {
            				if (!(mioOrario.plusMinutes(30).isBefore(v.getOrarioPartenza()) ||
            						mioOrario.isAfter(v.getOrarioPartenza().plusMinutes(30)))) {
            					libera = false;
            					break;
            				}
                	   }

            			// Conflitto con voli in arrivo sulla stessa pista
            			if (v.getDestinazione().equals(aeroporto)) {
            				if (!(mioOrario.plusMinutes(30).isBefore(v.getOrarioPartenza().plusMinutes(30 + v.calcolaTempo())) ||
            						mioOrario.isAfter(v.getOrarioPartenza().plusMinutes(30 + v.calcolaTempo())))) {
            					libera = false;
            					break;
            				}
            			}
            		}  
            	}
            }

            // Se la pista è libera, assegna
            if (libera) {
                aeroporto.getPiste()[i].add(this);
                return i;
            }
        }

        return -1; // Nessuna pista disponibile
    }


    /**
     * Calcola il ritardo solo se la pista non è assegnata.
     * 
     * @return Ritardo calcolato in ore
     */
    public LocalTime calcolaRitardo() {
        // Prova prima ad assegnare direttamente il volo se c'è una pista libera
        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            boolean sovrapposto = false;

            for (Volo v : partenza.getPiste()[i]) {

                // Test sovrapposizione
                if (!(orarioPartenza.plusMinutes(30).isBefore(v.getOrarioPartenza()) || orarioPartenza.isAfter(v.getOrarioPartenza().plusMinutes(30)))) {
                    sovrapposto = true;
                    break;
                }
            }

            if (!sovrapposto) {
                // Pista libera, assegna
            	partenza.getPiste()[i].add(this);
                this.pistaAssegnata = i;
                return LocalTime.of(0, 0); // Nessun ritardo
            }
        }

        // Se tutte le piste sono occupate, cerca il primo orario disponibile dopo i 30 minuti di ciascuna pista
        LocalTime primoDisponibile = LocalTime.MAX;
        int pistaScelta = -1;

        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            for (Volo v : partenza.getPiste()[i]) {
            	if(v.getDataVolo().isEqual(dataVolo)) {
            		 if (v.getOrarioPartenza().plusMinutes(30).isAfter(orarioPartenza) && v.getOrarioPartenza().plusMinutes(30).isBefore(primoDisponibile)) {
                         primoDisponibile = v.getOrarioPartenza().plusMinutes(30);
                         pistaScelta = i;
                     }
            	}
            }
        }

        // Calcola ritardo
        Duration ritardo = Duration.between(orarioPartenza, primoDisponibile);
        partenza.getPiste()[pistaScelta].add(this);
        this.pistaAssegnata = pistaScelta;

        return LocalTime.of((int) ritardo.toHours(), ritardo.toMinutesPart());
    }

    
    /**
     * Calcola lo stato rispetto a un orario fittizio.
     */
    public StatoVolo calcolaStato(LocalTime orario) {
    	stato = StatoVolo.PROGRAMMATO;
        if((ritardo.isAfter(LocalTime.of(2, 0)))) return StatoVolo.CANCELLATO;
    	if ( orario.isAfter(orarioPartenza)
            && orario.isBefore(orarioPartenza.plusMinutes(ritardo.toSecondOfDay() / 60))) return StatoVolo.IN_ATTESA;
        if ((orario.equals(orarioPartenza.plusMinutes(ritardo.toSecondOfDay() / 60)) || orario.isAfter(orarioPartenza.plusMinutes(ritardo.toSecondOfDay() / 60))) 
        	&& orario.isBefore(orarioPartenza.plusMinutes(30 + ritardo.toSecondOfDay() / 60))) return StatoVolo.IN_PARTENZA;
        if ((orario.equals(orarioPartenza.plusMinutes(30 + ritardo.toSecondOfDay() / 60))) || orario.isAfter(orarioPartenza.plusMinutes(30 + ritardo.toSecondOfDay() / 60))
            && orario.isBefore(orarioPartenza.plusMinutes(30 + calcolaTempo() + ritardo.toSecondOfDay() / 60))) return StatoVolo.IN_VOLO;
        if (orario.isAfter(orarioPartenza.plusMinutes(30 + calcolaTempo() + ritardo.toSecondOfDay() / 60))) return StatoVolo.ATTERRATO;

        return stato;
    }
    
    /**
    * Restituisce una rappresentazione testuale del volo.
    * Include codice, rotta, orario e stato delle prenotazioni.
    * 
    * @return Stringa descrittiva del volo
    */
   @Override
   public String toString() {
        return String.format("Volo %s: %s -> %s, Partenza: %.2s, Passeggeri: %d",
                codice, partenza.getCodice(), destinazione.getCodice(), orarioPartenza, prenotazioni.size());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volo)) return false;
        Volo volo = (Volo) o;
        return codice.equals(volo.codice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codice);
    }
}