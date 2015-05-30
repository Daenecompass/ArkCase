package com.armedia.acm.plugins.task.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.SearchResults;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.SolrResponse;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;


@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private MuleClient muleClient;
    private ExecuteSolrQuery executeSolrQuery;



    private SearchResults searchResults = new SearchResults();

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(
            @RequestBody AcmTask in,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Creating ad-hoc task.");
        }

        try
        {
        	in.setOwner(authentication.getName());
            //find the complaint id by name
            String objectNumber;
            String obj;
            Long objectId = 0L;
            if(in.getAttachedToObjectName() != ""){
                objectNumber = in.getAttachedToObjectName();
                in.setAttachedToObjectName(objectNumber);
                obj = getObjectsFromSolr(in.getAttachedToObjectType(), in.getAttachedToObjectName(), authentication,0,10,"",null);
                if(obj != null && getSearchResults().getNumFound(obj) > 0){
                    JSONArray results = getSearchResults().getDocuments(obj);
                    JSONObject result = results.getJSONObject(0);
                    objectId = getSearchResults().extractLong(result, SearchConstants.PROPERTY_OBJECT_ID_S);
                }
            }
            else{
                objectId = null;
            }
            if(objectId != null){
                in.setAttachedToObjectId(objectId);
                in.setParentObjectId(objectId);
                in.setParentObjectType(in.getAttachedToObjectType());
            }
            else{
                in.setAttachedToObjectId(null);
            }

            AcmTask adHocTask = getTaskDao().createAdHocTask(in);
            publishAdHocTaskCreatedEvent(authentication, httpSession, adHocTask, true);

            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null);  // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }
    
    protected void publishAdHocTaskCreatedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask created,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
    }


    public String getObjectsFromSolr(String objectType, String objectName, Authentication authentication, int startRow, int maxRows, String sortParams, String userId)
    {
        String retval = null;

        log.debug("Taking objects from Solr for object type = " + objectType);

        String authorQuery = "";
        if (userId != null)
        {
            authorQuery = " AND author_s:" + userId;
        }

        String query = "object_type_s:" + objectType + " AND name:" + objectName + authorQuery + " AND -status_s:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows, sortParams);

            log.debug("Objects was retrieved.");
        }
        catch (MuleException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return retval;
    }


    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

    public ExecuteSolrQuery getExecuteSolrQuery() {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
        this.executeSolrQuery = executeSolrQuery;
    }
    public SearchResults getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults) {
        this.searchResults = searchResults;
    }
}
