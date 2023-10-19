package main;

import exo1.Couplage;
import exo1.Processor;
import parse.ParserAST;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // VARIABLES :
    public static Processor processor;
    public static List<String> getClasses= new ArrayList<>();
    private static List<String> methodsClassA= new ArrayList<>();
    private static List<String> methodsClassB= new ArrayList<>();

    public static void main(String[] args) throws IOException {

                Scanner scanner = new Scanner(System.in);
                String projectSourcePath = null;
                Processor processor = null;
                ParserAST parserAST = null;
                Couplage couplageClass=null;

                while (projectSourcePath == null) {
                    System.out.print("Enter l'URI de votre projet sans le package src : ");
                    String projectURI = scanner.nextLine();
                    projectSourcePath = projectURI + "/src";

                    try {
                        processor = new Processor(projectSourcePath);
                        parserAST = new ParserAST(projectSourcePath);
                        couplageClass= new Couplage(projectSourcePath);
                    } catch (Exception e) {
                        System.out.println("Erreur : le chemin du projet est incorrect. Veuillez réessayer.");
                        projectSourcePath = null; // Réinitialise le chemin pour réessayer.
                    }
                }

        String choice = "";

        while (true) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("***		BIENVENUE dans application de l'analyse statique d'un programme		***");
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Pour calculez une métrique de couplage entre deux classes A et B : tapez 1");
            System.out.println("Pour générez un graphe de couplage pondéré entre les classes de application : tapez 2");
            System.out.println("Pour l'exercice 2 : tapez 3");
            System.out.println("Pour l'exercice 3 : tapez 4");
            System.out.println("Pour quitter tapez 0");
            choice = scanner.nextLine();
            switch (choice) {
                case "1": {
                    Couplage.myResultExercice1();
                    System.out.println(" ");

                    System.out.println("Pour générez un graphe de couplage pondéré entre les classes de application : tapez 5");
                    System.out.println("Pour quitter : tapez 0 ");
                    choice = scanner.nextLine();
                    switch (choice) {
                        case "5": {
                            System.out.println(processor.getGraph());
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
                    System.out.println("Le graphe d'appels est :");
                    System.out.println(processor.getGraph());
                    //Couplage.resultExercice2();
                    break;
                }
                case "3": {
                    System.out.println("Le graphe d'appels est :");

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
