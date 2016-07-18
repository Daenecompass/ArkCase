package com.armedia.acm.plugins.businessprocess.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StartBusinessProcessServiceImpl implements StartBusinessProcessService
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RuntimeService activitiRuntimeService;

    @Override
    public void startBusinessProcess(String processName, Map<String, Object> processVaribales)
    {
        ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, processVaribales);
        log.debug("Started process with ID: {}.", pi.getId());
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

}
