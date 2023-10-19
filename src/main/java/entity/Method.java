package entity;

import java.util.ArrayList;

public class Method {
    String methodName;
    ArrayList<String> methodInvocations = new ArrayList<>();

    public Method(String name, ArrayList<String> methodInvocations) {
        this.methodName = name;
        this.methodInvocations = methodInvocations;
    }

    public ArrayList<String> getMethodInvocations() {
        return methodInvocations;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodInvocations(ArrayList<String> methodInvocations) {
        this.methodInvocations = methodInvocations;
    }

    public void setName(String name) {
        this.methodName = name;
    }

    // Liste d'invocation de methode pour recuperer les methodes d'invo pour le graphe d'appel
    public void addMethodInvocation(Method method) {
        methodInvocations.add(method.getMethodName().toString());
    }

    public String getMethodWithCallsAndEntriesLinks() {
        StringBuilder res = new StringBuilder("");
        for (String methodInvocation : methodInvocations) {
            res.append(methodName).append("->").append(methodInvocation).append(" ");
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return "methodName=" + methodName + ", methodInvocations=" + methodInvocations +"\n";
    }
}
