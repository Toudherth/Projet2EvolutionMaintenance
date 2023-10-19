package exo1;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import parse.ParserAST;
import visitors.VisitorsClass;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Processor {
    // VARIABLES : -----------------------------------------------
    private static ParserAST parse;
    private static File folder;
    private static ArrayList<File> javaFiles;
    private static String projectSourcePath ;
    private static VisitorsClass visitorsClass= new VisitorsClass();
    private static List<String> classesName;
    public static float couplage=0;

    // METHODES : -----------------------------------------

    // for initialisation of uri :
    public Processor(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
        this.folder = new File(projectSourcePath);
        this.javaFiles = parse.listJavaFilesForFolder(this.folder);
    }


    //La liste des methodes entre la classe A et B
    public static List<String>  getNbMethodeBetweenAetB(List<String> a , List<String> b ){

        // get liste of relations between A and B
        List<String> allMethodBetweenAB = new ArrayList<>();
        for(String classa : a){
            for(String classb: b){
                if(! allMethodBetweenAB.contains(classa)){
                   allMethodBetweenAB.add(classa);
                }
                if(! allMethodBetweenAB.contains(classb)){
                    allMethodBetweenAB.add(classb);
                }
            }
        }
    return allMethodBetweenAB;

    }

    public static int calculAllMethods(List<String> a , List<String> b ){
        return getNbMethodeBetweenAetB(a, b).size();
    }

    // get nbr method includ in A & B
    public static int getNbMethodIncludInAB(List<String> a , List<String> b ){

        return getNbListOfMethoClassAetB(a, b).size();
    }

    // method includ in A & B
    public static List<String> getNbListOfMethoClassAetB(List<String> a , List<String> b ){
        List<String> allMethodEncludInAB = new ArrayList<>();
        for(String classa : a){
            for(String classb: b){
                if(classa.equals(classb)){
                    allMethodEncludInAB.add(classa.toString());
                }
            }
        }
        return allMethodEncludInAB;
    }

    // The method for Couplage between A and B
    public  static  float Couplage(int a, int b){
         couplage = (float) a / b;
        return couplage;
    }

    // recuperer les methodes d'invocations d'une methode
    public static List<String> recuperListMethodeInvocation(String nameClass, String nameMethod){
        List<String>  listeMethodeInvocationPerMethod= new ArrayList<>();
        List<MethodDeclaration>  methodsClass = visitorsClass.getMethodsDeclarationClass(nameClass);
        MethodDeclaration nomMethodDeclaration= methodsClass.get(0);
        System.out.println(" ");
        String nommethodeDec = nomMethodDeclaration.getName().toString();
        if(nommethodeDec.equals(nameMethod)){
            listeMethodeInvocationPerMethod= visitorsClass.methodInvocationsOfMethodName(nomMethodDeclaration);
            System.out.println(listeMethodeInvocationPerMethod);
        }
        return listeMethodeInvocationPerMethod;

    }

    // recuperation des methodes de declaration par classe :
    public static  List<String> getMethodDeclarationClass(String nameClass){
        List<String> methodsDeclaration= new ArrayList<>();
        List<MethodDeclaration>  methodsClass = visitorsClass.getMethodsDeclarationClass(nameClass);
        for(MethodDeclaration md: methodsClass){
            methodsDeclaration.add(md.getName().toString());

        }
        return methodsDeclaration;
    }

    //cette methode pour afficher tous ce qui faut sans repetition de boucle
    public static List<String>  AffichagesClass() throws IOException {
        List<String> listes= new ArrayList<>();
        for(String nom : display()){
            if(! listes.contains(nom)){
                listes.add(nom);
            }
        }
        return listes;
    }

    // Appel de boucle pour verifier si la class donner en param existe dans la liste
    public static boolean checkClasses(String nomClss) throws IOException {
        for(String nom : display()){
            if(nom.equals(nomClss)){
                return true;
            }
        }
        return false;
    }

    // Appel de boucle pour verifier si la class donner en param existe dans la liste
    public static boolean checkMethod(String nomMethod, String nameClass) throws IOException {
        for (String md : getMethodDeclarationClass(nameClass)){
            if(md.equals(nomMethod)){
                return true;
            }
        }
       return false;
    }


    // cette methode recupere les methodes
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


    // pour les graphes
    public String getGraph() {
        return visitorsClass.getGraph();
    }

    public void saveGraph() {
        try {
            FileWriter fw = new FileWriter("graph.dot", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(visitorsClass.getGraphAsDot());
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Exception ecriture fichier");
            e.printStackTrace();
        }
    }


}
