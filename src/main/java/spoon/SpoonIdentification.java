package spoon;

import entity.Module;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class SpoonIdentification {

    private CtModel model;
    private double CP;
    private List<Module> modules;
    private SpoonHierarchicalClustering spoonHierarchicalClustering;
    public SpoonIdentification(CtModel model, double CP) {
        this.model = model;
        this.CP=CP;
        this.modules = new ArrayList<>();
    }



    public void identifyModules(List<String> classes, double[][] couplingMatrix) {
            System.out.println("CP : " + CP);


            for (String className : classes) {
                boolean addedToModule = false;

                CtType<?> ctType = model.getElements(new TypeFilter<>(CtType.class))
                        .stream()
                        .filter(c -> c.getSimpleName().equals(className))
                        .findFirst()
                        .orElse(null);

                if (ctType != null) {
                    for (Module module : modules) {
                        double avgCoupling = calculateAverageCoupling(module.getClasses(), className, couplingMatrix);
                        if (avgCoupling >= CP) {
                            module.addClass(className);
                            addedToModule = true;
                            break;
                        }
                    }
                }

                if (!addedToModule) {
                    Module newModule = new Module("Module " + (modules.size() + 1));
                    newModule.addClass(className);
                    modules.add(newModule);
                }
            }
        }

        private double calculateAverageCoupling(List<String> moduleClasses, String newClass, double[][] couplingMatrix) {
            double sum = 0;
            for (String moduleClass : moduleClasses) {
                double metric = spoonHierarchicalClustering.calculateCouplingMetric(moduleClass, newClass);
                if (metric > 0) {
                    sum += metric;
                }
            }
            return sum / moduleClasses.size();
        }

        public List<Module> getModules() {
            return modules;
        }
}

