import java.util.*;
import java.io.*;

class Graphe {

    // Tableau de taille n, avec n le nombre de sommets du graphe.
    // Pour i allant de 0 à n-1, sommets[i] contient l'ensemble des arcs ayant pour source le noeud (i+1), dans une liste chaînée. 
    Maillon[] sommets;

    Graphe(InputStream in) throws Exception {
        lire(in);
    }

    void lire(InputStream in) throws Exception {
        Scanner s;
        int nombre_sommets;
        String specification_arc;
        String[] parties;
        int numero, source, destination, etiquette;

        s = new Scanner(in);
        nombre_sommets = s.nextInt();
        sommets = new Maillon[nombre_sommets];

        while (s.hasNext()) {
            specification_arc = s.next();
            if (!specification_arc.matches(
                    "[0-9]+/[0-9]+\\+[0-9]+/->-?[0-9]+"))
                throw new Exception("Arc mal formé : " + specification_arc);

            parties = specification_arc.split("/", 2);
            numero = Integer.valueOf(parties[0]);
            parties = parties[1].split("\\+", 2);
            source = Integer.valueOf(parties[0]) - 1;
            parties = parties[1].split("/->", 2);
            destination = Integer.valueOf(parties[0]) - 1;
            etiquette = Integer.valueOf(parties[1]);

            Maillon nouveau, courant;
            nouveau = new Maillon();
            nouveau.arc = new Arc(numero, source, destination,
                    new Etiquette(etiquette));
            nouveau.suivant = null;
            if (sommets[source] == null) {
                sommets[source] = nouveau;
            } else {
                courant = sommets[source];
                while (courant.suivant != null)
                    courant = courant.suivant;
                courant.suivant = nouveau;
            }
        }
    }

    public String toString() {
        String resultat;

        resultat = sommets.length + "\n";
        for (int i = 0; i < sommets.length; i++) {
            Maillon courant;

            courant = sommets[i];
            while (courant != null) {
                resultat += courant.arc + "\n";
                courant = courant.suivant;
            }
        }

        return resultat;
    }


    // Retourne le nombre de sommets dans le graphe
    public int nombreSommets() {
        return sommets.length;
    }

    // Retourne le nombre d'arcs dans le graphe
    public int nombreArcs() {
        int nbArcs = 0;
        for (Maillon m : sommets) {
            while (m != null) {
                m = m.suivant;
                nbArcs++;
            }
        }
        return nbArcs;
    }


    // Cherche si un arc ayant pour source sommetSource a pour destination sommetDest
    // S'il est trouvé, le renvoie. Sinon, renvoie null.
    // Utilisé par d'autres méthodes.
    private Arc chercheArcVers(int sommetSource, int sommetDest) {
        Maillon actuel = sommets[sommetSource];
        while (actuel != null) {
            Arc candidat = actuel.arc;
            if (candidat.destination == sommetDest) return candidat;
            actuel = actuel.suivant;
        }
        return null;
    }

    // Prend en entrée sommet1 et sommet2, deux entiers correspondant à deux sommets.
    // Renvoie vrai s'il existe un arc reliant sommet1 et sommet2
    // Renvoie faux sinon
    // Renvoie également faux si un de entiers est invalide (<0 ou >= nbrSommets)
    public boolean adjacents(int sommet1, int sommet2) {
        if (sommet1 < 0 || sommet2 < 0 || sommet1 >= nombreSommets() || sommet2 >= nombreSommets()) {
            return false;
        }

        // Cherche, pour les deux sommets, s'ils possèdent un arc les reliant.
        if (chercheArcVers(sommet1, sommet2) != null || chercheArcVers(sommet2, sommet1) != null) {
            return true;
        }

        // Si aucun arc ne correspond, renvoie faux
        return false;
    }

    // Prend en entrée sommet1 et sommet2, deux entiers correspondant à deux sommets.
    // Si il existe, renvoie l'arc les reliant.
    // Sinon, renvoie null.
    public Arc arcEntre(int sommet1, int sommet2) {
        if (adjacents(sommet1, sommet2)) {
            Arc candidat = chercheArcVers(sommet1, sommet2);
            if (candidat != null) return candidat;
            else return chercheArcVers(sommet2, sommet1);
        }
        return null;
    }


