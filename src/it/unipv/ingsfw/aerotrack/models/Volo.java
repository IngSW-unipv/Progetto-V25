package it.unipv.ingsfw.aerotrack.models;

import it.unipv.ingsfw.aerotrack.utils.CalcolaDistanza;

import java.time.LocalDate;
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
    private int pistaAssegnata;
    private boolean pistaLiberata;
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
    public Volo(String codice, Aeroporto partenza, Aeroporto destinazione, double orarioPartenza, double velocita, LocalDate dataVolo) {
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
        this.dataVolo = dataVolo;
        this.prenotazioni = new ArrayList<>();
        this.ritardo = 0;     
        this.pistaAssegnata = -1;
        this.stato = StatoVolo.PROGRAMMATO;
        this.pistaLiberata = false;
        
        // Aggiorna le liste degli aeroporti
        partenza.aggiungiVoloInPartenza(this);
        destinazione.aggiungiVoloInArrivo(this);  
    }
	
    /**
     * Costruttore secondario
     */
    public Volo(String codice, Aeroporto partenza, Aeroporto destinazione, double orarioPartenza, double velocita,
                int pistaAssegnata, double ritardo, StatoVolo stato, LocalDate dataVolo) {
        if (codice == null || codice.isEmpty())
            throw new IllegalArgumentException("Codice volo null");
        if (partenza == null || destinazione == null)
            throw new IllegalArgumentException("Aeroporto partenza/destinazione null");
        if (velocita <= 0)
            throw new IllegalArgumentException("Velocità non valida");

        this.codice = codice;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.orarioPartenza = orarioPartenza;
        this.velocita = velocita;
        this.prenotazioni = new ArrayList<>();
        this.pistaAssegnata = pistaAssegnata;
        this.ritardo = ritardo;
        this.stato = stato != null ? stato : StatoVolo.PROGRAMMATO;
        this.dataVolo = dataVolo;
        this.pistaLiberata = false;
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
    
    // Getter per la data
    public LocalDate getDataVolo() {
        return dataVolo;
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
     * Restituisce true se il volo è stato cancellato.
     */
    public boolean isCancellato() {
        return stato == StatoVolo.CANCELLATO;
    }
    
    // === SETTER ===
    public void setStato(StatoVolo stato) {
        this.stato = stato;
    }

    public void setPistaAssegnata(int pistaAssegnata) {
        this.pistaAssegnata = pistaAssegnata;
    }

    public void setRitardo(double ritardo) {
        this.ritardo = ritardo;
    }

    /**
     * Imposta lo stato del volo a CANCELLATO.
     */
    public void setCancellato() {
        this.stato = StatoVolo.CANCELLATO;
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
     * @return Tempo di volo in ore
     */
    public double calcolaTempo() {
        return (getDistanzaKm() / velocita);
    }
	
    /**
     * Calcola il ritardo solo se la pista non è assegnata.
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
     * Trova il primo orario in cui almeno una pista dell'aeroporto di partenza è libera.
     * Se nessuna pista è occupata, restituisce l'orario richiesto.
     */
    public double trovaPrimoOrarioLiberoSuQualsiasiPista() {
    	double orarioRichiesto = this.orarioPartenza;
        double minOrarioLibero = Double.MAX_VALUE;

        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            Volo occupante = partenza.getPiste()[i];
            if (occupante == null) {
                // Pista libera da subito
                return orarioRichiesto;
            } else {
                // Calcola quando la pista si libera (fine occupazione = partenza + 0.5 ore)
                double fineOccupazione = occupante.getOrarioPartenza() + 0.5;
                if (fineOccupazione < minOrarioLibero) {
                    minOrarioLibero = fineOccupazione;
                }
            }
        }
        // Se nessuna pista libera subito, restituisci il primo orario libero tra tutte
        return minOrarioLibero;
    }
    
    /**
     * Trova l'indice della pista libera all'orario indicato.
     * Restituisce l'indice della pista oppure -1 se nessuna è libera.
     */
    public int trovaIndicePistaLibera(double orarioLibero) {
        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            Volo occupante = partenza.getPiste()[i];
            // Libera se non c'è volo oppure il volo che la occupa parte prima di orarioLibero
            if (occupante == null || (occupante.getOrarioPartenza() + 0.5 <= orarioLibero)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Aggiorna lo stato del volo e libera la pista in base all'orario reale.
     * Il ritardo viene calcolato solo se la pista non è assegnata e il  volo è pronto a partire.
     * @param orarioReale l'orario reale 
     */
    public void aggiornaStatoECalcolaRitardo(double orarioReale) {
    	LocalDate oggi = LocalDate.now();
    	
    	if (isCancellato()) return;

        // Se il volo è in futuro, stato = PROGRAMMATO e ritardo = 0
        if (dataVolo.isAfter(oggi)) {
            stato = StatoVolo.PROGRAMMATO;
            ritardo = 0;
            return;
        }

        // Se il volo è in passato, stato = ATTERRATO
        if (dataVolo.isBefore(oggi)) {
            stato = StatoVolo.ATTERRATO;
            ritardo = 0;
            return;
        }

        // Se la data è oggi, calcola in base all'orario
        // Ritardo solo se la pista non è assegnata e siamo all'orario di partenza
        if (stato == StatoVolo.PROGRAMMATO && orarioReale >= orarioPartenza) {
            if (pistaAssegnata < 0) {
                // Calcola ritardo come tempo di attesa per pista libera
                double primoOrarioLibero = trovaPrimoOrarioLiberoSuQualsiasiPista();
                ritardo = primoOrarioLibero - orarioPartenza;
                if (ritardo < 0) ritardo = 0;
                // Assegna la pista ora libera
                int pistaLibera = trovaIndicePistaLibera(primoOrarioLibero);
                if (pistaLibera >= 0) {
                	Volo occupante = partenza.getPiste()[pistaLibera];
                    if (occupante == null || (occupante.getOrarioPartenza() + 0.5 <= primoOrarioLibero)) {
                        partenza.liberaPista(pistaLibera); // Libera la pista se il volo precedente dovrebbe aver decollato
                        partenza.occupaPista(pistaLibera, this);
                        pistaAssegnata = pistaLibera;
                    }
                }
            } else {
                ritardo = 0;
            }
        }
        double partenzaEffettiva = orarioPartenza + ritardo;

        // Stato sequenziale
        if (pistaAssegnata < 0 && ritardo > 0 && orarioReale >= orarioPartenza) {
            stato = StatoVolo.RITARDATO;
        } else if (orarioReale < partenzaEffettiva) {
            stato = StatoVolo.PROGRAMMATO;
        } else if (orarioReale >= partenzaEffettiva && orarioReale < partenzaEffettiva + 0.1) {
            stato = StatoVolo.IN_PARTENZA;
        } else if (orarioReale >= partenzaEffettiva + 0.1 && orarioReale < partenzaEffettiva + 0.1 + calcolaTempo()) {
            stato = StatoVolo.IN_VOLO;
            // Libera la pista una volta decollato
            if (pistaAssegnata >= 0 && !pistaLiberata) {
                partenza.liberaPista(pistaAssegnata);
                pistaLiberata = true;
            }
        } else if (orarioReale >= partenzaEffettiva + 0.1 + calcolaTempo()) {
            stato = StatoVolo.ATTERRATO;
        }
    }


    /**
     * Calcola lo stato rispetto a un orario fittizio (per ricerca, NON aggiorna campo stato!).
     */
    public StatoVolo calcolaStato(double orario) {
        if (isCancellato()) return StatoVolo.CANCELLATO;
        double partenzaEffettiva = orarioPartenza + ritardo;
        if (orario < orarioPartenza) return StatoVolo.PROGRAMMATO;
        if (orario >= orarioPartenza && orario < partenzaEffettiva) 
        	return StatoVolo.RITARDATO;
        if (orario >= partenzaEffettiva && orario < partenzaEffettiva + 0.1) 
        	return StatoVolo.IN_PARTENZA;
        if (orario >= partenzaEffettiva + 0.1 && orario < partenzaEffettiva + 0.1 + calcolaTempo()) 
        	return StatoVolo.IN_VOLO;
        if (orario >= partenzaEffettiva + 0.1 + calcolaTempo()) 
        	return StatoVolo.ATTERRATO;
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