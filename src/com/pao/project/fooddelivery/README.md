# Platforma Food Delivery

## Interogari posibile in sistem:

### Afisari

- Afiseaza restaurante
- Afiseaza meniul restaurantului
- Afiseaza istoric comenzi (client)
- Afiseaza comenzi de livrat (sofer)
- Afiseaza ingredientele unui produs
- Afiseaza valori nutritionale pentru produse
- Afiseaza date cont (client)
- Afiseaza checkout comanda (confirmare)

### Interactiuni

- Adauga produse in cos (client)
- Adauga restaurant la favorite (client)
- Adauga adresa/adrese (client)
- Adauga carduri pentru plata cu cardul (client)
- Sterge produse din cos (client)
- Sterge restaurant de la favorite (client)
- Sterge adresa/adrese (client)
- Sterge carduri pentru plata cu cardul (client)
- Checkout la produse (client)
- Selectare plata cu cardul online sau fizica (cu cash) (client)
- Personalizare ingrediente (unde e valabil) (client)
- Adauga review soferului si comenzii, optional (client)
- Preia comanda din lista valabila de comenzi (sofer)

### Reguli de sistem

- Fiecare restaurant va avea un comision de livrare (soferul va primi x% din fiecare comanda livrata) si acesta difera in functie de restaurant
- Nu se ia in considerare timpul de livrare, comanda este ori nepreluata, ori preluata (in proces de livrare), ori finalizata
- Clientii trebuie sa aiba cont ca sa faca comenzi
- Soferii trebuie sa aiba cont ca sa preia comenzi
- La plata cu cardul se ia un TVA de la client
- Restaurantele trebuie inregistrate si se iau ca atare, fara stoc (adica fiecare restaurant poate produce mancare la infinit)
- Un sofer poate livra pentru orice restaurant
- Cardurile au balanta, care poate fi topped up (pot fi adaugati bani, dupa alegerea clientului)
- Restaurantul nu are capital, exista preturi la produse dar restaurantele produc mancare pe gratis
- Soferii au doar balanta, nu conteaza unde le intra banii (adica nu se diferentiaza banii cash de banii pe card pentru soferi)

## Tipuri de obiecte din domeniu

- Client
- Sofer
- Restaurant
- Plata
- PlataCard
- PlataCash
- LivrareService
- RestaurantService (meniul pentru browsing de restaurante)
- ClientCard
- Produs
- Comanda

