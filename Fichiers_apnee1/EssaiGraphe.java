import java.io.*;

class EssaiGraphe {
    public static void main(String[] args) {
        FileInputStream f;
        Graphe g;
        try {
            f = new FileInputStream(args[0]);
            g = new Graphe(f);

            // Affichage du nombre de sommets et d'arcs
            System.out.println("Nombre de sommets : " + g.nombreSommets());
            System.out.println("Nombre d'arcs : " + g.nombreArcs());

            // Test de la méthode degré pour le sommet 0
            int sommetTest = 1;
            System.out.println("Degré du sommet " + sommetTest + " : " + g.degre(sommetTest));

            // Test de la méthode arcsIndependants avec deux arcs
            Arc arc1 = g.arcs()[0];
            Arc arc2 = g.arcs()[1];
            System.out.println("Arcs indépendants : " + g.arcsIndependants(arc1, arc2));

            // Test de la méthode estBiparti
            System.out.println("Est biparti : " + g.estBiparti());

            // Test de la méthode couplageParfait
            boolean h = g.estCouplage();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Fonction pour afficher un couplage parfait
    private static void afficherCouplage(Arc[] couplage) {
        for (Arc arc : couplage) {
            System.out.print(arc.numero + ",");
        }
        System.out.println();
    }
}
