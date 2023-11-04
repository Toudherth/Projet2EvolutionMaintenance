package exo2;

import entity.Module;

import java.util.ArrayList;
import java.util.List;

public class Identification {

    private double CP; // Seuil de couplage
    private List<Module> modules;
    private Module module;
    private static Clustering clustering;

    public Identification(double CP) {
        this.CP = CP;
        modules = new ArrayList<>();
    }


    public void identifyModules(List<String> classes, double[][] couplingMatrix) {
        System.out.println("CP : "+CP);
        for (String className : classes) {
            boolean addedToModule = false;

            for (Module module : modules) {
                double avgCoupling = calculateAverageCoupling(module.getClasses(), className, couplingMatrix);
                if (avgCoupling >= CP) {
                    module.addClass(className);
                    addedToModule = true;
                    break;
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
            double metric = clustering.calculateCouplingMetric(moduleClass, newClass);
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





