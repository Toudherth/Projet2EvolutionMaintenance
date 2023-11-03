package exo2;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import parse.ParserAST;
import visitors.VisitorsClass;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Clustering {

    /** VARIABLES */
    private static ParserAST parse;
    private static File folder;
    private static ArrayList<File> javaFiles;
    private static String projectSourcePath ;
    private static VisitorsClass visitorsClass= new VisitorsClass();
    private static List<String> classesName;
    private static List<String> clusters= new ArrayList<>();

    /** METHODES */

    // for initialisation of uri :
    public Clustering(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
        this.folder = new File(projectSourcePath);
        this.javaFiles = parse.listJavaFilesForFolder(this.folder);
    }





    public Clustering() {
        super();
        // TODO Auto-generated constructor stub
    }


    // TODO: Algorithme de clusturing

    // TODO : Recuperation de l'ensemble des classes dans la liste cluster

    /** ce qui faut faire cest d'implementer cet algorithme */
    public static List<String>  AffichagesClass() throws IOException {
        for(String nom : display()){
            if(! clusters.contains(nom)){
                clusters.add(nom);
            }}
        return clusters;
    }

    /** recuperation des classes de programme with UC */
    public static List<String> display() throws  IOException {
        for (File file : javaFiles) {
            String content = FileUtils.readFileToString(file);
            CompilationUnit ast = ParserAST.getCompilationUnit(content.toCharArray());
            visitorsClass.setCu(ast);
            ast.accept(visitorsClass);
            classesName = visitorsClass.getClassNames();
        }
        return classesName;
    }

    // TODO: une methode merge qui permet de fusionner entre deux cluster
    public static void HierarchicalClustering(){
        System.out.println("Démmarage du processus de Clustering ...........");
        System.out.println("Clustering en cours ...........");
        int k=0;

        while (clusters.size()>1 ) {
        double bestMetric = -1.0;
        String cluster1 = null;
        String cluster2 = null;


        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                String c1 = clusters.get(i);
                String c2 = clusters.get(j);

                // Calculez la métrique de couplage entre c1 et c2
                double metric = calculateCouplingMetric(c1, c2);

                if (metric > bestMetric) {
                    bestMetric = metric;
                    cluster1 = c1;
                    cluster2 = c2;
                    mergeClusters(cluster1, cluster2,clusters);
                }

                }
            }
            k++;
            System.out.println("Clusters "+ k +" actuels : " + clusters);


        }
       }

    // il faut faire un traitement sur la best metric
    // TODO : La methode pour le merge entre deux clusters
    public  static void mergeClusters(String cluster1, String cluster2, List<String> clusters){
        clusters.remove(cluster1);
        clusters.remove(cluster2);
        String newCluster=cluster1 + "+" + cluster2;

        if(! clusters.contains(newCluster)){
            clusters.add(newCluster);

        }

    }

    private static double calculateCouplingMetric(String classA, String classB) {
        // Obtenez la liste des méthodes de déclaration et d'invocation pour les classes A et B
        List<String> methodsDeclA = getMethodsDeclarationInCluster(classA);
        List<String> methodsDeclB = getMethodsDeclarationInCluster(classB);
        List<String> methodsInvA = getMethodInvocationInCluster(classA);
        List<String> methodsInvB = getMethodInvocationInCluster(classB);

        int relationCount = 0;

        // Parcourez les méthodes d'une classe et vérifiez les relations avec l'autre classe
        for (String methodA : methodsDeclA) {
            for (String methodB : methodsInvB) {
                if (methodA.equals(methodB)) {
                    relationCount++;
                }
            }
        }

        for (String methodA : methodsInvA) {
            for (String methodB : methodsDeclB) {
                if (methodA.equals(methodB)) {
                    relationCount++;
                }
            }
        }
        // Calculez le nombre total de relations possibles entre toutes les classes
        int totalRelations = getTotalPossibleRelations();

        // Calculez la métrique de couplage
        double couplingMetric = (double) relationCount / totalRelations;

        return couplingMetric;
    }

    private static int getTotalPossibleRelations() {
        // pour ne pas avoir le calcul de la metric a une valeur infinity si cetais O
        int totalRelations = 1;
        for (String classA : clusters) {
            for (String classB : clusters) {
                if (!classA.equals(classB)) {
                    List<String> methodsDeclA = getMethodsDeclarationInCluster(classA);
                    List<String> methodsInvB = getMethodInvocationInCluster(classB);
                    List<String> methodsInvA = getMethodInvocationInCluster(classA);
                    List<String> methodsDeclB = getMethodsDeclarationInCluster(classB);

                    totalRelations += methodsDeclA.size() * methodsInvB.size();
                    totalRelations += methodsInvA.size() * methodsDeclB.size();
                }
            }
        }

        return totalRelations;
    }




    /** pour recuperer les methodes de declaration ainsi que les methodes d'invocation de chaque cluster par class et les fusionner */
    private static List<String> getMethodsDeclarationInCluster(String cluster) {
        List<String> methodsDeclarationInCluster1 = new ArrayList<>();
        for (String className : clusters) {
            if(className.equals(cluster)){
                List<String> methodsDeclarationInClass = new ArrayList<>();

                methodsDeclarationInClass = getMethodDeclarationClass(className);
                for(String nameMethod : methodsDeclarationInClass){
                    if(! methodsDeclarationInCluster1.contains(nameMethod)){
                        methodsDeclarationInCluster1.add(nameMethod);
                    }
                }
            }
        }
        return methodsDeclarationInCluster1;
    }

    private static List<String> getMethodInvocationInCluster(String cluster) {
        List<String> methodsInvocationInCluster1 = new ArrayList<>();
        for (String className : clusters) {
            if(className.equals(cluster)){
                List<String> methodsInvocationInClass = new ArrayList<>();
                methodsInvocationInClass = getMethodInvocationClass(className);

                for(String nameMethod : methodsInvocationInClass){
                    if(! methodsInvocationInCluster1.contains(nameMethod)){
                        methodsInvocationInCluster1.add(nameMethod);
                    }
                }
            }

        }

        return methodsInvocationInCluster1;
    }





    /** Get a methodes declaration of classes :*/
    // private static List<String> methodsDeclaration= new ArrayList<>();
    public static  List<String> getMethodDeclarationClass(String nameClass){
        List<String> methodsDeclaration= new ArrayList<>();
        List<MethodDeclaration>  methodsClass = visitorsClass.getMethodsDeclarationClass(nameClass);
        for(MethodDeclaration md: methodsClass){
            if(!methodsDeclaration.contains(md.getName().toString())){
                methodsDeclaration.add(md.getName().toString());
            }

        }
        return methodsDeclaration;
    }

    /** Get methode invocation of classes  **/
    //private static List<String> listeMethodeInvocationPerClass = new ArrayList<>();
    public static List<String>  getMethodInvocationClass(String nameClass) {
        List<String> listeMethodeInvocationPerClass = new ArrayList<>();
        for (TypeDeclaration nom : visitorsClass.getClassDeclarations()) {
            String nameC= nom.getName().toString();
            if(nameC.equals(nameClass.toString())){
                listeMethodeInvocationPerClass= visitorsClass.methodInvocationsOfClassName(nom);
                if(listeMethodeInvocationPerClass.size()!=0){
                    return  listeMethodeInvocationPerClass;
                }
            }
        }
        return listeMethodeInvocationPerClass;
    }







    // TODO : pour ajouter la class dans le cluster
    public void addClasses(Set<String> classesToAdd) {
        for (String classToAdd : classesToAdd) {
            if (!this.getClasses().contains(classToAdd)) {
                this.clusters.add(classToAdd);
            }
        }
    }



    // Getters && Setters

    public List<String> getClasses() {
        return clusters;
    }


}
