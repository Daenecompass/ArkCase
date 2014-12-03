/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import org.drools.core.RuntimeDroolsException;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.model.AddressHistory;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.Subject;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.service.history.model.AcmHistory;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CaseFileFactory caseFileFactory = new CaseFileFactory();
	private SaveCaseService saveCaseService;
	private AcmHistoryDao acmHistoryDao;

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
	 */
	@Override
	public Object get(String action) 
	{
		Object result = null;
		
		if (action != null) 
		{
			if ("init-form-data".equals(action)) 
			{
				result = initFormData();
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String, org.springframework.util.MultiValueMap)
	 */
	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{

		// Convert XML to Object
		CaseFileForm form = (CaseFileForm) convertFromXMLToObject(cleanXML(xml), CaseFileForm.class);
		
		// Save Case File to the database
		form = saveCaseFile(form);
		
		// Save Address History
		form = saveAddressHistory(form);
		
		// TODO: Save Employment History
		
		// Cave Attachments
		saveAttachments(attachments, form.getCmisFolderId(), FrevvoFormName.CASE_FILE.toUpperCase(), form.getId(), form.getNumber());
		
		// Log the last user action
		if (null != form && null != form.getId())
		{
			getUserActionExecutor().execute(form.getId(), AcmUserActionName.LAST_CASE_CREATED, getAuthentication().getName());
		}
		
		return true;
	}
	
	private CaseFileForm saveCaseFile(CaseFileForm form) throws AcmCreateObjectFailedException 
	{
		CaseFile caseFile = getCaseFileFactory().asAcmCaseFile(form);
		HttpSession session = getRequest().getSession();
		String ipAddress = (String) session.getAttribute("acm_ip_address");
		
		try
        {
			caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), ipAddress);
        }
		catch (MuleException | PersistenceException | RuntimeDroolsException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }
		
		form.setId(caseFile.getId());
		form.setCmisFolderId(caseFile.getEcmFolderId());
		form.setNumber(caseFile.getCaseNumber());
		
		List<AddressHistory> addressHistory = form.getAddressHistory();
		if (addressHistory != null && addressHistory.size() > 0 && 
				caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null  &&
				caseFile.getOriginator().getPerson().getAddresses() != null && 
				caseFile.getOriginator().getPerson().getAddresses().size() > 0)
		{
			for (int i = 0; i < addressHistory.size(); i++)
			{
				addressHistory.get(i).setLocation(caseFile.getOriginator().getPerson().getAddresses().get(i));
			}
		}
		
		if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
		{
			form.getSubject().setPersonId(caseFile.getOriginator().getPerson().getId());		
		}
		
		return form;
	}
	
	private CaseFileForm saveAddressHistory(CaseFileForm form)
	{
		Long personId = form.getSubject().getPersonId();
		List<AddressHistory> addressHistoryArray = form.getAddressHistory();
		
		if (personId != null && addressHistoryArray != null && addressHistoryArray.size() > 0)
		{
			for (AddressHistory addressHistory : addressHistoryArray)
			{
				AcmHistory acmHistory = new AcmHistory();
				acmHistory.setPersonId(personId);
				acmHistory.setObjectId(addressHistory.getLocation().getId());
				acmHistory.setObjectType("ADDRESS");
				acmHistory.setStartDate(addressHistory.getStartDate());
				acmHistory.setEndDate(addressHistory.getEndDate());
				
				getAcmHistoryDao().save(acmHistory);
			}
		}
		
		return form;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#getFormName()
	 */
	@Override
	public String getFormName() 
	{
		return FrevvoFormName.CASE_FILE;
	}
	
	private Object initFormData()
	{
		CaseFileForm caseFileForm = new CaseFileForm();
		Subject subject = new Subject();
		AddressHistory addressHistory = new AddressHistory();
		PostalAddress postalAddress = new PostalAddress();
		List<AddressHistory> addressHistoryList = new ArrayList<AddressHistory>();
		
		caseFileForm.setType("Background Investigation");
		caseFileForm.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".types"), ","));
		subject.setTitles(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".titles"), ","));
		postalAddress.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".locationTypes"), ","));
		addressHistory.setLocation(postalAddress);
		addressHistoryList.add(addressHistory);
		
		caseFileForm.setSubject(subject);
		caseFileForm.setAddressHistory(addressHistoryList);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(caseFileForm);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}

	public CaseFileFactory getCaseFileFactory() 
	{
		return caseFileFactory;
	}

	public void setCaseFileFactory(CaseFileFactory caseFileFactory) 
	{
		this.caseFileFactory = caseFileFactory;
	}

	public SaveCaseService getSaveCaseService() 
	{
		return saveCaseService;
	}

	public void setSaveCaseService(SaveCaseService saveCaseService) 
	{
		this.saveCaseService = saveCaseService;
	}

	public AcmHistoryDao getAcmHistoryDao() 
	{
		return acmHistoryDao;
	}

	public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao) 
	{
		this.acmHistoryDao = acmHistoryDao;
	}

}
