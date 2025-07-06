DROP TABLE IF EXISTS prenotazioni;
DROP TABLE IF EXISTS voli;
DROP TABLE IF EXISTS passeggeri;
DROP TABLE IF EXISTS aeroporti;

-- Creazione della tabella aereoporti
CREATE TABLE IF NOT EXISTS aeroporti (
    codice VARCHAR(100) PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    latitudine DOUBLE NOT NULL,
    longitudine DOUBLE NOT NULL,
    numeroPiste INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Creazione della tabella passeggeri
CREATE TABLE IF NOT EXISTS passeggeri (
    documento VARCHAR(100) PRIMARY KEY,
    nome VARCHAR(50) DEFAULT NULL,
    cognome VARCHAR(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Creazione della tabella voli
CREATE TABLE IF NOT EXISTS voli (
    codice VARCHAR(100) PRIMARY KEY,
    partenza VARCHAR(100) NOT NULL,
    destinazione VARCHAR(100) NOT NULL,
    orario_partenza DOUBLE NOT NULL,
    velocita DOUBLE NOT NULL,
    pista_assegnata INT DEFAULT -1,
    ritardo DOUBLE DEFAULT 0,
    stato VARCHAR(20) DEFAULT 'PROGRAMMATO',
    FOREIGN KEY(partenza) REFERENCES aeroporti(codice),
    FOREIGN KEY(destinazione) REFERENCES aeroporti(codice)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Creazione della tabella prenotazioni
CREATE TABLE IF NOT EXISTS prenotazioni (
    codice_prenotazione VARCHAR(100) PRIMARY KEY,
    documento_passeggero VARCHAR(100) NOT NULL,
    codice_volo VARCHAR(100) NOT NULL,
    cancellata BOOLEAN DEFAULT FALSE,
    FOREIGN KEY(codice_volo) REFERENCES voli(codice),
    FOREIGN KEY(documento_passeggero) REFERENCES passeggeri(documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;