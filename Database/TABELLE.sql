-- Inserimento di alcuni dati nella tabella aereoporti
INSERT INTO aeroporti (codice, nome, latitudine, longitudine, numeroPiste) VALUES
  ('MXP', 'Milano Malpensa', 45.63, 8.72, 2),
  ('FCO', 'Roma Fiumicino', 41.80, 12.24, 4);

-- Inserimento di alcuni dati nella tabella passeggeri
INSERT INTO passeggeri (documento, nome, cognome) VALUES
  ('IT12345', 'Chiara', 'Viale'),
  ('IT54321', ‘Davide’, ‘Bozzola’);