package exo1;

import exo1.Processor;
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
    private static String projectSourcePath ;
    public static List<String> getClasses= new ArrayList<>();
    private static List<String> methodsClassA= new ArrayList<>();
    private static List<String> methodsClassB= new ArrayList<>();
    private static String choice = "";

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

        /*
         ******************  la classe A ******************
         */

        // Boucle de répétition pour entrer la classe A
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
        List<String> methodDeclarationA =processor.getMethodDeclarationClass(classA);
        System.out.println(methodDeclarationA);
        String methodeA = null;
        boolean methodeAExists = false;
        while (!methodeAExists) {
            System.out.println("Entrez une methode de la classe A : ");
            methodeA = scanner.nextLine();
            methodeAExists = processor.checkMethod(methodeA, classA);
            if (!methodeAExists) {
                System.out.println("La methode n'existe pas dans la liste de methodes de classe "+classA+" ! Vérifiez le nom de la methode dans la liste !");
            }
        }
        /*
         ******************  la classe B ******************
         */
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
        List<String> methodDeclarationB =  processor.getMethodDeclarationClass(classB);
        System.out.println(methodDeclarationB);
        String methodeB = null;
        boolean methodeBExists = false;
        while (!methodeBExists) {
            System.out.println("Entrez une methode de la classe B : ");
            methodeB = scanner.nextLine();
            methodeBExists = processor.checkMethod(methodeB, classB);
            if (!methodeBExists) {
                System.out.println("La methode n'existe pas dans la liste de methodes de classe "+classB+" ! Vérifiez le nom de la methode dans la liste !");
            }
        }

        System.out.println();

        // Reste du traitement sur le couplage :
        if (classAExists && classBExists && methodeAExists && methodeBExists) {
            methodsClassA = processor.recuperListMethodeInvocation(classA, methodeA);
            methodsClassB = processor.recuperListMethodeInvocation(classB, methodeB);

            int nbMethodAB = processor.getNbMethodIncludInAB(methodsClassA, methodsClassB);
            int nbMethodAll = processor.calculAllMethods(methodsClassA, methodsClassB);
            float couplageAB = processor.Couplage(nbMethodAB, nbMethodAll);
            System.out.println("La métrique de couplage entre deux classes " + classA + " et " + classB + " est de : " + couplageAB);
        }




    }






}
