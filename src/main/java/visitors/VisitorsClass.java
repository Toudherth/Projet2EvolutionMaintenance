package visitors;

import java.util.ArrayList;
//import java.lang.reflect.Method;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import entity.Method;

public class VisitorsClass extends ASTVisitor {

        // VARIABLES :
        private CompilationUnit cu;
        private List<TypeDeclaration> classDeclarations = new ArrayList();
        private List<MethodDeclaration> methodsDeclaration = new ArrayList<>();
        private List<MethodInvocation> methodsInvocation = new ArrayList<>();
        private List<SuperMethodInvocation> superMethodsInvocation = new ArrayList<>();
        private ArrayList<Method> methodsHavingReferences = new ArrayList<Method>();
        private List<String> classesName = new ArrayList<>();


        // METHODES :

        // get All Classes
        public List<TypeDeclaration> getClassDeclarations() {
            return classDeclarations;
        }
        // Get Name Classes :
        public List<String> getClassNames() {
            for(TypeDeclaration td: getClassDeclarations()){
                String name = td.getName().toString();
                classesName.add(name);
            }
            return classesName;
        }

        // Method declaration
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

        private boolean methodContainedInCollection(String methodName, ArrayList<Method> methodsWithReferences2) {
            for (int i = 0; i < methodsWithReferences2.size(); i++) {
                if (methodsWithReferences2.get(i).getMethodName().equals(methodName)) {
                    return true;
                }
            }
            return false;
        }

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

        public String getGraph() {
            StringBuilder res = new StringBuilder("");
            for (Method method : methodsHavingReferences) {
                res.append(method);
            }
            if (res.toString().equals("")) {
                res.append("Il n'y a aucun appel de methodes !");
            }
            return res.toString();
        }

        public String getGraphAsDot() {
            StringBuilder res = new StringBuilder("digraph G {\n");
            for (Method method : methodsHavingReferences) {
                res.append(method.getMethodWithCallsAndEntriesLinks());
            }
            res.append("\n}");
            return res.toString();
        }



    }
