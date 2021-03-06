package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieProject;
import org.kie.runtime.KieBase;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKieJar implements InternalKieJar {

    private ClassLoader classLoader;
    
    private final Map<String, KieBase> kbases = new HashMap<String, KieBase>();

    protected final KieProject kieProject;

    public AbstractKieJar(KieProject kieProject) {
        this.kieProject = kieProject;
    }        

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public GAV getGAV() {
        return kieProject.getGroupArtifactVersion();
    }

    public void addKieBase(String kBaseName, KieBase kBase) {
        kbases.put(kBaseName, kBase);
    }

    public KieBase getKieBase(String kBaseName) {
        return kbases.get(kBaseName);
    }

    public KieProject getKieProject() {
        return kieProject;
    }
}
