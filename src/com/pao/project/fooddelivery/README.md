# Platforma Food Delivery - Proiect PAO 2026

**Tema:** Platforma food delivery (restaurante, meniuri, comenzi, soferi, clienti)

---

# 1 - Definirea sistemului

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
13. **Personalizare ingrediente** - clientul modifica ingredientele unui produs, acolo unde restaurantul permite
14. **Adauga review** - dupa livrare, clientul poate lasa un review optional pentru sofer si comanda

### Interactiuni sofer
15. **Preia comanda** - soferul selecteaza o comanda din lista celor disponibile si o marcheaza ca in livrare
16. **Finalizeaza livrare** - soferul marcheaza comanda ca livrata, i se crediteaza comisionul in balanta

---

### Reguli de sistem
- Fiecare restaurant are un comision de livrare propriu, soferul primeste un procent din valoarea comenzii livrate
- Statusurile unei comenzi sunt: NEPRELUATA -> IN_LIVRARE -> FINALIZATA
- Clientii si soferii trebuie sa aiba cont inregistrat pentru a interactiona cu platforma
- La plata cu cardul se aplica TVA, la plata cash nu se aplica TVA
- Restaurantele nu au stoc - pot produce la infinit, nu exista capital de restaurant
- Un sofer poate livra pentru orice restaurant
- Cardurile au balanta, clientii pot face top-up oricand
- Soferii au o singura balanta (nu se diferentiaza cash de card pentru ei)

---

## 1.2 - Tipuri de obiecte din domeniu

| Clasa | Tip | Descriere |
|---|---|---|
| **Utilizator** | clasa abstracta | Baza comuna pentru **Client** si **Sofer**, contine nume, email, parola, balanta si metoda abstracta *getRol()* |
| **Client** | clasa, extends **Utilizator** | Adauga cos, adrese salvate, carduri si lista de favorite |
| **Sofer** | clasa, extends **Utilizator** | Adauga status de disponibilitate |
| **Restaurant** | clasa | Entitatea inregistrata pe platforma, are meniu, comision de livrare si lista de produse |
| **Produs** | clasa | Un preparat din meniu, are nume, pret, ingrediente, valori nutritionale si flag de personalizare |
| **Comanda** | clasa | Comanda plasata de un client, contine produse, referinta la **StatusComanda** si gestioneaza tranzitiile FSM |
| **Cos** | clasa | Cosul temporar al clientului inainte de checkout, contine produse si cantitati |
| **Plata** | clasa abstracta | Nu poate fi instantiata direct, contine metoda abstracta *calculeazaTotal()*, extinsa de **PlataCard** si **PlataCash** |
| **PlataCard** | clasa, extends **Plata** | Implementeaza *calculeazaTotal()* cu TVA |
| **PlataCash** | clasa, extends **Plata** | Implementeaza *calculeazaTotal()* fara TVA |
| **CardBancar** | clasa | Cardul unui client, numarul cardului este final, are balanta si suporta operatii de top-up |
| **Adresa** | clasa imutabila | Toate campurile sunt final, fara setteri, bifeaza cerinta explicita din proiect |
| **Review** | clasa | Recenzie optionala lasata de client dupa livrare, are rating si comentariu |
| **Rol** | enum | Valorile CLIENT si SOFER, returnat de *getRol()* din **Utilizator** |
| **StatusComanda** | enum + FSM | Valorile NEPRELUATA, IN_LIVRARE, FINALIZATA, fiecare valoare defineste tranzitiile valide, arunca **TranzitieInvalidaException** la tranzitie invalida |
| **MetodaPlata** | enum | Valorile CARD si CASH, folosit la selectarea metodei de plata in checkout |