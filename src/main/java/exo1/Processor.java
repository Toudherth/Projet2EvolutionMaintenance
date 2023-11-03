package exo1;

import entity.Method;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import parse.ParserAST;
import visitors.VisitorsClass;

import java.io.*;
import java.util.*;

public class Processor {
    // VARIABLES : -----------------------------------------------
    private static ParserAST parse;
    private static File folder;
    private static ArrayList<File> javaFiles;
    private static String projectSourcePath ;
    private static VisitorsClass visitorsClass= new VisitorsClass();
    private static List<String> classesName;
    public static float couplage=0;



    /** Constructor & Initialisation uri  for chemin d'acces : */
    public Processor(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
        this.folder = new File(projectSourcePath);
        this.javaFiles = parse.listJavaFilesForFolder(this.folder);
    }


    /** METHODES :
    ============================================ */



    /**Get all method for class name  */
    static List<String> allMethodBetweenAB = new ArrayList<>();

    /** Get mla liste des methodes de la classe A a cause d'initialisation de la variable pour ne pas avoir des problemes de repetition */
    public static List<String>  getNbMethodeBetweenAllAetB(List<String> a  ){
        for(String classa : a){
            allMethodBetweenAB.add(classa);
        }
        return allMethodBetweenAB;

    }
    /** Get mla liste des methodes de la classe B */
    static List<String> allMethodBetweenAllAB = new ArrayList<>();
    public static List<String>  getNbMethodeBetweenAllBetA(List<String> a  ){
        for(String classa : a){
            allMethodBetweenAllAB.add(classa);
        }
        return allMethodBetweenAllAB;
    }

    /** Calculer le nombre de methode */
    public static int calculAllMethods(List<String> a,List<String> b  ){
        /** clacule le nombre de methode pour les classes A et les classes B puis il fait la somme */
        int clsa = getNbMethodeBetweenAllAetB(a).size();
        int clsb= getNbMethodeBetweenAllBetA(b).size();
        int s= clsa+clsb;
        return s;
    }

    /** recuperer le nombre de method between A & B */
    public static int getNbMethodIncludInAB(List<String> a , List<String> b ){
        int clsA= a.size();
        int clsB= b.size();
        int s= clsA+clsB;
        return s;
    }

    /** la methode de calcul de Couplage between A and B**/
    public  static  float Couplage(int a, int b){
         couplage = (float) a / b;
        return couplage;
    }

    /** verification des classes qui sont en relation entre les deux  classes A & B */
    public static  List<String> getListMethodDeclarationInvocation(List<String> methodclassA, List<String> methodclassB){
        List<String> listeMethodeDeclarationInvocation = new ArrayList<>();
        for(String sd: methodclassA){
            for(String si: methodclassB){
                if(sd.toString().equals(si.toString())){
                    listeMethodeDeclarationInvocation.add(sd);
                 }
        }   }
        return listeMethodeDeclarationInvocation;
    }

    /** Get methode invocation of classes  **/
    public static List<String>  getListInvocationClass(String nameClass) {
        List<String> listeMethodeInvocationPerClass = new ArrayList<>();
        for (TypeDeclaration nom : visitorsClass.getClassDeclarations()) {
            String nameC= nom.getName().toString();
            if(nameC.equals(nameClass.toString())){
                //System.out.println(nameC);
                listeMethodeInvocationPerClass= visitorsClass.methodInvocationsOfClassName(nom);
                if(listeMethodeInvocationPerClass.size()!=0){
                    return  listeMethodeInvocationPerClass;
                }
            }
        }
        return listeMethodeInvocationPerClass;
    }


    /** Get a methodes declaration of classes :*/
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

    public void saveGraphAsPNG() throws IOException {

        MutableGraph g = new Parser().read(visitorsClass.getGraphAsDot());
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("graph.png"));

        g.graphAttrs().add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90)).nodeAttrs()
                .add(Color.WHITE.fill()).nodes()
                .forEach(node -> node.add(Color.named(node.name().toString()), Style.lineWidth(4), Style.FILLED));
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("graph-2.png"));

    }

    // recuperation de la liste des methodes d'invocation et de declaration for classes
    public static List<Method> getMethodDeclarationInvocationBetweenClasses(String methodA, String methodB, List<String> a , List<String> b){
      return  visitorsClass.getMethodDeclarationInvocationBetweenClasses(methodA, methodB,a,b);


    }


}
