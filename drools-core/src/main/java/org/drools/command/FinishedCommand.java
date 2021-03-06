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
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceConfiguration;
import org.kie.builder.ResourceType;
import org.kie.command.Command;
import org.kie.command.Context;
import org.kie.io.Resource;

public class FinishedCommand
    implements
    GenericCommand<Void> {

    public FinishedCommand() {
    }

    public Void execute(Context ctx) {
        return null;
    }

}