    // Renvoie un tableau contenant les successeurs du sommet en entrée.
    public int[] successeurs(int sommet) {
        int successeurs[] = new int[0];
        // Si le sommet en entrée n'est pas valide, retourne un tableau vide.
        if (sommet < 0 || sommet >= nombreSommets()) return successeurs;

        // Sinon, vérifie pour tous les sommets s'ils sont adjacents, et compte le nombre trouvés.
        int nbAdjacents = 0;
        boolean adjacents[] = new boolean[nombreSommets()];
        for (int sommet2 = 0; sommet2 < nombreSommets(); sommet2++) {
            if (adjacents(sommet, sommet2)) {
                adjacents[sommet2] = true;
                nbAdjacents++;
            } else adjacents[sommet2] = false;
        }
        successeurs = new int[nbAdjacents];
        int indexSuccesseurs = 0;
        for (int sommet2 = 0; sommet2 < nombreSommets(); sommet2++) {
            if (adjacents[sommet2]) {
                successeurs[indexSuccesseurs] = sommet2;
                indexSuccesseurs++;
            }
        }
        return successeurs;
    }


    // Renvoie un tableau contenant les arcs du graphe
    public Arc[] arcs() {
        Arc arcs[] = new Arc[nombreArcs()];
        int index = 0;
        for (Maillon m : sommets) {
            while (m != null) {
                arcs[index] = m.arc;
                index++;
                m = m.suivant;
            }
        }
        return arcs;
    }

    // Renvoie le degré d'un sommet
    public int degre(int sommet) {
        int degree = 0;
        Maillon m = sommets[sommet];
        while (m != null) {
            degree++;
            m = m.suivant;
        }
        return degree;
    }



    // Renvoie vrai si deux arcs en entrée sont indépendants
    public boolean arcsIndependants(Arc arc1, Arc arc2) {
        if (arc1.source == arc2.source || arc1.source == arc2.destination || arc1.destination == arc2.source || arc1.destination == arc2.destination)
            return false;
        return true;
    }

    //Renvoie vrai si le graphe est biparti
    public boolean estBiparti() {
            int sommetDepart = 0;
            int[] couleurs = new int[nombreSommets()];
            Arrays.fill(couleurs, -1);

            couleurs[sommetDepart] = 1;
            Queue<Integer> file = new LinkedList<>();
            file.add(sommetDepart);

            while (!file.isEmpty()) {
                int sommetCourant = file.poll();

                // Retourne faux s'il y a une boucle auto-référente
                if (adjacents(sommetCourant, sommetCourant))
                    return false;

                for (int voisin = 0; voisin < nombreSommets(); ++voisin) {
                    if (adjacents(sommetCourant, voisin) && couleurs[voisin] == -1) {
                        couleurs[voisin] = 1 - couleurs[sommetCourant];
                        file.add(voisin);
                    } else if (adjacents(sommetCourant, voisin) && couleurs[voisin] == couleurs[sommetCourant]) {
                        return false;
                    }
                }
            }
            return true;
        }

    public boolean estCouplage() {

        if (!estBiparti()) {
            System.out.println("Le graphe n'est pas biparti, le couplage parfait n'est pas possible.");
        }

        if (nombreSommets() % 2 != 0) {
            System.out.println("Couplage impossible");
            return false;
        }

        boolean[] visite = new boolean[nombreSommets()];
        int[] couple = new int[nombreSommets()];
        Arrays.fill(couple, -1);

        for (int u = 0; u < nombreSommets(); u++) {
            if (couple[u] == -1) {
                Arrays.fill(visite, false);
                if (!bpm(u, couple, visite)) {
                    System.out.println("Couplage non trouve");
                    return false;
                }
            }
        }

        System.out.print("Couplage trouve : ");
        for (int i = 0; i < couple.length; i++) {
            if (couple[i] != -1) {
                System.out.print(Math.min(i, couple[i]) + "," + Math.max(i, couple[i]) + " ");
                couple[couple[i]] = -1;
            }
        }
        System.out.println();
        return true;
    }

    private boolean bpm(int u, int[] match, boolean[] visited) {
        for (int v = 0; v < nombreSommets(); v++) {
            if (adjacents(u, v) && !visited[v]) {
                visited[v] = true;
                if (match[v] < 0 || bpm(match[v], match, visited)) {
                    match[v] = u;
                    return true;
                }
            }
        }
        return false;
    }


}
