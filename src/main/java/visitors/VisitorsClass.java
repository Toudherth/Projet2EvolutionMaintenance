package visitors;

import entity.Method;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisitorsClass extends ASTVisitor {

    // TODO: VARIABLES :
    private CompilationUnit cu;
    private List<TypeDeclaration> classDeclarations = new ArrayList();
    private List<MethodDeclaration> methodsDeclaration = new ArrayList<>();
    private List<MethodInvocation> methodsInvocation = new ArrayList<>();
    private List<SuperMethodInvocation> superMethodsInvocation = new ArrayList<>();
    private ArrayList<Method> methodsHavingReferences = new ArrayList<Method>();
    private List<String> classesName = new ArrayList<>();
    private static ArrayList<Method> methodsHavingReferencesBetweenAB = new ArrayList<Method>();
    private   List<TypeDeclaration> classesN=new ArrayList<>();
    private static  Set<TypeDeclaration> classesnomdeclaration=new HashSet<>();




    // TODO: METHODES :

    /** recuperer toutes les classes de declaration de programme  */
    public List<TypeDeclaration> getClassDeclarations() {
        return classDeclarations;
    }

    /** recuperer toutes les classes de declaration de programme avec type String */
    public List<String> getClassNames() {
        for(TypeDeclaration td: getClassDeclarations()){
            String name = td.getName().toString();
            classesName.add(name);
        }
        return classesName;
    }


    /** Method declaration  de class */
    public   List<MethodDeclaration> getMethodsDeclarationClass(String nomClass)  {
        List<MethodDeclaration> methods = new ArrayList<>();
        for(TypeDeclaration c : getClassDeclarations()){
            if(c.getName().toString().contains(nomClass)){
                for(MethodDeclaration m: c.getMethods()){
                    methods.add(m);
                }
                break;
            }
        } return methods;
    }

    /** recuperation des methodes d'invocations pour chaque classe de programme*/
    public ArrayList<String> methodInvocationsOfClassName(TypeDeclaration classdec) {
        ArrayList<String> references = new ArrayList<>();
        VisitorsClass visitorClass = new VisitorsClass();
        methodsHavingReferences= new ArrayList<>();

        visitorClass.setCu(cu);
        classdec.accept(visitorClass);
        String className = classdec.getName().toString();

        if (!methodContainedInCollection(className, methodsHavingReferences)) {
            references = visitorClass.getMethodsCalls();
            methodsHavingReferences.add(new Method(className, references));
        }
        // Récupérez uniquement les méthodes d'invocation sans le nom de méthode
        ArrayList<String> methodInvocations = new ArrayList<>();
        for (String reference : references) {
            if (!reference.equals(className)) {
                methodInvocations.add(reference);
            }
        }
        return methodInvocations;
    }

    // je peux utiliser cette methode pour stocker les methodes d'invocations et de declaration between les deux classes pour la realisation de graphe
    // recuperation des methodes qui existe entre les deux classes de la methode

    /** pour la recuperations des methodes en commun entre les classes A et B pour la génération de graphe d'appel */
    public static ArrayList<Method> getMethodDeclarationInvocationBetweenClasses(String methodA, String methodB,List<String> a , List<String> b ){
        ArrayList<String> referencesA = new ArrayList<>();
        ArrayList<String> referencesB = new ArrayList<>();

        String methodNameB= methodB.toString();
        String methodNameA= methodA.toString();

        VisitorsClass visitorClass = new VisitorsClass();
        visitorClass.setCu(visitorClass.cu);
        if(a.size()>0){
            for (String classa : a) {
                if(!visitorClass.methodContainedInCollection(classa,methodsHavingReferencesBetweenAB )){
                    referencesA.add(classa);
                }
            }
            methodsHavingReferencesBetweenAB.add(new Method(methodNameA, referencesA));
            methodsHavingReferencesBetweenAB.add(new Method(methodNameB, referencesA));
        }

        if(b.size()>0){
            for (String classb : b) {
                if(!visitorClass.methodContainedInCollectionsFormethods(classb,referencesB )){
                    referencesB.add(classb);
                }
            }
            methodsHavingReferencesBetweenAB.add(new Method(methodNameA, referencesB));
            methodsHavingReferencesBetweenAB.add(new Method(methodNameB, referencesB));
        }
        return methodsHavingReferencesBetweenAB;
    }

    /** return les methodes d'invocation d'une methode name */
    private boolean methodContainedInCollection(String methodName, ArrayList<Method> methodsWithReferences2) {
        for (int i = 0; i < methodsWithReferences2.size(); i++) {
            if (methodsWithReferences2.get(i).getMethodName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    /** verifier si une methode et contenu dans la collection de methode de class */
    private boolean methodContainedInCollectionsFormethods(String methodName, ArrayList<String> methodsWithReferences2) {
        for (int i = 0; i < methodsWithReferences2.size(); i++) {
            if (methodsWithReferences2.get(i).equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    /** pour recuperer les methodes d'invocation*/

    private ArrayList<String> getMethodsCalls() {
        ArrayList<String> res = new ArrayList<>();
        String methodName;
        for (MethodInvocation methodsInvocation : methodsInvocation) {
            methodName = methodsInvocation.getName().toString();
            if (!res.contains(methodName)) {
                res.add(methodName);
            }
        }
        return res;
    }

    /** ------------------- Les methodes des visitors  et AST ------------------- */
    @Override
    public boolean visit(TypeDeclaration node) {
        classDeclarations.add(node);
        return super.visit(node);
    }

    public void setCu(CompilationUnit ast) {
        cu = ast;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.isConstructor()) {
            return false;
        }
        methodsDeclaration.add(node);
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        methodsInvocation.add(node);
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        superMethodsInvocation.add(node);
        return super.visit(node);
    }


    /** ----------- Creation et generation de Graph ------- */
    // on recupere les methodes qui sont en relation entre les deux classes uniquement ça !
    public String getGraph() {
        StringBuilder res = new StringBuilder("");
        for (Method method : methodsHavingReferencesBetweenAB) {
            res.append(method);
        }
        if (res.toString().equals("")) {
            res.append("Il n'y a aucun appel de methodes !");
        }
        return res.toString();
    }

    public String getGraphAsDot() {
        StringBuilder res = new StringBuilder("digraph G {\n");
        for (Method method : methodsHavingReferencesBetweenAB) {
            res.append(method.getMethodWithCallsAndEntriesLinks());
        }
        res.append("\n}");
        return res.toString();
    }



    /** ------------------- des Mehodes de plus que je peux avoir besion ------------------- */

    // cette classe recupere les classes,
    public  List<String>  getClassDeclarationClass(String nomClass)  {
        List<String> classes = new ArrayList<>();
        for(TypeDeclaration c : getClassDeclarations()){
            if(c.getName().toString().contains(nomClass)){
                classes.add(nomClass);
            }
        } return classes;
    }

    // retourn le nom de la classe
    public List<TypeDeclaration> getClassName() {
        for(TypeDeclaration td: getClassDeclarations()){
            String name = td.getName().toString();
            classesN.add(td);
        }
        return classesN;
    }

    // cette methode recupere les methodes d'invocation de la methode donner en paramettre
    public ArrayList<String> methodInvocationsOfMethodName(MethodDeclaration method) {
        ArrayList<String> references = new ArrayList<>();
        VisitorsClass visitorClass = new VisitorsClass();
        visitorClass.setCu(cu);
        method.accept(visitorClass);
        String methodName = method.getName().toString();
        if (!methodContainedInCollection(methodName, methodsHavingReferences)) {
            references = visitorClass.getMethodsCalls();
            methodsHavingReferences.add(new Method(methodName, references));
        }
        // Récupérez uniquement les méthodes d'invocation sans le nom de méthode
        ArrayList<String> methodInvocations = new ArrayList<>();
        for (String reference : references) {
            if (!reference.equals(methodName)) {
                methodInvocations.add(reference);
            }
        }
        return methodInvocations;
    }



}