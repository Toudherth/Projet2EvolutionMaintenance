package spoon;

import exo1.Processor;
import parse.ParserAST;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpoonProcessor {

    public static Processor processor;
    private static ParserAST parse;
    private static File folder;
    private static ArrayList<File> javaFiles;
    private static String projectSourcePath;
    private static List<String> getClasses = new ArrayList<>();


    private static SpoonParser spoonParser;
    private static SpoonCouplingMetric spoonCouplingMetric;

    private CtModel model;

    public SpoonProcessor(String projectSourcePath) {
        super();
        this.projectSourcePath = projectSourcePath;
    }

    private void initializeSpoon() {
        spoonParser = new SpoonParser(projectSourcePath);
        spoonCouplingMetric = new SpoonCouplingMetric();
      //  spoonCouplingMetric.initializeSpoon(projectSourcePath);
    }


    public static void myResultExerciceOfSpoon() throws IOException {
        Scanner scanner = new Scanner(System.in);
        spoonParser = new SpoonParser(projectSourcePath);

        //TODO : Calculez une métrique avec Spoon de couplage entre deux classes A et B
        spoonCouplingMetric = new SpoonCouplingMetric(spoonParser.getModel());
        System.out.println("Voici la liste des classes de projet : ");
        getClasses =spoonCouplingMetric.AffichagesClass();

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

        /** je suis dans la partie spoon */

        List<String> methodDeclarationA = spoonCouplingMetric.getMethodDeclarationClass(classA);
        List<String> methodInvocationA = spoonCouplingMetric.getListInvocationClass(classA);
        List<String> methodDeclarationB = spoonCouplingMetric.getMethodDeclarationClass(classB);
        List<String> methodInvocationB = spoonCouplingMetric.getListInvocationClass(classB);


        if (classAExists && classBExists) {
            List<String> listeMethodsA = spoonCouplingMetric.getListMethodDeclarationInvocation(methodDeclarationA, methodInvocationB);

            List<String> listeMethodsB = spoonCouplingMetric.getListMethodDeclarationInvocation(methodDeclarationB, methodInvocationA);

            List<String> listeMethodstotalA = new ArrayList<>();
            List<String> listeMethodstotalB = new ArrayList<>();


            int nbMethodAll = 0;

            /** boucle pour la recuperation des methodes de declaration et d'invocation for all classes dans le programme */
            for (int i = 1; i < getClasses.size() - 1; i++) {
                for (int j = i + 1; j < getClasses.size(); j++) {
                    String clsA = getClasses.get(i);
                    String clsB = getClasses.get(j);


                    List<String> methodDeclarationAllA = spoonCouplingMetric.getMethodDeclarationClass(clsA);
                    List<String> methodDeclarationAllB = spoonCouplingMetric.getMethodDeclarationClass(clsB);
                    List<String> methodInvocationAllA = spoonCouplingMetric.getListInvocationClass(clsA);
                    List<String> methodInvocationAllB = spoonCouplingMetric.getListInvocationClass(clsB);


                    /** recuperation des methodes en communs entre chaque pair de classes dans le programme */
                    listeMethodstotalA= spoonCouplingMetric.getListMethodDeclarationInvocation(methodDeclarationAllA, methodInvocationAllB);
                    listeMethodstotalB = spoonCouplingMetric.getListMethodDeclarationInvocation(methodDeclarationAllB, methodInvocationAllA);

                    /** clacul de nombre de methodes dans tous le programme */
                    nbMethodAll = processor.calculAllMethods(listeMethodstotalA, listeMethodstotalB);
                }
            }

            /** cette methode permet de recuperer la liste des methodes enrelation entre la classe A et B pour realiser le graphe d'appel */
            spoonCouplingMetric.getMethodDeclarationInvocationBetweenClasses(classA, classB,listeMethodsA, listeMethodsB);


            int nbMethodAB = spoonCouplingMetric.getNbMethodIncludInAB(listeMethodsA, listeMethodsB);

            System.out.println();
            System.out.println("Le nombre d'appels de méthodes qui sont en relation entre la Classe A et la Classe B : " + nbMethodAB);
            System.out.println("Le nombre total d'appels de méthodes dans le programme est : " + nbMethodAll);

            float couplageAB = spoonCouplingMetric.calculateCouplage(nbMethodAB, nbMethodAll);
            System.out.println("La métrique de couplage entre deux classes " + classA + " et " + classB + " est de : " + couplageAB);
         }

    }
}
