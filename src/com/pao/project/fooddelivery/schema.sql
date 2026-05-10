DROP TABLE IF EXISTS reviewuri;
DROP TABLE IF EXISTS iteme_extra;
DROP TABLE IF EXISTS iteme_fara;
DROP TABLE IF EXISTS iteme_comanda;
DROP TABLE IF EXISTS comenzi;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS clienti_adrese;
DROP TABLE IF EXISTS carduri;
DROP TABLE IF EXISTS extra;
DROP TABLE IF EXISTS ingrediente;
DROP TABLE IF EXISTS produse;
DROP TABLE IF EXISTS restaurante;
DROP TABLE IF EXISTS soferi;
DROP TABLE IF EXISTS clienti;
DROP TABLE IF EXISTS adrese;

CREATE TABLE adrese (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	     strada TEXT NOT NULL,
		     numar INTEGER NOT NULL,
		     oras TEXT NOT NULL,
		     cod_postal TEXT NOT NULL);

CREATE TABLE clienti (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	      nume TEXT NOT NULL,
		      email TEXT NOT NULL UNIQUE,
		      parola TEXT NOT NULL,
		      este_premium INTEGER NOT NULL);

CREATE TABLE soferi (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	     nume TEXT NOT NULL,
		     email TEXT NOT NULL UNIQUE,
		     parola TEXT NOT NULL,
		     balanta REAL NOT NULL,
		     disponibil INTEGER NOT NULL);

CREATE TABLE restaurante (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		  nume TEXT NOT NULL,
			  adresa_id INTEGER NOT NULL,
			  taxa_livrare REAL NOT NULL,
			  procent_comision_sofer REAL NOT NULL,
			  livrare_gratuita INTEGER NOT NULL,
			  FOREIGN KEY (adresa_id) REFERENCES adrese(id));

CREATE TABLE produse (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	      restaurant_id INTEGER NOT NULL,
		      nume TEXT NOT NULL,
		      categorie TEXT NOT NULL,
		      pret REAL NOT NULL,
		      calorii INTEGER NOT NULL,
		      proteine REAL NOT NULL,
		      carbohidrati REAL NOT NULL,
		      grasimi REAL NOT NULL,
		      FOREIGN KEY (restaurant_id) REFERENCES restaurante(id));

CREATE TABLE ingrediente (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		  produs_id INTEGER NOT NULL,
			  nume TEXT NOT NULL,
			  alergen INTEGER NOT NULL,
			  optional INTEGER NOT NULL,
			  FOREIGN KEY (produs_id) REFERENCES produse(id));

CREATE TABLE extra (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	    produs_id INTEGER NOT NULL,
		    nume TEXT NOT NULL,
		    alergen INTEGER NOT NULL,
		    pret REAL NOT NULL,
		    FOREIGN KEY (produs_id) REFERENCES produse(id));

CREATE TABLE carduri (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	      client_id INTEGER NOT NULL,
		      numar TEXT NOT NULL UNIQUE,
		      balanta REAL NOT NULL,
		      FOREIGN KEY (client_id) REFERENCES clienti(id));

CREATE TABLE clienti_adrese (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		     client_id INTEGER NOT NULL,
			     adresa_id INTEGER NOT NULL,
			     UNIQUE (client_id, adresa_id),
			     FOREIGN KEY (client_id) REFERENCES clienti(id),
			     FOREIGN KEY (adresa_id) REFERENCES adrese(id));

CREATE TABLE favorite (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	       client_id INTEGER NOT NULL,
		       restaurant_id INTEGER NOT NULL,
		       UNIQUE (client_id, restaurant_id),
		       FOREIGN KEY (client_id) REFERENCES clienti(id),
		       FOREIGN KEY (restaurant_id) REFERENCES restaurante(id));

CREATE TABLE comenzi (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	      client_id INTEGER NOT NULL,
		      restaurant_id INTEGER NOT NULL,
		      adresa_id INTEGER NOT NULL,
		      sofer_id INTEGER,
		      status TEXT NOT NULL,
		      metoda_plata TEXT NOT NULL,
		      total_plata REAL NOT NULL,
		      FOREIGN KEY (client_id) REFERENCES clienti(id),
		      FOREIGN KEY (restaurant_id) REFERENCES restaurante(id),
		      FOREIGN KEY (adresa_id) REFERENCES adrese(id),
		      FOREIGN KEY (sofer_id) REFERENCES soferi(id));

CREATE TABLE iteme_comanda (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		    comanda_id INTEGER NOT NULL,
			    produs_id INTEGER NOT NULL,
			    cantitate INTEGER NOT NULL,
			    pret_unitar_baza REAL NOT NULL,
			    FOREIGN KEY (comanda_id) REFERENCES comenzi(id),
			    FOREIGN KEY (produs_id) REFERENCES produse(id));

CREATE TABLE iteme_fara (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		 item_comanda_id INTEGER NOT NULL,
			 ingredient_id INTEGER NOT NULL,
			 UNIQUE (item_comanda_id, ingredient_id),
			 FOREIGN KEY (item_comanda_id) REFERENCES iteme_comanda(id),
			 FOREIGN KEY (ingredient_id) REFERENCES ingrediente(id));

CREATE TABLE iteme_extra (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     		  item_comanda_id INTEGER NOT NULL,
			  extra_id INTEGER NOT NULL,
			  cantitate INTEGER NOT NULL,
			  UNIQUE (item_comanda_id, extra_id),
			  FOREIGN KEY (item_comanda_id) REFERENCES iteme_comanda(id),
			  FOREIGN KEY (extra_id) REFERENCES extra(id));

CREATE TABLE reviewuri (id INTEGER PRIMARY KEY AUTOINCREMENT,
       	     	        comanda_id INTEGER NOT NULL UNIQUE,
			sofer_id INTEGER NOT NULL,
			rating INTEGER NOT NULL,
			comentariu TEXT,
			FOREIGN KEY (comanda_id) REFERENCES comenzi(id),
			FOREIGN KEY (sofer_id) REFERENCES soferi(id));
