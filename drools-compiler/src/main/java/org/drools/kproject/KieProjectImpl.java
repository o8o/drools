package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KieProjectImpl implements KieProject {

    private GAV groupArtifactVersion;
    
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KieBaseModel>  kBases;
    
    public KieProjectImpl() {
        kBases = Collections.emptyMap();
    }    

    public GAV getGroupArtifactVersion() {
        return groupArtifactVersion;
    }

    public KieProject setGroupArtifactVersion(GAV groupArtifactVersion) {
        this.groupArtifactVersion = groupArtifactVersion;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKProjectPath()
     */
    public String getKProjectPath() {
        return kProjectPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKProjectPath(java.lang.String)
     */
    public KieProject setKProjectPath(String kprojectPath) {
        this.kProjectPath = kprojectPath;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKBasesPath()
     */
    public String getKBasesPath() {
        return kBasesPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKBasesPath(java.lang.String)
     */
    public KieProject setKBasesPath(String kprojectPath) {
        this.kBasesPath = kprojectPath;
        return this;
    }  
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#addKBase(org.kie.kproject.KieBaseModelImpl)
     */
    public KieBaseModel newKieBaseModel(String name) {
        KieBaseModel kbase = new KieBaseModelImpl(this, name);
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }

    public KieBaseModel newDefaultKieBaseModel() {
        if ( kBases.containsKey(KieBaseModelImpl.DEFAULT_KIEBASE_NAME) ) {
            throw new RuntimeException("This project already contains a default kie base");
        }
        return newKieBaseModel(KieBaseModelImpl.DEFAULT_KIEBASE_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseModel(org.kie.kproject.KieBaseModel)
     */
    public void removeKieBaseModel(String qName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#removeKieBaseModel(org.kie.kproject.KieBaseModel)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KieBaseModel> newMap = new HashMap<String, KieBaseModel>();
        newMap.putAll( this.kBases );
        KieBaseModel kieBaseModel = newMap.remove( oldQName );
        newMap.put( newQName, kieBaseModel);
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#getKieBaseModels()
     */
    public Map<String, KieBaseModel> getKieBaseModels() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KieBaseModel> kBases) {
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        if ( kProjectPath == null) {
            problems.add( "A path to the kproject.properties file must be specified" );
        }
        return problems;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }

    public String toXML() {
        return MARSHALLER.toXML(this);
    }

    public static KieProject fromXML(InputStream kProjectStream) {
        return MARSHALLER.fromXML(kProjectStream);
    }

    public static KieProject fromXML(java.io.File kProjectFile) {
        return MARSHALLER.fromXML(kProjectFile);
    }

    public static KieProject fromXML(URL kProjectUrl) {
        return MARSHALLER.fromXML(kProjectUrl);
    }

    private static final KProjectMarshaller MARSHALLER = new KProjectMarshaller();

    private static class KProjectMarshaller {
        private final XStream xStream = new XStream(new DomDriver());

        private KProjectMarshaller() {
            xStream.registerConverter(new KProjectConverter());
            xStream.registerConverter(new KieBaseModelImpl.KBaseConverter());
            xStream.registerConverter(new KieSessionModelImpl.KSessionConverter());
            xStream.alias("kproject", KieProjectImpl.class);
            xStream.alias("kbase", KieBaseModelImpl.class);
            xStream.alias("ksession", KieSessionModelImpl.class);
        }

        public String toXML(KieProject kieProject) {
            return xStream.toXML(kieProject);
        }

        public KieProject fromXML(InputStream kProjectStream) {
            return (KieProject)xStream.fromXML(kProjectStream);
        }

        public KieProject fromXML(java.io.File kProjectFile) {
            return (KieProject)xStream.fromXML(kProjectFile);
        }

        public KieProject fromXML(URL kProjectUrl) {
            return (KieProject)xStream.fromXML(kProjectUrl);
        }
    }

    public static class KProjectConverter extends AbstractXStreamConverter {

        public KProjectConverter() {
            super(KieProjectImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieProjectImpl kProject = (KieProjectImpl) value;
            writeAttribute(writer, "kBasesPath", kProject.getKBasesPath());
            writeAttribute(writer, "kProjectPath", kProject.getKProjectPath());
            writeObject(writer, context, "groupArtifactVersion", kProject.getGroupArtifactVersion());
            writeObjectList(writer, context, "kbases", "kbase", kProject.getKieBaseModels().values());
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieProjectImpl kProject = new KieProjectImpl();
            kProject.setKBasesPath(reader.getAttribute("kBasesPath"));
            kProject.setKProjectPath(reader.getAttribute("kProjectPath"));

            readNodes(reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader, String name, String value) {
                    if ("groupArtifactVersion".equals(name)) {
                        kProject.setGroupArtifactVersion((GroupArtifactVersion) context.convertAnother(reader.getValue(), GroupArtifactVersion.class));
                    } else if ("kbases".equals(name)) {
                        Map<String, KieBaseModel> kBases = new HashMap<String, KieBaseModel>();
                        for (KieBaseModelImpl kBase : readObjectList(reader, context, KieBaseModelImpl.class)) {
                            kBase.setKProject(kProject);
                            kBases.put(kBase.getName(), kBase);
                        }
                        kProject.setKBases(kBases);
                    }
                }
            });

            return kProject;
        }
    }
}