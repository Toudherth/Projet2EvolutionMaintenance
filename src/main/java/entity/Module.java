package entity;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private String name;
    private List<String> classes;

    public Module(String name) {
        this.name = name;
        this.classes = new ArrayList<>();
    }
    public Module() {
        this.name = name;
        this.classes = new ArrayList<>();
    }

    public void addClass(String className) {
        classes.add(className);
    }

    public List<String> getClasses() {
        return classes;
    }

    public String getName() {
        return name;
    }
}
