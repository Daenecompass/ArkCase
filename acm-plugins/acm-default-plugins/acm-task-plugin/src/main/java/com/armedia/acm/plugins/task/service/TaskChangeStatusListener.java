package com.armedia.acm.plugins.task.service;

/*-
 * #%L
 * ACM Default Plugin: Tasks
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * @author nikolche
 */
public class TaskChangeStatusListener implements ApplicationListener<AcmApplicationTaskEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {

        if (event != null)
        {

            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                try
                {
                    // call Mule flow to create the Alfresco folder
                    LOG.debug("Sending message to jms://copyTaskFilesAndFoldersToParent.in for task {}", event.getAcmTask().getId());
                    // AFDP-4146 The EcmFile has JPA mappings such that it can't be sent to JMS. The queue listener
                    // doesn't need the doc-under-review anyway, so easiest solution is to remove it from the AcmTask,
                    // then restore it after the AcmTask is sent to JMS
                    EcmFile docUnderReview = event.getAcmTask().getDocumentUnderReview();
                    event.getAcmTask().setDocumentUnderReview(null);
                    MuleMessage msg = getMuleContextManager().send("jms://copyTaskFilesAndFoldersToParent.in", event);
                    event.getAcmTask().setDocumentUnderReview(docUnderReview);
                    LOG.debug("Done sending message to jms://copyTaskFilesAndFoldersToParent.in for task {}", event.getAcmTask().getId());

                    // TODO: this is fix for bug EDTRM-178 (workaround). We should see why on this point msg is null
                    // (maybe it's normal behaviour?)
                    if (msg != null)
                    {
                        MuleException e = msg.getInboundProperty("executionException");

                        if (e != null)
                        {
                            throw e;
                        }
                    }

                }
                catch (MuleException e)
                {
                    throw new RuntimeException("Error while copying Task documents.", e);
                }

            }
        }
    }

    private boolean checkExecution(String eventType)
    {

        return "com.armedia.acm.app.task.complete".equals(eventType) || "com.armedia.acm.activiti.task.complete".equals(eventType);
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

}
