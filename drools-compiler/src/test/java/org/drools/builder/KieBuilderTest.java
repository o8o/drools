package org.drools.builder;


import org.drools.core.util.FileManager;
import org.drools.kproject.memory.MemoryFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.Message.Level;
import org.kie.builder.impl.KieFileSystemImpl;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class KieBuilderTest {
    protected FileManager fileManager;
    
    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }
    
    @Test
    public void testInMemory() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProject kProj = createKieProject(namespace);
        
        KieFileSystem kfs = createProjectSource(namespace, kProj);
        
        createAndTestKieContainer(kProj.getGroupArtifactVersion(), createKieBuilder(kfs));
    }    

    @Test
    public void testOnDisc() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProject kProj = createKieProject(namespace);
        
        KieFileSystem kfs = createProjectSource(namespace, kProj);
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
        
        File file = fileManager.getRootDirectory() ;
        mfs.writeAsFs( file );
        
        createAndTestKieContainer(kProj.getGroupArtifactVersion(), createKieBuilder( file ));
    }
    
    public KieProject createKieProject(String namespace) {        
        KieFactory kf = KieFactory.Factory.get();
        
        KieProject kProj = kf.newKieProject();
        KieBaseModel kBase1 = kProj.newKieBaseModel(namespace)
                                   .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                                   .setEventProcessingMode( EventProcessingOption.STREAM );        

        
        GAV gav = kf.newGav( namespace, "DEFAULT", "0.1");
        kProj.setGroupArtifactVersion( gav );
        
        return kProj;
    }    

    public KieFileSystem createProjectSource(String namespace, KieProject kProj) {
        KieFactory kf = KieFactory.Factory.get();
        
        KieFileSystem kfs = kf.newKieFileSystem();
        kfs.write("src/main/java/" + namespace.replace('.', '/') + "/Message.java", getMessageClass( namespace ) );
        kfs.write("src/main/resources/META-INF/kproject.xml", kProj.toXML() );
        kfs.write("src/main/resources/" + namespace.replace('.', '/') + "/rule1.drl", getRule(namespace, "r1") );
        
        return kfs;
    }
    
    public KieBuilder createKieBuilder(KieFileSystem kfs) {
        KieServices ks = KieServices.Factory.get();       
        return ks.newKieBuilder( kfs );        
    }

    public KieBuilder createKieBuilder(File file) {
        KieServices ks = KieServices.Factory.get();       
        return ks.newKieBuilder( file );        
    }    
    
    public void createAndTestKieContainer(GAV gav, KieBuilder kb) throws IOException,
            ClassNotFoundException,
            InterruptedException {
        KieServices ks = KieServices.Factory.get();
        
        kb.build();
        
        if ( kb.hasResults( Level.ERROR  ) ) {
            fail("Unable to build KieJar\n" + kb.getResults( ).toString() );
        }
        KieRepository kr = ks.getKieRepository();
        KieJar kJar = kr.getKieJar( gav );
        assertNotNull( kJar );
        
        KieContainer kContainer = ks.getKieContainer( gav );
        KieBase kBase = kContainer.getKieBase( gav.getGroupId() );
        
        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
        assertEquals( "org.kie.test.Message", list.get(0).getClass().getName() );
       
    }
    
    public String getRule(String namespace,
                          String ruleName) {
        String s = "package " + namespace + "\n" +
                "import " + namespace  + ".Message;\n"+
                "global java.util.List list;\n" +
                "rule " + ruleName + " when \n" +
                "then \n" +
                "  Message msg = new Message('hello');" +
                "  list.add(msg); " +
                "end \n" +
                "";
        return s;
    }
    
    public String getMessageClass(String namespace) {
        String s = "package " + namespace  + ";\n" +
                   "import java.lang.*;\n" +
                   "public class Message  {\n" +
                   "    private String text; \n " +
                   "    public Message(String text) { \n" +
                   "        this.text = text; \n" +
                   "    } \n" +
                   "    \n" +
                   "    public String getText() { \n" +
                   "        return this.text;\n" +
                   "    }\n" +
                   "}\n";

        return s;
    }    
}
