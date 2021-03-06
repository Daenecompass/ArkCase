package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AcmCaseFileStatusChangedListener implements ApplicationListener<CaseFileModifiedEvent>, InitializingBean
{

    private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private List<String> caseClosedStatuses = new ArrayList<>();

    @Override
    public void onApplicationEvent(CaseFileModifiedEvent event)
    {
        boolean checkIntegrationEnabled = getAlfrescoRecordsService()
                .checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY);

        if (!checkIntegrationEnabled)
        {
            return;
        }

        boolean shouldDeclareRecords = shouldDeclareRecords(event);

        if (shouldDeclareRecords)
        {
            CaseFile caseFile = (CaseFile) event.getSource();

            if (null != caseFile)
            {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(event.getUserId(), event.getUserId());
                getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, caseFile.getContainer(),
                        event.getEventDate(), caseFile.getCaseNumber());

            }
        }
    }

    private boolean shouldDeclareRecords(CaseFileModifiedEvent event)
    {
        LOG.debug("Event: {}", event.getEventType());
        if (AlfrescoRmaPluginConstants.CASE_STATUS_CHANGED_EVENT.equals(event.getEventType().toLowerCase()))
        {
            CaseFile caseFile = (CaseFile) event.getSource();
            if (caseFile != null)
            {
                LOG.debug("Status: {}; status counts as closed: {}",
                        caseFile.getStatus(), getCaseClosedStatuses().contains(caseFile.getStatus()));
            }
            return caseFile != null && getCaseClosedStatuses().contains(caseFile.getStatus());
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug("Finding case closed statuses");
        String statuses = getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty("alfresco_rma_case_closed_statuses");
        if (statuses != null)
        {
            LOG.debug("Case closed statuses: {}", statuses);
            List<String> statusList = Arrays.asList(statuses.split(","));
            statusList = statusList.stream().filter(s -> s != null).filter(s -> !s.trim().isEmpty()).map(s -> s.trim())
                    .collect(Collectors.toList());
            setCaseClosedStatuses(statusList);
        }
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public List<String> getCaseClosedStatuses()
    {
        return caseClosedStatuses;
    }

    public void setCaseClosedStatuses(List<String> caseClosedStatuses)
    {
        this.caseClosedStatuses = caseClosedStatuses;
    }
}
