package com.armedia.acm.services.participants.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.FlushModeType;
import javax.persistence.metamodel.EntityType;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */
public class AcmParticipantService
{

    private AcmParticipantDao participantDao;
    private ParticipantsBusinessRule participantsBusinessRule;
    private AcmParticipantEventPublisher acmParticipantEventPublisher;

    private Set<Class<?>> assignedObjectClasses;

    public void init()
    {
        Set<EntityType<?>> entityTypes = participantDao.getEm().getMetamodel().getEntities();
        assignedObjectClasses = entityTypes.stream()
                .filter(entityType -> AcmAssignedObject.class.isAssignableFrom(entityType.getJavaType()))
                .map(entityType -> entityType.getJavaType()).collect(Collectors.toSet());
    }

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmParticipant saveParticipant(String userId, String participantType, Long objectId, String objectType)
            throws AcmAccessControlException
    {
        if (objectType.equals("FILE") || objectType.equals("FOLDER"))
        {
            throw new InvalidParameterException("Use EcmFileParticipantService methods to change file and folder participants!");
        }

        AcmParticipant returnedParticipant = getParticipantByLdapIdParticipantTypeObjectTypeObjectId(userId, participantType, objectType,
                objectId);

        if (returnedParticipant != null)
        {
            log.debug("Participant {} already exists and is added on object [{}]:[{}] as a {}", userId, objectType, objectId,
                    participantType);

            return returnedParticipant;
        }

        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(userId);
        participant.setParticipantType(participantType);
        participant.setObjectId(objectId);
        participant.setObjectType(objectType);

        CheckParticipantListModel model = new CheckParticipantListModel();
        List<String> errorListAfterRules = applyParticipantRules(participant, model);
        if (errorListAfterRules != null && !errorListAfterRules.isEmpty())
        {
            throw new AcmAccessControlException(errorListAfterRules,
                    "Conflict permissions combination has occurred for the chosen participants");
        }

        AcmParticipant savedParticipant = getParticipantDao().save(participant);

        getAcmParticipantEventPublisher().publishParticipantCreatedEvent(savedParticipant, true);

        log.debug("Added participant [{}] to object type [{}] with object id [{}]", userId, objectType, objectId);

        return savedParticipant;
    }

    private List<String> applyParticipantRules(AcmParticipant participant, CheckParticipantListModel model)
    {
        List<AcmParticipant> allParticipantsFromParentObject = participantDao.findParticipantsForObject(participant.getObjectType(),
                participant.getObjectId());
        if (allParticipantsFromParentObject != null)
        {
            allParticipantsFromParentObject
                    .removeIf(parentObjectParticipant -> parentObjectParticipant.getId().equals(participant.getId()));
            allParticipantsFromParentObject.add(participant);

            model.setParticipantList(allParticipantsFromParentObject);
            model.setObjectType(participant.getObjectType());
            model = participantsBusinessRule.applyRules(model);

            List<String> listOfErrors = new ArrayList<>();
            if (!model.getErrorsList().isEmpty())
            {
                listOfErrors = model.getErrorsList();
            }
            return listOfErrors;
        }
        return null;
    }

    public AcmParticipant getParticipantByLdapIdParticipantTypeObjectTypeObjectId(String participantLdapId, String participantType,
            String objectType, Long objectId)
    {
        return getParticipantByLdapIdParticipantTypeObjectTypeObjectId(participantLdapId, participantType, objectType, objectId,
                FlushModeType.AUTO);
    }

    public AcmParticipant getParticipantByLdapIdParticipantTypeObjectTypeObjectId(String participantLdapId, String participantType,
            String objectType, Long objectId, FlushModeType flushModeType)
    {
        return getParticipantDao().getParticipantByLdapIdParticipantTypeObjectTypeObjectId(participantLdapId, participantType, objectType,
                objectId, flushModeType);
    }

    public AcmParticipant changeParticipantRole(AcmParticipant participant, String newRole) throws AcmAccessControlException
    {
        participant.setParticipantType(newRole);
        CheckParticipantListModel model = new CheckParticipantListModel();

        applyParticipantRules(participant, model);

        AcmParticipant updatedParticipant = getParticipantDao().save(participant);

        getAcmParticipantEventPublisher().publishParticipantUpdatedEvent(updatedParticipant, true);

        return updatedParticipant;
    }

    public List<AcmParticipant> listAllParticipantsPerObjectTypeAndId(String objectType, Long objectId)
    {
        return listAllParticipantsPerObjectTypeAndId(objectType, objectId, FlushModeType.AUTO);
    }

    public List<AcmParticipant> listAllParticipantsPerObjectTypeAndId(String objectType, Long objectId, FlushModeType flushModeType)
    {
        return getParticipantDao().findParticipantsForObject(objectType, objectId, flushModeType);
    }

    public void removeParticipant(Long participantId)
    {
        AcmParticipant participant = getParticipantDao().find(participantId);
        if (participant == null)
        {
            return;
        }
        getParticipantDao().deleteParticipant(participantId);
        getAcmParticipantEventPublisher().publishParticipantDeletedEvent(participant, true);
    }

    public void removeParticipant(AcmParticipant participant)
    {
        if (participant == null)
        {
            return;
        }
        removeParticipant(participant.getId());
    }

    public void removeParticipant(String userId, String participantType, String objectType, Long objectId)
    {
        AcmParticipant participant = getParticipantByLdapIdParticipantTypeObjectTypeObjectId(userId, participantType, objectType, objectId,
                FlushModeType.COMMIT);
        removeParticipant(participant);
    }

    public AcmParticipant findParticipant(Long id)
    {
        return getParticipantDao().find(id);
    }

    public List<AcmParticipant> getParticipantsFromParentObject(String objectType, Long objectId)
    {
        for (Class<?> class1 : assignedObjectClasses)
        {
            AcmAssignedObject assignedObject = null;
            try
            {
                assignedObject = (AcmAssignedObject) class1.newInstance();
            }
            catch (Exception e)
            {
                // should never happen
                log.error("Cannot create new instance of class: " + class1.getName(), e);
                return new ArrayList<>();
            }
            if (assignedObject.getObjectType().equals(objectType))
            {
                return assignedObject.getParticipants();
            }
        }

        log.warn("No participants found for objectType: " + objectType + " objectId: " + objectId);

        return new ArrayList<>();
    }

    public AcmParticipantDao getParticipantDao()
    {
        return participantDao;
    }

    public void setParticipantDao(AcmParticipantDao participantDao)
    {
        this.participantDao = participantDao;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsBusinessRule)
    {
        this.participantsBusinessRule = participantsBusinessRule;
    }

    public AcmParticipantEventPublisher getAcmParticipantEventPublisher()
    {
        return acmParticipantEventPublisher;
    }

    public void setAcmParticipantEventPublisher(AcmParticipantEventPublisher acmParticipantEventPublisher)
    {
        this.acmParticipantEventPublisher = acmParticipantEventPublisher;
    }
}
