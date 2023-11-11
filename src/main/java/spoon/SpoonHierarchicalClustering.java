package spoon;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpoonHierarchicalClustering {
    private static CtModel model;
    private static List<String> clusters= new ArrayList<>();
    public SpoonHierarchicalClustering(CtModel model) {
        super();
        this.model = model;
    }
    public static List<String> AffichagesClass() {
        // Récupérer toutes les classes du modèle
        List<CtClass<?>> classes = model.getElements(e -> e instanceof CtClass);
        // Ajouter les noms des classes à la liste des clusters
        for (CtClass<?> ctClass : classes) {
            if( ! clusters.contains(ctClass.getSimpleName())) {
                clusters.add(ctClass.getSimpleName());
            }
        }
        return clusters;
    }

    /** Algorithme de Clustering Hiérarchique */
    public void hierarchicalClustering() {
        System.out.println("Démarrage du processus de Clustering ...........");
        System.out.println("Clustering en cours ...........");
        System.out.println();
        int k = 0;

        while (clusters.size() > 1) {
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
                    }
                }
            }
            // Fusionner les clusters avec la meilleure métrique
            if (cluster1 != null && cluster2 != null) {
                mergeClusters(cluster1, cluster2);
            }
            k++;
            System.out.println("Clusters " + k + " actuels : " + clusters);
        }
    }

    /** Fusionner deux clusters */
    private void mergeClusters(String cluster1, String cluster2) {
        clusters.remove(cluster1);
        clusters.remove(cluster2);
        String newCluster = cluster1 + "+" + cluster2;
        clusters.add(newCluster);
    }

    /** Calcul de la métrique de couplage entre deux classes */
    public static double calculateCouplingMetric(String classA, String classB) {
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

        /** Obtenir les méthodes de déclaration dans un cluster Spoon */
        private static List<String> getMethodsDeclarationInCluster(String cluster) {
            List<String> methods = new ArrayList<>();
            CtClass<?> ctClass = model.getElements(new TypeFilter<>(CtClass.class))
                    .stream()
                    .filter(c -> c.getSimpleName().equals(cluster))
                    .findFirst()
                    .orElse(null);

            if (ctClass != null) {
                List<CtMethod<?>> methodDeclarations = ctClass.filterChildren(new TypeFilter<>(CtMethod.class)).list();
                for (CtMethod<?> ctMethod : methodDeclarations) {
                    methods.add(ctMethod.getSimpleName());
                }
            }
            return methods;
        }

    private static int getTotalPossibleRelations() {
        int totalRelations = 1;

        for (String classA : clusters) {
            for (String classB : clusters) {
                if (!classA.equals(classB)) {
                    List<String> methodsDeclA = getMethodsDeclarationInCluster(classA);
                    List<String> methodsInvB = getMethodInvocationInCluster(classB);
                    List<String> methodsInvA = getMethodInvocationInCluster(classA);
                    List<String> methodsDeclB = getMethodsDeclarationInCluster(classB);

                    for (String methodA : methodsDeclA) {
                        for (String methodB : methodsInvB) {
                            if (methodA.equals(methodB)) {
                                totalRelations++;
                            }
                        }
                    }
                    for (String methodA : methodsInvA) {
                        for (String methodB : methodsDeclB) {
                            if (methodA.equals(methodB)) {
                                totalRelations++;
                            }
                        }
                    }
                }
            }
        }

        return totalRelations;
    }

    private static List<String> getMethodInvocationInCluster(String cluster) {
        List<String> methodsInvocationInCluster = new ArrayList<>();

        // Parcourir chaque classe dans le cluster
        for (String className : clusters) {
            if (className.equals(cluster)) {
                List<String> methodsInvocationInClass = getMethodInvocationClass(className);
                methodsInvocationInCluster.addAll(methodsInvocationInClass);
            }
        }
        // Supprimez les doublons s'il y en a
        Set<String> uniqueMethodsInvocation = new HashSet<>(methodsInvocationInCluster);
        methodsInvocationInCluster.clear();
        methodsInvocationInCluster.addAll(uniqueMethodsInvocation);

        return methodsInvocationInCluster;
    }

    /** Get a method's declaration of a class: */
    public static List<String> getMethodInvocationClass(String nameClass) {
        List<String> listeMethodeInvocationPerClass = new ArrayList<>();
        // Récupérez la référence à la classe
        CtType<?> ctType = model.getElements(new TypeFilter<>(CtType.class))
                .stream()
                .filter(c -> c.getSimpleName().equals(nameClass))
                .findFirst()
                .orElse(null);

        if (ctType != null) {
            // Filtrer les enfants pour obtenir les méthodes
            List<CtExecutableReference<?>> methods = ctType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).list();
            // Itérez sur les méthodes pour trouver les invocations
            for (CtExecutableReference<?> method : methods) {
                CtTypeReference<?> declaringType = method.getDeclaringType();
                String methodName = declaringType.getSimpleName() + "." + method.getSimpleName();
                listeMethodeInvocationPerClass.add(methodName);
            }
        }
        return listeMethodeInvocationPerClass;
    }

}
