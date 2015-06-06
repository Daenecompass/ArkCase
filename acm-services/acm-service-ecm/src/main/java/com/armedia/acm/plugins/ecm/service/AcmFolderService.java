package com.armedia.acm.plugins.ecm.service;

import org.json.JSONArray;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import org.mule.api.MuleException;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */
public interface AcmFolderService {

    AcmFolder addNewFolder(Long parentFolderId, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolder(AcmFolder parentFolder, String folderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder addNewFolderByPath(String targetObjectType, Long targetObjectId, String newPath) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    String findContainerFolderPath(AcmContainer container) throws MuleException, AcmUserActionFailedException;

    String findFolderPath(String cmisFolderObjectId) throws MuleException, AcmUserActionFailedException;

    AcmFolder renameFolder(Long folderId, String newFolderName) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException;

    AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder findById(Long folderId);

    AcmCmisObjectList getFolderChildren(String objectType,Long objectId, Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException;

    AcmFolder moveRootFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException;

    AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId, String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException;

    void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException;

}
