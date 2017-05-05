CREATE TABLE IF NOT EXISTS horariosProvaveisTable (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  rota TEXT,
  tipo_dia TEXT,
  id_parada INTEGER,
  horario_medio TEXT,
  horario_anterior TEXT,
  horario_posterior TEXT
);

CREATE TABLE IF NOT EXISTS resposta (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  categoria INTEGER,
  valor INTEGER,
  timestamp INTEGER,
  FOREIGN KEY(timestamp) REFERENCES avaliacao(timestamp)
);

CREATE TABLE IF NOT EXISTS avaliacao (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  timestamp INTEGER,
  rota TEXT,
  preenchida INTEGER DEFAULT 0
);

CREATE INDEX avaliacao_rota_idx ON avaliacao(rota);


CREATE TABLE IF NOT EXISTS location (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  lat REAL,
  lon REAL,
  acc TEXT,
  speed TEXT,
  bear TEXT,
  et INTEGER,
  timestamp INTEGER,
  FOREIGN KEY(timestamp) REFERENCES avaliacao(timestamp)
);

CREATE TABLE IF NOT EXISTS route (
  id TEXT PRIMARY KEY,
  short_name TEXT,
  long_name TEXT,
  color TEXT,
  line_name TEXT,
  main_stops TEXT
);

CREATE TABLE IF NOT EXISTS stop (
  id INTEGER PRIMARY KEY,
  name TEXT,
  desc TEXT,
  lat REAL,
  lon REAL
);

CREATE TABLE IF NOT EXISTS route_stop (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  id_route TEXT,
  id_stop INTEGER,
  stop_order INTEGER,
  next_stop INTEGER,
  FOREIGN KEY(id_route) REFERENCES route(id),
  FOREIGN KEY(id_stop) REFERENCES stop(id),
  FOREIGN KEY(next_stop) REFERENCES stop(id) 
);

CREATE TABLE IF NOT EXISTS shapes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  id_route TEXT,
  order_num INTEGER,
  lat REAL,
  lon REAL,
  sub TEXT
);

CREATE TABLE IF NOT EXISTS non_published_ratings (
  id_rating INTEGER PRIMARY KEY,
  FOREIGN KEY(id_rating) REFERENCES avaliacao(timestamp)
);
