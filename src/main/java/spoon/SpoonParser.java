package spoon;

import spoon.legacy.NameFilter;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class SpoonParser {
	private String projectPath;

	public SpoonParser(String projectPath) {
		super();
		this.projectPath = projectPath;
	}

	public CtModel getModel() {
		Launcher launcher = new Launcher();
		launcher.addInputResource(projectPath);
		launcher.buildModel();
		return launcher.getModel();
	}



	public static void main(String[] args) {
		// Create a Spoon launcher
		Launcher launcher = new Launcher();
		System.out.println("fjdfjdjfjdfjdj");

		// Add the source code to the launcher
		launcher.addInputResource("C:\\Users\\DELL\\OneDrive\\Desktop\\class\\src\\Processor.java"); // Update the path accordingly

		System.out.println("ekfjkjdj       ");
		// Build the model
		CtModel model = launcher.buildModel();


		// Retrieve the class named "MyClass"
		CtClass<?> myClass = model.getElements(new NameFilter<CtClass<?>>("Processor")).get(0);

		// Modify the originalMethod to print a different message
		CtMethod<?> originalMethod = myClass.getMethod("getClasses");
		originalMethod.getBody().getStatements().clear();
		originalMethod.getBody().insertBegin(launcher.getFactory().createCodeSnippetStatement(
				"System.out.println(\"Modified method\");"));

		// Print the modified code
		System.out.println(myClass);

		// You can save the modified code to a file if needed
		//  launcher.createOutputWriter().createFileWriter("path/to/ModifiedMyClass.java").write(myClass.toString());

	}

	}
