package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.Message;
import org.junit.Test;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProject;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.Results;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieSession;
import org.kie.runtime.conf.ClockTypeOption;

/**
 * This is a sample class to launch a rule.
 */
public class KieHelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();
        
        KieFileSystem kfs = kf.newKieFileSystem().write( "r1.drl", drl );
        ks.newKieBuilder( kfs ).build();

        KieSession ksession = ks.getKieContainer().getKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();
         
        assertEquals( 1, count );
    }

    @Test
    public void testFailingHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( mesage == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();

        KieFileSystem kfs = kf.newKieFileSystem().write( "r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).build();

        assertEquals( 1, results.getInsertedMessages().size() );
    }

    @Test
    public void testHelloWorldWithPackages() throws Exception {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();

        GAV gav = kf.newGav("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = kf.newKieFileSystem()
                .write("src/main/resoureces/org/pkg1/r1.drl", drl1)
                .write("src/main/resoureces/org/pkg2/r2.drl", drl2)
                .write(KieProject.KPROJECT_JAR_PATH, createKieProjectWithPackages(kf, gav).toXML());
        ks.newKieBuilder( kfs ).build();

        KieSession ksession = ks.getKieContainer(gav).getKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    private KieProject createKieProjectWithPackages(KieFactory kf, GAV gav) {
        KieProject kproj = kf.newKieProject()
                .setGroupArtifactVersion(gav);

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1");

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        return kproj;
    }
}
