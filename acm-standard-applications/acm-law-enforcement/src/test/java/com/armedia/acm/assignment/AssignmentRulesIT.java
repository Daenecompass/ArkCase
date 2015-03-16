package com.armedia.acm.assignment;

import com.armedia.acm.plugins.complaint.model.Complaint;
import org.drools.compiler.compiler.DroolsError;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/17/14.
 */
public class AssignmentRulesIT
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-assignment-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if ( kbuilder.hasErrors() )
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors() )
            {
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

    }

    @Test
    public void complaint_defaultAssignee() throws Exception
    {
        assertNotNull(workingMemory);

        Complaint c = new Complaint();
        c.setStatus("DRAFT");

        log.debug("object type: " + c.getObjectType());

        assertTrue(c.getParticipants().isEmpty());

        workingMemory.execute(c);

        assertEquals(2, c.getParticipants().size());

        assertEquals("samuel-acm", c.getParticipants().get(0).getParticipantLdapId());
        assertEquals("assignee", c.getParticipants().get(0).getParticipantType());
        assertEquals("*", c.getParticipants().get(1).getParticipantLdapId());
        assertEquals("*", c.getParticipants().get(1).getParticipantType());

        // since we have participants now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(2, c.getParticipants().size());

    }


}