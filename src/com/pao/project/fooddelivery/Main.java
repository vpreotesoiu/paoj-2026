package com.pao.project.fooddelivery;

import com.pao.project.fooddelivery.model.*;
import com.pao.project.fooddelivery.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    private static RestaurantService restaurantService = RestaurantService.getInstance();
    private static UtilizatorService utilizatorService = UtilizatorService.getInstance();
    private static ComandaService comandaService = ComandaService.getInstance();
    private static CosService cosService = CosService.getInstance();
    private static ReviewService reviewService = ReviewService.getInstance();

    private static Client clientCrt = null;
    private static Sofer soferCrt = null;

    public static void main(String[] args) {
        initializareDate();

        boolean inMeniu = true;
        while (inMeniu) {
            afiseazaMeniuPrincipal();
            System.out.print("Alege optiune: ");
            int optiune = Integer.parseInt(scanner.nextLine());
            if (optiune == 1) {
                loginClient();
            }
            else if (optiune == 2) {
                loginSofer();
            }
            else if (optiune == 3) {
                inregistrareClient();
            }
            else if (optiune == 4) {
                afiseazaRestaurante();
            }
            else if (optiune == 5) {
                inMeniu = false;
                System.out.println("La revedere!");
            }
            else {
                System.out.println("Optiune invalida! Alege intre 1,2,3,4,5");
            }
        }
    }

    private static void afiseazaMeniuPrincipal() {
        System.out.println("\n----- Meniu FoodDelivery -----");
        System.out.println("1. Login client");
        System.out.println("2. Login sofer");
        System.out.println("3. Inregistrare client");
        System.out.println("4. Afiseaza restaurante");
        System.out.println("5. Iesire");
    }

    private static void loginClient() {
        System.out.print("Email client: ");
        String email = scanner.nextLine();
        try {
            clientCrt = utilizatorService.cautaClientDupaEmail(email);
            System.out.println("Login reusit. Bun venit " + clientCrt.getNume() + "!");
            meniuClient();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void loginSofer() {
        System.out.print("Email sofer: ");
        String email = scanner.nextLine();
        try {
            soferCrt = utilizatorService.cautaSoferDupaEmail(email);
            System.out.println("Login reusit. Bun venit " + soferCrt.getNume() + "!");
            meniuSofer();
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void meniuSofer() {
        boolean inMeniu = true;
        while (inMeniu) {
            System.out.println("----- Meniu Sofer -----");
            System.out.println("1. Afiseaza comenzi disponibile");
            System.out.println("2. Preia comanda");
            System.out.println("3. Finalizeaza livrare");
            System.out.println("4. Afiseaza datele tale de sofer");
            System.out.println("5. Iesire");
            System.out.print("Alege optiune: ");

            int optiune = Integer.parseInt(scanner.nextLine());
            if (optiune == 1) {
                afiseazaComenziDisponibile();
            }
            else if (optiune == 2) {
                preiaComanda();
            }
            else if (optiune == 3) {
                finalizeazaLivrare();
            }
            else if (optiune == 4) {
                afiseazaDateSofer();
            }
            else if (optiune == 5) {
                soferCrt = null;
                inMeniu = false;
            }
            else {
                System.out.println("Optiune invalida! Alege intre 1..5");
            }
        }
    }

    private static void meniuClient() {
        boolean inMeniu = true;

        while (inMeniu) {
            System.out.println("\n----- Meniu Client -----");
            System.out.println("1. Afiseaza restaurantele");
            System.out.println("2. Afiseaza meniul unui restaurant");
            System.out.println("3. Adauga produs in cos");
            System.out.println("4. Afiseaza cosul");
            System.out.println("5. Sterge produsul din cos");
            System.out.println("6. Goleste cosul");
            System.out.println("7. Checkout");
            System.out.println("8. Afiseaza istoricul comenzilor");
            System.out.println("9. Afiseaza comenzile active");
            System.out.println("10. Adauga review");
            System.out.println("11. Adauga adresa");
            System.out.println("12. Sterge adresa");
            System.out.println("13. Adauga card");
            System.out.println("14. Sterge card");
            System.out.println("15. Adauga restaurant la favorite");
            System.out.println("16. Sterge restaurant de la favorite");
            System.out.println("17. Afiseaza datele contului");
            System.out.println("18. Iesire");
            System.out.print("Alege optiune: ");
            int optiune = Integer.parseInt(scanner.nextLine());

            if (optiune == 1) {
                afiseazaRestaurante();
            }
            else if (optiune == 2) {
                afiseazaMeniuRestaurant();
            }
            else if (optiune == 3) {
                adaugaProdusInCos();
            }
            else if (optiune == 4) {
                afiseazaCos();
            }
            else if (optiune == 5) {
                stergeProduseDinCos();
            }
            else if (optiune == 6) {
                golesteCos();
            }
            else if (optiune == 7) {
                checkout();
            }
            else if (optiune == 8) {
                afiseazaIstoricComenzi();
            }
            else if (optiune == 9) {
                afiseazaComenziActive();
            }
            else if (optiune == 10) {
                adaugaReview();
            }
            else if (optiune == 11) {
                adaugaAdresaClient();
            }
            else if (optiune == 12) {
                stergeAdresaClient();
            }
            else if (optiune == 13) {
                adaugaCardClient();
            }
            else if (optiune == 14) {
                stergeCardClient();
            }
            else if (optiune == 15) {
                adaugaRestaurantFavorit();
            }
            else if (optiune == 16) {
                stergeRestaurantFavorit();
            }
            else if (optiune == 17) {
                afiseazaDateClient();
            }
            else if (optiune == 18) {
                clientCrt = null;
                inMeniu = false;
            }
            else {
                System.out.println("Optiune invalida! Alege intre 1..18");
            }
        }
    }

    private static void inregistrareClient() {
        System.out.print("Nume client: ");
        String nume = scanner.nextLine();

        System.out.print("Email client: ");
        String email = scanner.nextLine();

        System.out.print("Parola client: ");
        String parola = scanner.nextLine();

        try {
            Client c = new Client(nume, email, parola);
            utilizatorService.adaugaClient(c);
            System.out.println("Clientul a fost inregistrat cu succes!");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void afiseazaRestaurante() {
        List<Restaurant> restaurante = restaurantService.getRestaurante();
        if (restaurante.isEmpty()) {
            System.out.println("Nu exista restaurante in sistem...");
            return;
        }
        System.out.println("----- Restaurante Disponibile -----");
        for (int i = 0; i < restaurante.size(); ++i) {
            System.out.println((i+1) + ". " + restaurante.get(i));
        }
    }

    private static void afiseazaMeniuRestaurant() {
        System.out.print("Introdu numele restaurantului: ");
        String nume = scanner.nextLine();

        try {
            Restaurant r = restaurantService.cautaDupaNume(nume);
            List<Produs> produse = r.getProduse();
            if (produse.isEmpty()) {
                System.out.println("Restaurantul nu are produse in meniu!");
                return;
            }

            System.out.println("\n----- Meniu " + r.getNume() + "-----");
            for (int i = 0; i < produse.size(); ++i) {
                System.out.println((i+1) + ". " + produse.get(i));
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void adaugaProdusInCos() {
        System.out.print("Introdu numele restaurantului: ");
        String nume = scanner.nextLine();
        try {
            Restaurant r = restaurantService.cautaDupaNume(nume);
            List<Produs> produse = r.getProduse();

            if (produse.isEmpty()) {
                System.out.println("Restaurantul nu are produse in meniu!");
                return;
            }
            System.out.println("\n----- Meniu " + r.getNume() + " -----");
            for (int i = 0; i < produse.size(); ++i) {
                System.out.println((i+1) + ". " + produse.get(i));
            }

            System.out.print("Alege produsul dupa numar: ");
            int i = Integer.parseInt(scanner.nextLine());
            if (i < 1 || i > produse.size()) {
                System.out.println("Indicele precizat este invalid!");
                return;
            }

            Produs p = produse.get(i-1);
            ProdusPersonalizat pp = new ProdusPersonalizat(p);
            List<Ingredient> ingrOpt = p.getIngredienteOptionale();
            for (Ingredient ingr : ingrOpt) {
                System.out.print("Vrei sa elimini ingredientul " + ingr.nume() +
                        "? (da/nu): ");
                String rasp = scanner.nextLine();

                if (rasp.equalsIgnoreCase("da")) {
                    pp.eliminaIngredientOptional(ingr);
                }
            }

            List<IngredientExtra> ingrExtra = p.getIngredienteExtraDisponibile();
            for (IngredientExtra ie : ingrExtra) {
                System.out.print("Cate bucati de " + ie.getIngredient().nume() + " extra vrei?");
                int cantExtra = Integer.parseInt(scanner.nextLine());

                // doar daca clientul spune mai mult de 0 se schimba ceva
                if (cantExtra > 0) {
                    pp.adaugaExtra(ie, cantExtra);
                }
            }
            System.out.print("Cantitate produs: ");
            int cant = Integer.parseInt(scanner.nextLine());
            cosService.adaugaProdus(clientCrt, pp, cant);
            System.out.println("Produsul a fost adaugat in cos cu succes!");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void afiseazaCos() {
        try {
            if (cosService.esteGol(clientCrt)) {
                System.out.println("Cosul este gol.");
                return;
            }
            Map<ProdusPersonalizat, Integer> produseCos = cosService.getProduseCos(clientCrt);

            System.out.println("\n-----Produsele din cos -----");
            int i = 1;
            for (Map.Entry<ProdusPersonalizat, Integer> elem : produseCos.entrySet()) {
                ProdusPersonalizat pp = elem.getKey();
                Integer cant = elem.getValue();

                System.out.println(i + ". " + pp + ", cantitate=" + cant);
                ++i;
            }

            double subtotal = cosService.calculeazaSubtotal(clientCrt);
            System.out.println("Subtotalul este: " + subtotal);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void stergeProduseDinCos() {
        try {
            if (cosService.esteGol(clientCrt)) {
                System.out.println("Cosul este gol.");
                return;
            }

            Map<ProdusPersonalizat, Integer> produseCos = cosService.getProduseCos(clientCrt);
            List<ProdusPersonalizat> produse = new ArrayList<ProdusPersonalizat>(produseCos.keySet());
            System.out.println("\n----- Produsele din cos -----");
            for (int i = 0; i < produse.size(); ++i) {
                ProdusPersonalizat pp = produse.get(i);
                int cant = produseCos.get(pp);
                System.out.println((i+1) + ". " + pp + ", cantitate=" + cant);
            }

            System.out.print("Alege produsul de sters dupa numar: " );
            int i = Integer.parseInt(scanner.nextLine());
            if (i < 1 || i > produse.size()) {
                System.out.println("Indicele precizat nu exista in lista de produse!");
                return;
            }

            ProdusPersonalizat produsSters = produse.get(i-1); // i-1 pt ca lista incepe de la 0

            cosService.stergeProdus(clientCrt, produsSters);
            System.out.println("Produsul a fost sters din cos cu succes!");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void golesteCos() {
        try {
            if (cosService.esteGol(clientCrt)) {
                System.out.println("Cosul este deja gol!");;
                return;
            }

            cosService.golesteCos(clientCrt);
            System.out.println("Cosul a fost golit cu succes.");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void afiseazaIstoricComenzi() {
        try {
            List<Comanda> ist = comandaService.getIstoricClient(clientCrt);
            if (ist.isEmpty()) {
                System.out.println("Clientul nu are nici o comanda finalizata in istoric.");
                return;
            }

            System.out.println("\n----- Istoric comenzi -----");
            for (int i = 0; i < ist.size(); ++i) {
                System.out.println((i+1) + ". " + ist.get(i));
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void adaugaAdresaClient() {
        System.out.print("Strada: ");
        String strada = scanner.nextLine();
        System.out.print("Numar: ");
        int nr = Integer.parseInt(scanner.nextLine());
        System.out.print("Oras: ");
        String oras = scanner.nextLine();
        System.out.print("Cod postal: ");
        String codPostal = scanner.nextLine();

        try {
            Adresa adr = new Adresa(strada, nr, oras, codPostal);
            clientCrt.adaugaAdresa(adr);
            System.out.println("Adresa a fost adaugata cu succes!");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void stergeAdresaClient() {
        List<Adresa> adrs = new ArrayList<Adresa>(clientCrt.getAdrese());
        if (adrs.isEmpty()) {
            System.out.println("Clientul nu are adrese salvate!");
            return;
        }

        System.out.println("----- Adresele dvs. -----");
        for (int i = 0; i < adrs.size(); ++i) {
            System.out.println((i+1) + ". " + adrs.get(i));
        }

        System.out.print("Alege adresa de sters dupa nr.: ");
        int i = Integer.parseInt(scanner.nextLine());

        if (i < 1 || i > adrs.size()) {
            System.out.println("Nu exista o adresa cu acest indice! Incearca altul!");
            return;
        }

        Adresa adr = adrs.get(i-1);
        clientCrt.stergeAdresa(adr);

        System.out.println("Adresa a fost stearsa cu succes!");
    }

    private static void adaugaCardClient() {
        System.out.print("Numar card (stai linistit, e secret >:) ): ");
        String numarCard = scanner.nextLine();

        System.out.print("Balanta initiala: ");
        double bal = Double.parseDouble(scanner.nextLine());

        try {
            CardBancar cb = new CardBancar(numarCard);
            if (bal > 0) {
                cb.topUp(bal);
            }

            clientCrt.adaugaCard(cb);

            System.out.println("Cardul a fost adaugat cu succes!");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void stergeCardClient() {
        List<CardBancar> cbs = new ArrayList<CardBancar>(clientCrt.getCarduri());

        if (cbs.isEmpty()) {
            System.out.println("Clientul nu are carduri salvate...");
            return;
        }

        System.out.println("\n----- Cardurile tale -----");
        for (int i = 0; i < cbs.size(); ++i) {
            System.out.println((i+1) + ". " + cbs.get(i));
        }

        System.out.print("Alege cardul de sters (dupa indice): ");
        int i = Integer.parseInt(scanner.nextLine());
        if (i < 1 || i > cbs.size()) {
            System.out.println("Acest indice este invalid!");
            return;
        }

        CardBancar cb = cbs.get(i-1);
        clientCrt.stergeCard(cb);

        System.out.println("Cardul a fost sters cu succes!");
    }

    private static void adaugaRestaurantFavorit() {
        List<Restaurant> rs = restaurantService.getRestaurante();

        if (rs.isEmpty()) {
            System.out.println("Nu exista restaurante in sistem.");
            return;
        }

        System.out.println("\n----- Restaurante disponibile -----");
        for (int i = 0; i < rs.size(); ++i) {
            System.out.println((i+1) + ". " + rs.get(i));
        }
        System.out.print("Alege restaurantul de adaugat la favorite dupa numar: ");
        int i = Integer.parseInt(scanner.nextLine());
        if (i < 1 || i > rs.size()) {
            System.out.println("Acest indice este invalid!");
            return;
        }

        Restaurant r = rs.get(i-1);
        clientCrt.adaugaRestaurantFavorit(r);
        System.out.println("Restaurantul a fost adaugat la favorite cu succes!");
    }

    private static void stergeRestaurantFavorit() {
        List<Restaurant> rsFav = new ArrayList<Restaurant>(clientCrt.getRestauranteFavorite());
        if (rsFav.isEmpty()) {
            System.out.println("Clientul nu are restaurante favorite.");
            return;
        }

        System.out.println("\n----- Restaurante fav.-----");
        for (int i = 0; i < rsFav.size(); ++i) {
            System.out.println((i+1) + ". " + rsFav.get(i));
        }

        System.out.print("Alege restaurant de sters de la fav. dupa numar: ");
        int i = Integer.parseInt(scanner.nextLine());
        if (i < 1 || i>rsFav.size()) {
            System.out.println("Acest indice este invalid!");
            return;
        }

        Restaurant r = rsFav.get(i-1);
        clientCrt.stergeFavorit(r);

        System.out.println("Restaurantul a fost sters de la favorite cu succes!");
    }

    private static void afiseazaDateClient() {
        System.out.println("\n----- Date cont client -----");
        System.out.println("Nume: " + clientCrt.getNume());
        System.out.println("Email: " + clientCrt.getEmail());
        System.out.println("Rol: " + clientCrt.getRol());

        if (clientCrt instanceof ClientPremium) {
            System.out.println("Abonamentul premium este activ!");
        }
        else {
            System.out.println("Abonamentul premium este inactiv... :(");
        }

        System.out.println("\n----- Adrese salvate -----");
        List<Adresa> adrs = new ArrayList<Adresa>(clientCrt.getAdrese());
        if (adrs.isEmpty()) {
            System.out.println("Nu exista adrese salvate.");
        }
        else {
            for (int i = 0; i < adrs.size(); ++i) {
                System.out.println((i+1) + ". " + adrs.size());
            }
        }

        System.out.println("\n----- Carduri salvate -----");
        List<CardBancar> cbs = new ArrayList<CardBancar>(clientCrt.getCarduri());
        if (cbs.isEmpty()) {
            System.out.println("Nu exista carduri salvate.");
        }
        else {
            for (int i = 0; i < cbs.size(); ++i) {
                System.out.println((i+1) + ". " + cbs.get(i));
            }
        }

        System.out.println("\n----- Restaurante favorite -----");
        List<Restaurant> favs = new ArrayList<Restaurant>(clientCrt.getRestauranteFavorite());
        if (favs.isEmpty()) {
            System.out.println("Nu exista restaurante favorite.");
        }
        else {
            for (int i = 0; i < favs.size(); ++i) {
                Restaurant r = favs.get(i);
                System.out.println((i+1) + ". " + r);

                if (clientCrt instanceof ClientPremium) {
                    if (clientCrt.areLivrareGratuita(r)) {
                        System.out.println("Livrare gratuita premium: da!");
                    }
                    else {
                        System.out.println("Livrare gratuita premium: nu... :c");
                    }
                }
            }
        }

        System.out.println("\n----- Cos curent -----");
        if (cosService.esteGol(clientCrt)) {
            System.out.println("Cosul este gol.");
        }
        else {
            Map<ProdusPersonalizat, Integer> prodCos = cosService.getProduseCos(clientCrt);
            for (Map.Entry<ProdusPersonalizat, Integer> elem : prodCos.entrySet()) {
                ProdusPersonalizat pp = elem.getKey();
                Integer cant = elem.getValue();

                System.out.println("- " + pp + ", cantitate=" + cant);
            }

            System.out.println("Subtotal cos: " + cosService.calculeazaSubtotal(clientCrt));
        }
    }

    private static void afiseazaComenziActive() {
        List<Comanda> comenziActive = comandaService.getComenziActiveClient(clientCrt);
        if (comenziActive.isEmpty()) {
            System.out.println("Clientul nu are comenzi active...");
            return;
        }

        System.out.println("\n----- Comenzi active -----");
        for (int i = 0; i < comenziActive.size(); ++i) {
            System.out.println((i+1) + ". " + comenziActive.get(i));
        }
    }

    private static void afiseazaComenziDisponibile() {
        List<Comanda> comenziDisp = comandaService.getComenziDisponibilePtLivrare();
        if (comenziDisp.isEmpty()) {
            System.out.println("Nu exista comenzi disponibile pentru livrare.");
            return;
        }

        System.out.println("\n----- Comenzi disponibile pt livrare -----");
        for (int i = 0; i < comenziDisp.size(); ++i) {
            System.out.println((i+1) + ". " + comenziDisp.get(i));
        }
    }

    private static void preiaComanda() {
        List<Comanda> comenziDisp = comandaService.getComenziDisponibilePtLivrare();
        if (comenziDisp.isEmpty()) {
            System.out.println("Nu exista comenzi disponibile pentru livrare.");
            return;
        }

        System.out.println("\n----- Comenzi disponibile pentru livrare -----");

        for (int i = 0; i < comenziDisp.size(); ++i) {
            System.out.println((i+1) + ". " + comenziDisp.get(i));
        }
        System.out.print("Alege comanda de preluat dupa numar: ");
        int i = Integer.parseInt(scanner.nextLine());

        if (i < 1 || i > comenziDisp.size()) {
            System.out.println("Indicele pentru comanda este invalid!");
            return;
        }

        try {
            Comanda c = comenziDisp.get(i - 1);
            comandaService.preiaComanda(c.getId(), soferCrt);

            System.out.println("Comanda a fost preluata cu succes!");
        } catch (Exception e) { // prinde diverse cum ar fi daca soferul nu e disponibil
            System.out.println(e.getMessage());
        }
    }

    private static void finalizeazaLivrare() {
        List<Comanda> cmdsInLivrare = new ArrayList<Comanda>();

        for (Comanda c : comandaService.getComenzi()) {
            if (c.getStatus() == StatusComanda.IN_LIVRARE && c.areSoferAsignat() &&
            c.getSoferAsignat().equals(soferCrt)) {
                cmdsInLivrare.add(c);
            }
        }

        if (cmdsInLivrare.isEmpty()) {
            System.out.println("Nu ai comenzi in livrare, smechere!");
            return;
        }

        System.out.println("\n----- Comenzile tale in livrare -----");
        for (int i = 0; i < cmdsInLivrare.size(); ++i) {
            System.out.println((i+1) + ". " + cmdsInLivrare.get(i));
        }

        System.out.print("Alege comanda de finalizat dupa numar: ");
        int i = Integer.parseInt(scanner.nextLine());

        if (i < 1 || i > cmdsInLivrare.size()) {
            System.out.println("Acest indice este invalid pt comanda!");
            return;
        }

        try {
            Comanda c = cmdsInLivrare.get(i-1);
            comandaService.finalizeazaComanda(c.getId());

            System.out.println("Livrarea a fost finalizata cu succes!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void afiseazaDateSofer() {
        System.out.println("\n ----- Date cont sofer -----");
        System.out.println("Nume: " + soferCrt.getNume());
        System.out.println("Email: " + soferCrt.getEmail());
        System.out.println("Rol: " + soferCrt.getRol());
        System.out.println("Balanta: " + soferCrt.getBalanta());
        System.out.println("Disponibil; " + ((soferCrt.isDisponibil()) ? "da" : "nu"));

        double medieRating = reviewService.getMedieRatingSofer(soferCrt);
        System.out.println("Medie rating: " + medieRating);

        List<Review> rs = reviewService.getReviewsSofer(soferCrt);

        System.out.println("\n----- Review-uri primite -----");
        if (rs.isEmpty()) {
            System.out.println("Soferul acesta nu are review-uri.");
        }
        else {
            for (int i = 0; i < rs.size(); ++i) {
                System.out.println((i+1) + ". " + rs.get(i));
            }
        }

        System.out.println("\n----- Comenzi in livrare -----");
        boolean existaComenzi = false;

        for (Comanda c : comandaService.getComenzi()) {
            if (c.getStatus() == StatusComanda.IN_LIVRARE && c.areSoferAsignat() &&
            c.getSoferAsignat().equals(soferCrt)) {
                System.out.println(c);
                existaComenzi = true;
            }
        }

        if (!existaComenzi) {
            System.out.println("Nu exista comenzi in livrare.");
        }
    }

    private static void adaugaReview() {
        List<Comanda> cmdsFinals = comandaService.getIstoricClient(clientCrt);
        List<Comanda> cmdsFaraReview = new ArrayList<Comanda>();

        for (Comanda c : cmdsFinals) {
            if (c.areSoferAsignat() && !reviewService.areReview(c)) {
                cmdsFaraReview.add(c);
            }
        }
        if (cmdsFaraReview.isEmpty()) {
            System.out.println("Nu exista comenzi finalizare fara review.");
            return;
        }

        System.out.println("\n----- Comenzi pt review -----");
        for (int i = 0; i < cmdsFaraReview.size(); ++i) {
            System.out.println((i+1) + ". " + cmdsFaraReview.get(i));
        }

        System.out.print("Alege comanda pentru care vrei sa lasi review: ");
        int i = Integer.parseInt(scanner.nextLine());

        if (i < 1 || i > cmdsFaraReview.size()) {
            System.out.println("Indicele pentru comanda este invalid!");
            return;
        }

        System.out.print("Rating (1-5): ");
        int rating = Integer.parseInt(scanner.nextLine());
        System.out.print("Comentariu: ");
        String comentariu = scanner.nextLine();

        try {
            Comanda c = cmdsFaraReview.get(i-1);
            Review r = new Review(rating, comentariu);

            reviewService.adaugaReview(c, r);

            System.out.println("Review-ul a fost adaugat cu succes!");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkout() {
        try {
            if (cosService.esteGol(clientCrt)) {
                System.out.println("Cosul este gol deci nu se poate face checkout!");
                return;
            }

            System.out.print("Introdu numele restaurantului: ");
            String nume = scanner.nextLine();
            Restaurant r = restaurantService.cautaDupaNume(nume);

            List<Adresa> adrs = new ArrayList<Adresa>(clientCrt.getAdrese());

            if (adrs.isEmpty()) {
                System.out.println("Clientul nu are adrese salvate...");
                return;
            }

            System.out.println("\n----- Adresele tale -----");
            for (int i = 0 ; i < adrs.size(); ++i) {
                System.out.println((i+1) + ". " + adrs.get(i));
            }

            System.out.print("Alege adresa ta de livrare: ");
            int i = Integer.parseInt(scanner.nextLine());

            if (i < 1 || i > adrs.size()) {
                System.out.println("Indicele acesta pentru adresa este invalid!");
                return; // nu poate continua checkout-ul mai departe fara adresa,
                        // pt ca comanda trebuie livrata undeva neaparat
            }

            Adresa adr = adrs.get(i-1);

            System.out.println("\n----- Metoda de plata -----");
            System.out.println("1. Card");
            System.out.println("2. Cash");
            System.out.print("Alege metoda de plata: ");

            int opt = Integer.parseInt(scanner.nextLine());
            MetodaPlata mp;
            if (opt == 1) {
                mp = MetodaPlata.CARD;
            }
            else if (opt == 2) {
                mp = MetodaPlata.CASH;
            }
            else {
                System.out.println("Optiune invalida!");
                return;
            }
            CardBancar cb = null;
            if (mp == MetodaPlata.CARD) {
                List<CardBancar> cbs = new ArrayList<CardBancar>(clientCrt.getCarduri());
                if (cbs.isEmpty()) {
                    System.out.println("Clientul nu are carduri salvate..");
                    return;
                }

                System.out.println("\n----- Cardurile tale -----");
                for (int j = 0; j < cbs.size(); ++j) {
                    System.out.println((j+1) + ". " + cbs.get(j));
                }
                System.out.print("Alege cardul: ");
                int k = Integer.parseInt(scanner.nextLine());

                if (k < 1 || k > cbs.size()) {
                    System.out.println("Indicele pentru card este invalid!");
                    return;
                }

                cb = cbs.get(k-1);
            }

            double subtotal = cosService.calculeazaSubtotal(clientCrt);
            double taxaLivr = comandaService.calculeazaTaxaLivrare(clientCrt, r);
            double total = comandaService.calculeazaCheckout(clientCrt, r, mp);

            System.out.println("\n----- Checkout -----");
            System.out.println("Restaurant: " + r.getNume());
            System.out.println("Adresa livrare: " + adr);
            System.out.println("Metoda plata: " + mp);
            System.out.println("Subtotal produse: " + subtotal);
            System.out.println("Taxa livrare: " + taxaLivr);

            if (mp == MetodaPlata.CARD) {
                System.out.println("TVA card: 9%");
            }
            else {
                System.out.println("TVA cash: 0%");
            }

            System.out.println("Total plata: " + total);

            System.out.print("Confirmi comanda asta? (da sau nu): ");
            String conf = scanner.nextLine();
            if (!conf.equalsIgnoreCase("da")) {
                System.out.println("Checkout-ul a fost anulat!");
                return;
            }

            Comanda c = comandaService.checkout(clientCrt, r, adr, mp, cb);

            System.out.println("Comanda a fost plasata cu succes!");
            System.out.println("Detalii comanda: ");
            System.out.println(c);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    private static void initializareDate() {
        Ingredient chifla = new Ingredient("Chifla", false, false);
        Ingredient carne = new Ingredient("Carne de vita", false, false);
        Ingredient ceapa = new Ingredient("Ceapa", false, true);
        Ingredient castraveti = new Ingredient("Castraveti murati", false, true);
        Ingredient bacon = new Ingredient("Bacon", false, false);
        Ingredient cascaval = new Ingredient("Cascaval", false, false);
        Ingredient sos = new Ingredient("Sos burger", false, false);

        IngredientExtra baconExtra = new IngredientExtra(bacon, 4);
        IngredientExtra cascavalExtra = new IngredientExtra(cascaval, 3.5);
        IngredientExtra sosExtra = new IngredientExtra(sos, 2);

        Produs burgerClasic = new Produs(
                "Burger Clasic",
                "Burger",
                24,
                new ValoriNutritionale(650, 32, 40, 35)
        );
        burgerClasic.adaugaIngredient(chifla);
        burgerClasic.adaugaIngredient(carne);
        burgerClasic.adaugaIngredient(ceapa);
        burgerClasic.adaugaIngredient(castraveti);
        burgerClasic.adaugaIngredientExtraDisponibil(baconExtra);
        burgerClasic.adaugaIngredientExtraDisponibil(cascavalExtra);
        burgerClasic.adaugaIngredientExtraDisponibil(sosExtra);

        Produs cartofi = new Produs(
                "Cartofi Prajiti",
                "Garnitura",
                10,
                new ValoriNutritionale(350, 4, 45, 15)
        );
        cartofi.adaugaIngredient(new Ingredient("Cartofi", false, false));
        cartofi.adaugaIngredient(new Ingredient("Sare", false, true));

        Produs tiramisu = new Produs(
                "Tiramisu",
                "Desert",
                18,
                new ValoriNutritionale(420, 7, 38, 26)
        );
        tiramisu.adaugaIngredient(new Ingredient("Piscoturi", false, false));
        tiramisu.adaugaIngredient(new Ingredient("Cafea", false, false));
        tiramisu.adaugaIngredient(new Ingredient("Cacao", false, true));

        Restaurant burgerHouse = new Restaurant(
                "Burger House",
                new Adresa("Strada Lalelelor", 10, "Bucuresti", "010101"),
                8,
                0.10,
                true
        );
        burgerHouse.adaugaProdus(burgerClasic);
        burgerHouse.adaugaProdus(cartofi);
        burgerHouse.adaugaProdus(tiramisu);

        Ingredient blat = new Ingredient("Blat", false, false);
        Ingredient sosRosii = new Ingredient("Sos rosii", false, false);
        Ingredient mozzarella = new Ingredient("Mozzarella", false, false);
        Ingredient ciuperci = new Ingredient("Ciuperci", false, true);
        Ingredient masline = new Ingredient("Masline", false, true);

        IngredientExtra mozzarellaExtra = new IngredientExtra(new Ingredient("Mozzarella extra", false, false), 4);
        IngredientExtra salamExtra = new IngredientExtra(new Ingredient("Salam extra", false, false), 5);

        Produs pizzaMargherita = new Produs(
                "Pizza Margherita",
                "Pizza",
                28,
                new ValoriNutritionale(700, 28, 75, 30)
        );
        pizzaMargherita.adaugaIngredient(blat);
        pizzaMargherita.adaugaIngredient(sosRosii);
        pizzaMargherita.adaugaIngredient(mozzarella);
        pizzaMargherita.adaugaIngredient(ciuperci);
        pizzaMargherita.adaugaIngredient(masline);
        pizzaMargherita.adaugaIngredientExtraDisponibil(mozzarellaExtra);
        pizzaMargherita.adaugaIngredientExtraDisponibil(salamExtra);

        Produs limonada = new Produs(
                "Limonada",
                "Bautura",
                12,
                new ValoriNutritionale(120, 0, 28, 0)
        );
        limonada.adaugaIngredient(new Ingredient("Apa", false, false));
        limonada.adaugaIngredient(new Ingredient("Lamaie", false, false));
        limonada.adaugaIngredient(new Ingredient("Gheata", false, true));

        Restaurant pizzaRoma = new Restaurant(
                "Pizza Roma",
                new Adresa("Strada Viitorului", 22, "Bucuresti", "020202"),
                6,
                0.12,
                false
        );
        pizzaRoma.adaugaProdus(pizzaMargherita);
        pizzaRoma.adaugaProdus(limonada);

        restaurantService.adaugaRestaurant(burgerHouse);
        restaurantService.adaugaRestaurant(pizzaRoma);

        Client client1 = new Client("Ana Popescu", "ana@mail.com", "parola123");
        client1.adaugaAdresa(new Adresa("Strada Florilor", 5, "Bucuresti", "030303"));
        client1.adaugaAdresa(new Adresa("Strada Libertatii", 15, "Bucuresti", "040404"));

        CardBancar card1 = new CardBancar("1111222233334444");
        card1.topUp(300);
        client1.adaugaCard(card1);

        CardBancar card2 = new CardBancar("5555666677778888");
        card2.topUp(150);
        client1.adaugaCard(card2);

        client1.adaugaRestaurantFavorit(burgerHouse);

        ClientPremium client2 = new ClientPremium("Mihai Ionescu", "mihai@mail.com", "parola123");
        client2.adaugaAdresa(new Adresa("Strada Unirii", 7, "Cluj", "400001"));

        CardBancar card3 = new CardBancar("9999000011112222");
        card3.topUp(500);
        client2.adaugaCard(card3);

        client2.adaugaRestaurantFavorit(pizzaRoma);

        Sofer sofer1 = new Sofer("Dan Soferu", "dan.sofer@mail.com", "parola123");
        Sofer sofer2 = new Sofer("Alex Curieru", "alex.sofer@mail.com", "parola123");

        utilizatorService.adaugaClient(client1);
        utilizatorService.adaugaClient(client2);
        utilizatorService.adaugaSofer(sofer1);
        utilizatorService.adaugaSofer(sofer2);
    }
}
