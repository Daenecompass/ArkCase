package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 08.11.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"} )
public class AddFileToCaseAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private CaseFileEventUtility caseFileEventUtility;
    private List<ResponseEntity<? extends Object>> uploadedFiles = new ArrayList<>();
    private List<Object> uploadedFilesJSON = new ArrayList<>();

    //private final String uploadFileType = "attachment_case";

    @RequestMapping(value = "/file", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public ResponseEntity<? extends Object> uploadFile(
            @RequestParam("uploadFileType") String uploadFileType,
            @RequestParam("caseFileId") Long caseId,
            //@RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException
    {

        if ( log.isInfoEnabled() )
        {
            log.info("Adding file to case id " + caseId);
        }
        if( uploadFileType == null ) {
            uploadFileType = "Case Attachment";
        }
        CaseFile in = null;
        try
        {
            in = getCaseFileDao().find(caseId);

            if ( in == null )
            {
                throw new AcmObjectNotFoundException("case", caseId, "No Such Case", null);
            }

            String folderId = in.getContainerFolder().getCmisFolderId();
            String objectType = in.getObjectType();
            Long objectId = caseId;
            String objectName = in.getCaseNumber();

            String contextPath = request.getServletContext().getContextPath();

            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
            MultiValueMap<String, MultipartFile> uploadFiles = multipartHttpServletRequest.getMultiFileMap();
            if ( uploadFiles != null )
            {
                for ( Map.Entry<String, List<MultipartFile>> entry : uploadFiles.entrySet() )
                {
                    final List<MultipartFile> files = entry.getValue();
                    if (files != null && ! files.isEmpty() )
                    {
                        for (final MultipartFile file : files)
                        {
                            if ( log.isInfoEnabled() )
                            {
                                log.info("Adding file : " + file.getOriginalFilename());
                            }

                            ResponseEntity<? extends Object> responseEntity =  getEcmFileService().upload(uploadFileType, file, acceptType, contextPath, authentication, folderId,
                                    objectType, objectId, objectName);

                            getCaseFileEventUtility().raiseFileAddedEvent(in,authentication.getName(),true);
                            getUploadedFilesJSON().add(responseEntity.getBody());

                        }
                    }
                }
            }

            return new ResponseEntity<Object>(getUploadedFilesJSON(), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            getCaseFileEventUtility().raiseFileAddedEvent(in,authentication.getName(),false);
            throw new AcmObjectNotFoundException("case", caseId, e.getMessage(), e);
        }
    }

    public CaseFileEventUtility getCaseFileEventUtility() {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility) {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public List<Object> getUploadedFilesJSON() {
        return uploadedFilesJSON;
    }

    public void setUploadedFilesJSON(List<Object> uploadedFilesJSON) {
        this.uploadedFilesJSON = uploadedFilesJSON;
    }

    public List<ResponseEntity<? extends Object>> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<ResponseEntity<? extends Object>> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    /*public String getUploadFileType() {
        return uploadFileType;
    }*/
}