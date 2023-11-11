package exo1;

import parse.ParserAST;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Couplage {

    public static Processor processor;
    private static ParserAST parse;
    private static File folder;
    private static ArrayList<File> javaFiles;
    private static String projectSourcePath;
    public static List<String> getClasses = new ArrayList<>();


    public Couplage(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
        this.folder = new File(projectSourcePath);
        this.javaFiles = parse.listJavaFilesForFolder(this.folder);
    }

    public static void myResultExercice1() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Voici la liste des classes de projet : ");
        getClasses = processor.AffichagesClass();
        System.out.println(getClasses);

        String classA = null;
        boolean classAExists = false;
        while (!classAExists) {
            System.out.println("Entrez la classe A : ");
            classA = scanner.nextLine();
            classAExists = processor.checkClasses(classA);
            if (!classAExists) {
                System.out.println("La classe n'est pas définie dans le projet ! Vérifiez le nom dans la liste des classes !");
            }
        }

        String classB = null;
        boolean classBExists = false;
        while (!classBExists) {
            System.out.println("Entrez la classe B : ");
            classB = scanner.nextLine();
            classBExists = processor.checkClasses(classB);
            if (!classBExists) {
                System.out.println("La classe n'est pas définie dans le projet ! Vérifiez le nom dans la liste des classes !");
            }
        }

        List<String> methodDeclarationA = processor.getMethodDeclarationClass(classA);
        List<String> methodInvocationA = processor.getListInvocationClass(classA);
        List<String> methodDeclarationB = processor.getMethodDeclarationClass(classB);
        List<String> methodInvocationB = processor.getListInvocationClass(classB);

        if (classAExists && classBExists) {
            List<String> listeMethodsA = processor.getListMethodDeclarationInvocation(methodDeclarationA, methodInvocationB);

            List<String> listeMethodsB = processor.getListMethodDeclarationInvocation(methodDeclarationB, methodInvocationA);

            List<String> listeMethodstotalA = new ArrayList<>();
            List<String> listeMethodstotalB = new ArrayList<>();

            int nbMethodAll = 0;

            /** boucle pour la recuperation des methodes de declaration et d'invocation for all classes dans le programme */
            for (int i = 1; i < getClasses.size() - 1; i++) {
                for (int j = i + 1; j < getClasses.size(); j++) {
                    String clsA = getClasses.get(i);
                    String clsB = getClasses.get(j);


                    List<String> methodDeclarationAllA = processor.getMethodDeclarationClass(clsA);
                    List<String> methodDeclarationAllB = processor.getMethodDeclarationClass(clsB);
                    List<String> methodInvocationAllA = processor.getListInvocationClass(clsA);
                    List<String> methodInvocationAllB = processor.getListInvocationClass(clsB);

                    /** recuperation des methodes en communs entre chaque pair de classes dans le programme */
                    listeMethodstotalA= processor.getListMethodDeclarationInvocation(methodDeclarationAllA, methodInvocationAllB);
                    listeMethodstotalB = processor.getListMethodDeclarationInvocation(methodDeclarationAllB, methodInvocationAllA);

                    /** clacul de nombre de methodes dans tous le programme */
                    nbMethodAll = processor.calculAllMethods(listeMethodstotalA, listeMethodstotalB);
                }
            }

            /** cette methode permet de recuperer la liste des methodes enrelation entre la classe A et B pour realiser le graphe d'appel */
            processor.getMethodDeclarationInvocationBetweenClasses(classA, classB,listeMethodsA, listeMethodsB);


            int nbMethodAB = processor.getNbMethodIncludInAB(listeMethodsA, listeMethodsB);
            System.out.println("Le nombre d'appels de méthodes qui sont en relation entre la Classe A et la Classe B : " + nbMethodAB);
            System.out.println("Le nombre total d'appels de méthodes dans le programme est : " + nbMethodAll);

            float couplageAB = processor.Couplage(nbMethodAB, nbMethodAll);
            System.out.println("La métrique de couplage entre deux classes " + classA + " et " + classB + " est de : " + couplageAB);
        }
    }
}
