package main;

import entity.Module;
import exo1.Couplage;
import exo1.Processor;
import exo2.Clustering;
import exo2.Identification;
import parse.ParserAST;
import spoon.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // VARIABLES :
    private static Processor processor;
    private static Clustering clustering;
    private static List<String> getClasses= new ArrayList<>();
    private static String projectSourcePath;
    private static ParserAST parserAST;
    private static Couplage couplageClass;
    private static SpoonParser spoonParser;
    private static SpoonCouplingMetric spoonCouplingMetric;
    private static SpoonProcessor spoonProcessor;
    private static SpoonHierarchicalClustering spoonHierarchicalClustering;
    private static SpoonIdentification spoonIdentification;


    private static List<String> classes = new ArrayList<>();


    public static void main(String[] args) throws IOException {

                Scanner scanner = new Scanner(System.in);
                projectSourcePath = null;
                processor = null;
                clustering=null;
                parserAST = null;
                couplageClass=null;
                /** Spoon */
                spoonParser= null;
                spoonProcessor=null;
                spoonCouplingMetric=null;
                spoonHierarchicalClustering=null;
                spoonIdentification=null;

                while (projectSourcePath == null) {
                    System.out.print("Enter l'URI de votre projet sans le package src : ");
                    String projectURI = scanner.nextLine();
                    projectSourcePath = projectURI + "/src";

                    try {
                        processor = new Processor(projectSourcePath);
                        clustering= new Clustering(projectSourcePath);
                        parserAST = new ParserAST(projectSourcePath);
                        couplageClass= new Couplage(projectSourcePath);
                        spoonParser= new SpoonParser(projectSourcePath);
                    } catch (Exception e) {
                        System.out.println("Erreur : le chemin du projet est incorrect. Veuillez réessayer.");
                        projectSourcePath = null; // Réinitialise le chemin pour réessayer.
                    }
                }

        String choice = "";

        while (true) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\u001B[1m   ***		BIENVENUE dans application de Compréhension des programmes		***\u001B[0m");
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("1 - Calculer la métrique de couplage entre deux classes A et B ");
            System.out.println("2 - Générer le regroupement hiérarchique des clusters. ");
            System.out.println("3 - Générer l'algorithme d'identification des modules. ");
            System.out.println("4 - Calculer la métrique de couplage entre deux classes A et B en utilisant Spoon.");
            System.out.println("5 - Générer le regroupement hiérarchique des classes (avec Spoon).");;
            System.out.println("6 - Générer l'algorithme d'identification des modules (avec Spoon).");

            System.out.println("0 - Quitter");
            choice = scanner.nextLine();
            switch (choice) {
                case "1": {
                    //TODO : Calculez une métrique de couplage entre deux classes A et B
                    System.out.println("\u001B[1m 1- Calcule de métrique de couplage entre deux classes : \u001B[0m");
                    Couplage.myResultExercice1();
                    System.out.println(" ");
                    System.out.println("8 - Pour générez un graphe de couplage pondéré entre les classes de application. ");
                    System.out.println("0 - Quitter. ");
                    System.out.println("Pour la suite taper sur entrer !!! ");
                    System.out.println(" ");
                    choice = scanner.nextLine();
                    switch (choice) {
                        case "8": {
                            System.out.println(processor.getGraph());
                            processor.saveGraph();
                            processor.saveGraphAsPNG();
                            System.out.println(" ");
                            break;
                        }
                        case "0": {
                            System.out.println("Au revoir ! ");
                            scanner.close();
                            System.exit(0);
                            break;
                        }
                        default:
                            System.out.println("Votre choix ne figure pas dans noter proposition !!!! ");
                            break;
                    }
                    break;
                }

                case "2": {
                    System.out.println("\u001B[1m 2- Algorithme de regroupement hérarchique des clusters : \u001B[0m");

                    System.out.println("Voici la liste des classes de projet : ");
                    getClasses = clustering.AffichagesClass();
                    System.out.println(getClasses);
                    clustering.hierarchicalClustering();
                    System.out.println();
                    break;
                }

                case "3": {
                    System.out.println("\u001B[1m 3- Algorithme d'identification des modules : \u001B[0m");
                    // TODO : ce qu'il faut pour l'algorithme d'indentification
                    classes= clustering.AffichagesClass();
                    double[][] couplingMatrix = new double[classes.size()][classes.size()];

                    double CP = 0.03; // sueil de couplage des modules
                    Identification moduleIdentification = new Identification(CP);
                    moduleIdentification.identifyModules(classes, couplingMatrix);

                    List<Module> identifiedModules = moduleIdentification.getModules();
                    for (Module module : identifiedModules) {
                        System.out.println();
                        System.out.println("Module: " + module.getName());
                        System.out.println("Classes: " + module.getClasses());
                    }
                    System.out.println();

                    break;
                }


                // TODO : **Spoon execution **
                case "4": {
                    //TODO : Calculez une métrique avec Spoon de couplage entre deux classes A et B
                    System.out.println("Spoon ...  ");
                    System.out.println("\u001B[1m 4 - Calculer la métrique de couplage entre deux classes A et B en utilisant Spoon: \u001B[0m");
                    spoonProcessor = new SpoonProcessor(projectSourcePath);

                    spoonProcessor.myResultExerciceOfSpoon();
                    System.out.println(" ");
                    System.out.println("8 - Pour générez un graphe de couplage pondéré entre les classes de application. ");
                    System.out.println("0 - Quitter. ");
                    System.out.println("Pour la suite taper sur entrer !!! ");
                    System.out.println(" ");
                    choice = scanner.nextLine();
                    switch (choice) {
                        case "8": {
                            System.out.println(processor.getGraph());
                            processor.saveGraph();
                            processor.saveGraphAsPNG();
                            System.out.println(" ");
                            break;
                        }
                        case "0": {
                            System.out.println("Au revoir ! ");
                            scanner.close();
                            System.exit(0);
                            break;
                        }
                        default:
                            System.out.println("Votre choix ne figure pas dans noter proposition !!!! ");
                            break;
                    }

                    break;
                }

                case "5": {
                    System.out.println("Spoon ...  ");
                    System.out.println("\u001B[1m 5- Générer le regroupement hiérarchique des classes (avec Spoon) : \u001B[0m");
                    spoonParser = new SpoonParser(projectSourcePath);
                    spoonHierarchicalClustering= new SpoonHierarchicalClustering(spoonParser.getModel());
                    System.out.println("Voici la liste des classes de projet : ");
                    classes = spoonHierarchicalClustering.AffichagesClass();
                    System.out.println(getClasses);
                    spoonHierarchicalClustering.hierarchicalClustering();
                    System.out.println();

                    break;
                }

                case "6": {

                    System.out.println("Spoon ...  ");
                    System.out.println("\u001B[1m 6- Générer l'algorithme d'identification des classes (avec Spoon) : \u001B[0m");
                    spoonParser = new SpoonParser(projectSourcePath);
                    spoonHierarchicalClustering= new SpoonHierarchicalClustering(spoonParser.getModel());

                    System.out.println("Voici la liste des classes de projet : ");
                    classes = spoonHierarchicalClustering.AffichagesClass();
                    double CP = 0.03; // sueil de couplage des modules

                    spoonIdentification = new SpoonIdentification(spoonParser.getModel(), CP);
                    // TODO : ce qu'il faut pour l'algorithme d'indentification
                    double[][] couplingMatrix = new double[classes.size()][classes.size()];
                   // Identification moduleIdentification = new Identification(CP);

                    spoonIdentification.identifyModules(classes, couplingMatrix);

                    List<Module> identifiedModules = spoonIdentification.getModules();
                    System.out.println("la taille est : " +identifiedModules.size());
                    for (Module module : identifiedModules) {
                        System.out.println();
                        System.out.println("Module: " + module.getName());
                        System.out.println("Classes: " + module.getClasses());
                    }
                    System.out.println();




                    break;
                }



                case "0": {
                    System.out.println("Au revoir ! ");
                    scanner.close();
                    System.exit(0);
                    break;
                }
                default:
                    System.out.println("Votre choix ne figure pas dans noter proposition !!!! ( 1, 2, 3, 4, 0) ");
                    break;
            }

        }
    }


}
