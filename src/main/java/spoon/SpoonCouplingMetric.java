package spoon;

import entity.Method;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import utility.Utility;
import visitors.VisitorsClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpoonCouplingMetric {
	private static CtModel model;

	private static VisitorsClass visitorsClass;

	public SpoonCouplingMetric(){}
	public SpoonCouplingMetric(CtModel model) {
		super();
		this.model = model;
	}




	public List<String> AffichagesClass() {
		if (model == null) {
			throw new IllegalStateException("Le modèle Spoon n'a pas été initialisé. Appelez d'abord initializeSpoon.");
		}
		List<String> listes = new ArrayList<>();

		CtPackage ctPackage = model.getAllPackages().iterator().next();
		if (ctPackage != null) {
			// Filter children to get classes
			List<CtClass<?>> classes = ctPackage.filterChildren(new TypeFilter<>(CtClass.class)).list();
			for (CtClass<?> ctClass : classes) {
				if (!listes.contains(ctClass.getSimpleName())) {
					listes.add(ctClass.getSimpleName());
				}
			}
		}
		return listes;
	}

	// verifier pour les classes


	// get class
	public static List<String> getNbMethodeBetweenAllAetB(List<String> a) {
		List<String> allMethodBetweenAB = new ArrayList<>();

		// Get the package containing classes
		CtPackage ctPackage = model.getAllPackages().iterator().next();

		// Iterate over classes
		for (CtType<?> ctClass : ctPackage.getTypes()) {
			// Check if the class is in the list 'a'
			if (a.contains(ctClass.getSimpleName())) {
				// Add methods of the class to the result list
				for (CtMethod<?> ctMethod : ctClass.getMethods()) {
					allMethodBetweenAB.add(ctMethod.getSimpleName());
				}
			}
		}

		return allMethodBetweenAB;
	}

	/** Get a methodes declaration of classes :*/

	public static List<String> getMethodDeclarationClass( String className) {
		List<String> methodsDeclaration = new ArrayList<>();
		// Find the class by name
		CtClass<?> ctClass = model.getElements(new TypeFilter<>(CtClass.class)).stream()
				.filter(c -> c.getSimpleName().equals(className))
				.findFirst()
				.orElse(null);
		if (ctClass != null) {
			// Filter children to get methods
			List<CtMethod<?>> methods = ctClass.filterChildren(new TypeFilter<>(CtMethod.class)).list();
			// Add method names to the result list
			for (CtMethod<?> ctMethod : methods) {
				methodsDeclaration.add(ctMethod.getSimpleName());
			}
		}

		return methodsDeclaration;
	}


	public static List<String> getListInvocationClass(String className) {
		List<String> listeMethodeInvocationPerClass = new ArrayList<>();
		// Trouver la classe par son nom
		CtClass<?> ctClass = model.getElements(new TypeFilter<>(CtClass.class)).stream()
				.filter(c -> c.getSimpleName().equals(className))
				.findFirst()
				.orElse(null);
		if (ctClass != null) {
			// Filtrer les enfants pour obtenir les méthodes
			List<CtMethod<?>> methods = ctClass.filterChildren(new TypeFilter<>(CtMethod.class)).list();
			// Itérer sur les méthodes pour trouver les invocations
			for (CtMethod<?> ctMethod : methods) {
				List<CtExecutableReference<?>> methodInvocations = ctMethod.getElements(new TypeFilter<>(CtExecutableReference.class));
				for (CtExecutableReference<?> invocation : methodInvocations) {
					// Obtenir le nom de la méthode
					String methodName = getMethodName(invocation);
					if (methodName != null && !methodName.isEmpty()) {
						listeMethodeInvocationPerClass.add(methodName);
					}
				}
			}
		}
		return listeMethodeInvocationPerClass;
	}
	private static String getMethodName(CtExecutableReference<?> invocation) {
		// Retourner le nom de la méthode
		return invocation.getSimpleName();
	}



	public static List<String> getListMethodDeclarationInvocation(List<String> methodClassA, List<String> methodClassB) {
		List<String> listeMethodeDeclarationInvocation = new ArrayList<>();
		for (String methodNameA : methodClassA) {
			for (String methodNameB : methodClassB) {
				if (methodNameA.equals(methodNameB) && !listeMethodeDeclarationInvocation.contains(methodNameA)) {
					listeMethodeDeclarationInvocation.add(methodNameA);
				}
			}
		}

		return listeMethodeDeclarationInvocation;
	}



	public static List<Method> getMethodDeclarationInvocationBetweenClasses(String methodA, String methodB, List<String> a , List<String> b){
		return  visitorsClass.getMethodDeclarationInvocationBetweenClasses(methodA, methodB,a,b);
	}



	public static float calculateCouplage(int countA, int countB) {
		if (countB != 0) {
			return (float) countA / countB;
		} else {
			// Handle division by zero case
			return 0.0f;
		}
	}


	public static int getNbMethodIncludInAB(List<String> a , List<String> b ){
		int clsA= a.size();
		int clsB= b.size();
		int s= clsA+clsB;
		return s;
	}

	private static CtType<?> findClassByName(String className) {
		return model.getElements(new TypeFilter<>(CtType.class)).stream()
				.filter(c -> c.getSimpleName().equals(className))
				.findFirst()
				.orElse(null);
	}








	//============================================================
	/**
	 * calcule du nombre total total d'appels
	 */
	public long getTotalNumberOfCalls() {
		long result = 0;
		for (CtType<?> type : model.getAllTypes()) {
			for (CtMethod<?> method : type.getAllMethods()) {
				for (CtInvocation<?> methodInvocation : Query.getElements(method,
						new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
					if (methodInvocation.getTarget().getType() != null) {
						if (!type.getQualifiedName().equals(
								methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
							result++;
						}
					}
				}
			}

		}
		return result;

	}

	/**
	 * calcule du nombre d'appel de la classe A vers la classe B
	 */
	public long getNbRelationsBetweenAB(String classA, String classB) throws IOException {
		long result = 0;
		if (classA.equals(classB)) {
			return 0;
		}
		for (CtType<?> type : model.getAllTypes()) {
			if (classA.equals(type.getQualifiedName())) {
				for (CtMethod<?> method : type.getAllMethods()) {
					for (CtInvocation<?> methodInvocation : Query.getElements(method,
							new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
						if (methodInvocation.getTarget().getType() != null) {
							if (classB.equals(
									methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
								result++;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Le <code>result</code> à été arrondi a 3 décimal après la virugle.
	 */
	public double getCouplingMetricBetweenAB(String fullyQNclassNameA, String fullyQNclassNameB) throws IOException {
		long nbTotalRelations = this.getTotalNumberOfCalls();
		long nbRelationsAB = this.getNbRelationsBetweenAB(fullyQNclassNameA, fullyQNclassNameB);
		double result = (nbRelationsAB + 0.0) / (nbTotalRelations + 0.0);
		return Utility.round(result, 3);
	}

}
