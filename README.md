# ✈️ Aerotrack – Sistema Universitario di Gestione Aeroportuale

<img alt="Backend: Java" src="https://img.shields.io/badge/Backend-Java-007396?style=for-the-badge&logo=java&labelColor=242424">
<img alt="GUI: Swing" src="https://img.shields.io/badge/GUI-Swing-0078D7?style=for-the-badge&labelColor=242424">
<img alt="Database: MySQL" src="https://img.shields.io/badge/Database-MySQL-4479A1?style=for-the-badge&logo=mysql&labelColor=242424">
<img alt="Test: JUnit 5" src="https://img.shields.io/badge/Test-JUnit%205-25A162?style=for-the-badge&logo=JUnit5&labelColor=242424">

---

## Descrizione

**Aerotrack** è un sistema didattico sviluppato in Java per la gestione di aeroporti, voli, passeggeri e prenotazioni.  
Il software include una GUI intuitiva realizzata con Java Swing, interazione con database MySQL, utility per import/export dati e test JUnit per garantire affidabilità e robustezza.

---

## Funzionalità Principali

- **Gestione Aeroporti**: creazione, rimozione, ricerca, importazione/esportazione CSV.
- **Gestione Voli**: creazione, rimozione, ricerca (per codice, partenza, destinazione), assegnazione piste.
- **Gestione Prenotazioni**: creazione, cancellazione, ricerca per passeggero o volo.
- **Gestione Passeggeri**: inserimento automatico tramite prenotazione, ricerca.
- **Calcolo Distanze**: utility per calcolare la distanza tra aeroporti tramite formula di Haversine.
- **Interfaccia Multi-tab**: navigazione semplice tra le sezioni principali.
- **Test JUnit**: copertura delle classi modello e dei servizi fondamentali.

---

## Dipendenze

- [Java SE 17+](https://www.oracle.com/java/technologies/downloads/)
- [MySQL Community Edition](https://dev.mysql.com/downloads/mysql/)
- [MySQL Connector/J](https://github.com/mysql/mysql-connector-j)
- [JUnit 5](https://junit.org/junit5/)

---

## Installazione e Setup

### 1. Clona il repository

```bash
git clone https://github.com/IngSW-unipv/Aerotrack.git
```

### 2. Importa il progetto nell’IDE

- Apri il tuo IDE preferito.
- Importa come progetto Java standard.

### 3. Configura il Database

- Installa [MySQL](https://dev.mysql.com/downloads/mysql/) e [MySQL Workbench](https://dev.mysql.com/downloads/workbench/).
- Crea uno schema chiamato `aerotrack`.
- Importa la struttura delle tabelle tramite il file `AEROTRACK.sql` presente nella root del progetto.
- (Facoltativo) Importa dati di esempio tramite `TABELLE.sql`.

### 4. Configura la connessione al database

- Modifica il file `/properties/dbconfig.properties` con i parametri della tua istanza:
  ```
  db.url=jdbc:mysql://localhost:3306
  db.username=<tuo_utente>
  db.password=<tua_password>
  db.driver=com.mysql.cj.jdbc.Driver
  ```

---

## Avvio dell’Applicazione

- Avvia la classe:
  ```
  it.unipv.ingsfw.aerotrack.test.MainApp
  ```
  oppure esegui `MainApp` dal tuo IDE.

- Naviga tra i tab:
    - **Benvenuto**: schermata introduttiva
    - **Aeroporti**: gestione CRUD e CSV aeroporti
    - **Voli**: gestione voli
    - **Prenotazioni**: aggiunta/cancellazione e ricerca prenotazioni

---

## Struttura del Progetto

- **/models** - Entità principali (Aeroporto, Volo, Prenotazione, Passeggero)
- **/dao** - Data Access Object per la persistenza su MySQL
- **/services** - Logica di business e validazione
- **/view** - Interfaccia grafica Swing (pannelli e frame)
- **/utils** - Utility di supporto (calcolo distanze, CSV)
- **/test** - Test manuali e automatici (JUnit)
- **/properties** - Configurazione database

---

## Esempio di Utilizzo

1. Aggiungi aeroporti tramite il tab **Aeroporti**.
2. Crea un volo tra due aeroporti dal tab **Voli**.
3. Prenota un passeggero su un volo dal tab **Prenotazioni**.
4. Esporta/importa aeroporti in formato CSV.

---

**Buon viaggio con Aerotrack!**