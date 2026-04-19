# Platforma Food Delivery - Proiect PAO 2026

**Tema:** Platforma food delivery (restaurante, meniuri, comenzi, soferi, clienti)

---

## 1.1 - Actiuni / interogari posibile in sistem

### Afisari
1. **Afiseaza restaurante** - listeaza toate restaurantele disponibile pe platforma
2. **Afiseaza meniul unui restaurant** - listeaza produsele unui restaurant, cu ingrediente si valori nutritionale
3. **Afiseaza istoricul comenzilor** - returneaza toate comenzile finalizate ale unui client
4. **Afiseaza comenzile disponibile pentru livrare** - listeaza comenzile nepreluate, vizibile pentru soferi
5. **Afiseaza datele contului** - afiseaza profilul clientului (adrese salvate, carduri, favorite)
6. **Afiseaza checkout-ul comenzii** - confirmarea finala cu total, TVA si metoda de plata aleasa

### Interactiuni client
7. **Adauga / sterge produse din cos** - clientul gestioneaza cosul curent inainte de checkout
8. **Adauga / sterge restaurant la favorite** - clientul salveaza sau elimina restaurante din lista de favorite
9. **Adauga / sterge adrese de livrare** - clientul gestioneaza adresele asociate contului sau
10. **Adauga / sterge carduri bancare** - clientul adauga carduri cu balanta, cu posibilitate de top-up
11. **Checkout comanda** - clientul finalizeaza cosul si plaseaza comanda, se alege metoda de plata
12. **Selectare metoda de plata** - clientul alege intre plata cu cardul (online, cu TVA) sau cash (fizic)
13. **Personalizare ingrediente** - clientul modifica ingredientele optionale ale unui produs, acolo unde restaurantul permite
14. **Adauga review** - dupa livrare, clientul poate lasa un review optional pentru sofer si comanda

### Interactiuni sofer
15. **Preia comanda** - soferul selecteaza o comanda din lista celor disponibile si o marcheaza ca in livrare
16. **Finalizeaza livrare** - soferul marcheaza comanda ca livrata, i se crediteaza comisionul in balanta

---

### Reguli de sistem
- Fiecare restaurant are o taxa de livrare platita de client si un procent de comision primit de sofer, ambele distincte
- Statusurile unei comenzi sunt: NEPRELUATA -> IN_LIVRARE -> FINALIZATA
- Clientii si soferii trebuie sa aiba cont inregistrat pentru a interactiona cu platforma
- La plata cu cardul se aplica TVA de 9%, la plata cash nu se aplica TVA
- Restaurantele nu au stoc - pot produce la infinit, nu exista capital de restaurant
- Un sofer poate livra pentru orice restaurant
- Cardurile au balanta, clientii pot face top-up oricand
- Soferii au o singura balanta, nu se diferentiaza cash de card pentru ei
- Clientii premium beneficiaza de livrare gratuita la restaurantele care permit acest lucru, atat timp cat obiectul ClientPremium exista in sistem
- Cand abonamentul unui client premium expira, obiectul ClientPremium este inlocuit cu un Client normal in UtilizatorService

---

## 1.2 - Tipuri de obiecte din domeniu

| Clasa | Tip | Descriere |
|---|---|---|
| **Utilizator** | clasa abstracta | Baza comuna pentru **Client** si **Sofer**, contine nume, email, parola si metoda abstracta *getRol()* care returneaza un String |
| **Client** | clasa, extends **Utilizator** | Adauga adrese salvate, carduri si lista de favorite, cosul este gestionat extern de **CosService**, implementeaza *areLivrareGratuita()* returnand false |
| **ClientPremium** | clasa, extends **Client** | Extinde **Client**, exista in sistem doar cat timp abonamentul e activ, suprascrie *areLivrareGratuita()* returnand true daca restaurantul permite |
| **Sofer** | clasa, extends **Utilizator** | Adauga balanta proprie unde se acumuleaza comisioanele din livrari si status de disponibilitate |
| **Restaurant** | clasa | Entitatea inregistrata pe platforma, are un **Meniu** asociat, o taxa de livrare pentru client, un procent de comision pentru sofer si un flag pentru livrare gratuita la clientii premium |
| **Meniu** | clasa | Apartine unui **Restaurant**, contine o lista de **Produs**, expune metode de cautare si filtrare precum *cautaDupaNume()* si *filtreazaDupaCategorie()* |
| **Produs** | clasa | Un preparat din meniu, are nume, pret, categorie, o lista de **Ingredient** si un obiect **ValoriNutritionale** asociat |
| **Ingredient** | record | Reprezinta un ingredient al unui **Produs**, are nume, un flag alergen si un flag optional care permite personalizarea |
| **ValoriNutritionale** | record | Bloc de date read-only asociat unui **Produs**, contine calorii, proteine, carbohidrati si grasimi, toate generate automat |
| **Comanda** | clasa | Comanda plasata de un client, contine restaurant, produse, metoda de plata si status, identificata printr-un id generat printr-un contor static, tranzitiile de status sunt gestionate prin FSM-ul din **StatusComanda** |
| **Cos** | clasa | Cosul temporar al clientului inainte de checkout, tine un Map de **Produs** la cantitate, expune *calculeazaSubtotal()* |
| **IPlata** | interfata | Contractualizeaza o plata, expune metodele *calculeazaTotal()* si *getMetodaPlata()*, implementata de **PlataCard** si **PlataCash** |
| **PlataCard** | clasa, implements **IPlata** | Implementeaza *calculeazaTotal()* aplicand TVA de 9% peste subtotal |
| **PlataCash** | clasa, implements **IPlata** | Implementeaza *calculeazaTotal()* returnand subtotalul fara TVA |
| **CardBancar** | clasa | Cardul unui client, numarul cardului este final, are balanta si suporta operatii de top-up si debitare |
| **Adresa** | record | Toate campurile sunt final si generate automat, folosita atat pentru adresele clientului cat si pentru adresa restaurantului |
| **Review** | clasa | Recenzie simpla cu rating intre 1 si 5 si comentariu optional, asocierea cu comanda si soferul este gestionata de **ReviewService** |
| **StatusComanda** | enum + FSM | Valorile NEPRELUATA, IN_LIVRARE, FINALIZATA, fiecare valoare defineste tranzitia valida prin metoda abstracta *avanseaza()*, arunca **TranzitieInvalidaException** la tranzitie invalida |
| **MetodaPlata** | enum | Valorile CARD si CASH, folosit la selectarea metodei de plata in checkout |
| **CosService** | singleton | Gestioneaza cosul fiecarui client, tine intern un Map de **Client** la **Cos**, expune *adaugaProdus()*, *stergeProdus()*, *goleste()* |
| **ComandaService** | singleton | Gestioneaza crearea si actualizarea comenzilor, tine istoricul indexat dupa client, calculeaza totalul tinand cont de taxa de livrare si tipul clientului |
| **RestaurantService** | singleton | Gestioneaza lista de restaurante si browsing-ul meniului, expune *getToate()*, *cautaDupaNume()* |
| **UtilizatorService** | singleton | Gestioneaza conturile de client si sofer, se ocupa de promovarea unui **Client** la **ClientPremium** si de expirarea abonamentului |
| **ReviewService** | singleton | Gestioneaza asocierea dintre review, comanda si sofer, tine un Map de **Comanda** la **Review** si un Map de **Sofer** la lista de **Review**, expune *adaugaReview()* si *getRatingMediuSofer()* |