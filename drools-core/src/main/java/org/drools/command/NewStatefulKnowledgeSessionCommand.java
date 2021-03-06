/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.command;

import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeContainer;
import org.kie.command.Context;
import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;

public class NewStatefulKnowledgeSessionCommand
    implements
    GenericCommand<StatefulKnowledgeSession> {

    private KnowledgeSessionConfiguration ksessionConf;
    private Environment environment;
    private String sessionId;

    public NewStatefulKnowledgeSessionCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    public NewStatefulKnowledgeSessionCommand(KnowledgeSessionConfiguration ksessionConf) {
        this.ksessionConf = ksessionConf;
        
    }
    
    public NewStatefulKnowledgeSessionCommand(KnowledgeSessionConfiguration ksessionConf,
                                                Environment env) { 
        this(ksessionConf);
        this.environment = env;
    }

    public StatefulKnowledgeSession execute(Context context) {
        StatefulKnowledgeSession ksession = null;
        if( sessionId != null ) {
            // use the new API to retrieve the session by ID
            KnowledgeContainer kcontainer = ((KnowledgeCommandContext) context).getKnowledgeContainer();
            ksession = kcontainer.getStatefulKnowlegeSession( sessionId );
        } else {
            KnowledgeBase kbase = ((KnowledgeCommandContext) context).getKnowledgeBase();
            ksession = kbase.newStatefulKnowledgeSession( this.ksessionConf, environment );
        }
        return ksession;
    }

}
