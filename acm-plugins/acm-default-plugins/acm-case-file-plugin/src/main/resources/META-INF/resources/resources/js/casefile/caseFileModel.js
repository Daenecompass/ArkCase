/**
 * CaseFile.Model
 *
 * @author jwu
 */
CaseFile.Model = CaseFile.Model || {
    create : function() {
        if (CaseFile.Model.Lookup.create)         {CaseFile.Model.Lookup.create();}
        if (CaseFile.Model.Tree.create)           {CaseFile.Model.Tree.create();}
        if (CaseFile.Model.Documents.create)      {CaseFile.Model.Documents.create();}
        if (CaseFile.Model.Detail.create)         {CaseFile.Model.Detail.create();}
        if (CaseFile.Model.People.create)         {CaseFile.Model.People.create();}
        if (CaseFile.Model.Notes.create)          {CaseFile.Model.Notes.create();}
        if (CaseFile.Model.Tasks.create)          {CaseFile.Model.Tasks.create();}
        if (CaseFile.Model.References.create)     {CaseFile.Model.References.create();}
        if (CaseFile.Model.Events.create)         {CaseFile.Model.Events.create();}
        if (CaseFile.Model.Correspondence.create) {CaseFile.Model.Correspondence.create();}
        if (CaseFile.Model.Time.create)           {CaseFile.Model.Time.create();}
        if (CaseFile.Model.Cost.create)           {CaseFile.Model.Cost.create();}

        if (CaseFile.Service.create)              {CaseFile.Service.create();}

        if ("undefined" != typeof Topbar) {
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA, this.onTopbarViewSetAsnData);
        }
    }
    ,onInitialized: function() {
        if (CaseFile.Model.Lookup.onInitialized)         {CaseFile.Model.Lookup.onInitialized();}
        if (CaseFile.Model.Tree.onInitialized)           {CaseFile.Model.Tree.onInitialized();}
        if (CaseFile.Model.Documents.onInitialized)      {CaseFile.Model.Documents.onInitialized();}
        if (CaseFile.Model.Detail.onInitialized)         {CaseFile.Model.Detail.onInitialized();}
        if (CaseFile.Model.Notes.onInitialized)          {CaseFile.Model.Notes.onInitialized();}
        if (CaseFile.Model.Tasks.onInitialized)          {CaseFile.Model.Tasks.onInitialized();}
        if (CaseFile.Model.References.onInitialized)     {CaseFile.Model.References.onInitialized();}
        if (CaseFile.Model.Events.onInitialized)         {CaseFile.Model.Events.onInitialized();}
        if (CaseFile.Model.Correspondence.onInitialized) {CaseFile.Model.Correspondence.onInitialized();}
        if (CaseFile.Model.Time.onInitialized)           {CaseFile.Model.Time.onInitialized();}
        if (CaseFile.Model.Cost.onInitialized)           {CaseFile.Model.Cost.onInitialized();}

        if (CaseFile.Service.onInitialized)              {CaseFile.Service.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/latest/plugin/search/CASE_FILE";
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/latest/plugin/casefile/byId/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/latest/plugin/casefile/";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
            //return parseInt(objSolr.object_id_s);
        }
        ,nodeType: function(objSolr) {
            return CaseFile.Model.DOC_TYPE_CASE_FILE;
        }
        ,nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable) + " (" + Acm.goodValue(objSolr.name) + ")";
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.author = objData.creator;
            solr.author_s = objData.creator;
            solr.create_tdt = objData.created;
            solr.last_modified_tdt = objData.modified;
            solr.modifier_s = objData.modifier;
            solr.name = objData.caseNumber;
            solr.object_id_s = objData.id;
            solr.object_type_s = CaseFile.Model.DOC_TYPE_CASE_FILE;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            return CaseFile.Model.Detail.validateCaseFile(data);
        }
        ,nodeTypeMap: function() {
            return CaseFile.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,onTopbarViewSetAsnData: function(asnData) {
        if (ObjNav.Model.Tree.Config.validateTreeInfo(asnData)) {
            if ("/plugin/casefile" == asnData.name) {
                var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                var sameResultSet = ObjNav.Model.Tree.Config.sameResultSet(asnData);
                ObjNav.Model.Tree.Config.readTreeInfo();

                if (!sameResultSet) {
                    ObjNav.Model.retrieveData(treeInfo);
                }
                return true;
            }
        }
        return false;
    }

    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"
    ,DOC_TYPE_FILE       : "FILE"
    ,DOC_CATEGORY_CORRESPONDENCE: "CORRESPONDENCE"

    ,getCaseFileId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getCaseFile: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, objId);
    }

    ,Tree: {
        create: function() {
            if (CaseFile.Model.Tree.Key.create)        {CaseFile.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (CaseFile.Model.Tree.Key.onInitialized)        {CaseFile.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_DETAILS      : "d"
            ,NODE_TYPE_PART_PEOPLE       : "p"
            ,NODE_TYPE_PART_DOCUMENTS    : "o"
            ,NODE_TYPE_PART_PARTICIPANTS : "a"
            ,NODE_TYPE_PART_NOTES        : "n"
            ,NODE_TYPE_PART_TASKS        : "t"
            ,NODE_TYPE_PART_REFERENCES   : "r"
            ,NODE_TYPE_PART_HISTORY      : "h"
            ,NODE_TYPE_PART_TEMPLATES    : "tm"
            ,NODE_TYPE_PART_TIME         : "time"
            ,NODE_TYPE_PART_COST         : "cost"

            ,nodeTypeMap: [
                {nodeType: "prevPage"    ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/CASE_FILE"        ,icon: "i i-folder"
                    ,tabIds: ["tabTitle"
                        ,"tabDetail","tabPeople"
                        ,"tabDocs","tabParticipants"
                        ,"tabNotes","tabTasks"
                        ,"tabRefs","tabHistory"
                        ,"tabTemplates"
                        ,"tabTime"
                        ,"tabCost"
                    ]
                }
                ,{nodeType: "p/CASE_FILE/d"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/CASE_FILE/p"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/CASE_FILE/o"      ,icon: "",tabIds: ["tabDocs"]}
                //,{nodeType: "p/CASE_FILE/o/c"     ,icon: "",tabIds: ["tabDoc"]}
                ,{nodeType: "p/CASE_FILE/a"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/CASE_FILE/n"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/CASE_FILE/t"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/CASE_FILE/r"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/CASE_FILE/h"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/CASE_FILE/tm"     ,icon: "",tabIds: ["tabTemplates"]}
                ,{nodeType: "p/CASE_FILE/time"   ,icon: "",tabIds: ["tabTime"]}
                ,{nodeType: "p/CASE_FILE/cost"   ,icon: "",tabIds: ["tabCost"]}
            ]
        }
    }

    ,Detail: {
        create : function() {
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_FILE           , this.onViewChangedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE          , this.onViewChangedCaseTitle);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_INCIDENT_DATE       , this.onViewChangedIncidentDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_ASSIGNEE            , this.onViewChangedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_SUBJECT_TYPE        , this.onViewChangedSubjectType);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_PRIORITY            , this.onViewChangedPriority);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DUE_DATE            , this.onViewChangedDueDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_RESTRICT_CHECKBOX   , this.onViewClickedRestrictCheckbox);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedCaseFile: function(caseFileId) {
            ObjNav.Service.retrieveObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            CaseFile.Service.Detail.saveCaseTitle(caseFileId, title);
        }

        ,onViewChangedIncidentDate: function(caseFileId, incidentDate) {
            CaseFile.Service.Detail.saveIncidentDate(caseFileId, incidentDate);
        }
        ,onViewChangedAssignee: function(caseFileId, assignee) {
            CaseFile.Service.Detail.saveAssignee(caseFileId, assignee);
        }
        ,onViewChangedSubjectType: function(caseFileId, caseType) {
            CaseFile.Service.Detail.saveSubjectType(caseFileId, caseType);
        }
        ,onViewChangedPriority: function(caseFileId, priority) {
            CaseFile.Service.Detail.savePriority(caseFileId, priority);
        }
        ,onViewChangedDueDate: function(caseFileId, dueDate) {
            CaseFile.Service.Detail.saveDueDate(caseFileId, dueDate);
        }
        ,onViewChangedDetail: function(caseFileId, details) {
            CaseFile.Service.Detail.saveDetail(caseFileId, details);
        }
        ,onViewClickedRestrictCheckbox: function(caseFileId, restriction) {
            CaseFile.Service.Detail.updateCaseRestriction(caseFileId, restriction);
        }


        ,getAssignee: function(caseFile) {
            var assignee = null;
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                if (Acm.isArray(caseFile.participants)) {
                    for (var i = 0; i < caseFile.participants.length; i++) {
                        var participant =  caseFile.participants[i];
                        if ("assignee" == participant.participantType) {
                            assignee = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return assignee;
        }
        ,setAssignee: function(caseFile, assignee) {
            if (caseFile) {
                if (!Acm.isArray(caseFile.participants)) {
                    caseFile.participants = [];
                }

                for (var i = 0; i < caseFile.participants.length; i++) {
                    if ("assignee" == caseFile.participants[i].participantType) {
                        caseFile.participants[i].participantLdapId = assignee;
                        return;
                    }
                }


                var participant = {};
                participant.participantType = "assignee";
                participant.participantLdapId = assignee;
                caseFile.participants.push(participant);
            }
        }
        ,getCacheCaseFile: function(caseFileId) {
            if (0 >= caseFileId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
        ,putCacheCaseFile: function(caseFileId, caseFile) {
            ObjNav.Model.Detail.putCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile);
        }
        ,validateCaseFile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Acm.isArray(data.childObjects)) {
                return false;
            }
            if (!Acm.isArray(data.milestones)) {
                return false;
            }
            if (!Acm.isArray(data.participants)) {
                return false;
            }
            if (!Acm.isArray(data.personAssociations)) {
                return false;
            }
            if (!Acm.isArray(data.references)) {
                return false;
            }
            return true;
        }
    }

    ,People: {
        create : function() {
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CHILD_OBJECT        , this.onViewChangedChildObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PARTICIPANT           , this.onViewAddedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PARTICIPANT         , this.onViewUpdatedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PARTICIPANT         , this.onViewDeletedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PERSON_ASSOCIATION    , this.onViewAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PERSON_ASSOCIATION  , this.onViewUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PERSON_ASSOCIATION  , this.onViewDeletedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_ADDRESS               , this.onViewAddedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_ADDRESS             , this.onViewUpdatedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_ADDRESS             , this.onViewDeletedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_CONTACT_METHOD        , this.onViewAddedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_CONTACT_METHOD      , this.onViewUpdatedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_CONTACT_METHOD      , this.onViewDeletedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_SECURITY_TAG          , this.onViewAddedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_SECURITY_TAG        , this.onViewUpdatedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_SECURITY_TAG        , this.onViewDeletedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PERSON_ALIAS          , this.onViewAddedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PERSON_ALIAS        , this.onViewUpdatedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PERSON_ALIAS        , this.onViewDeletedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_ORGANIZATION          , this.onViewAddedOrganization);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_ORGANIZATION        , this.onViewUpdatedOrganization);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_ORGANIZATION        , this.onViewDeletedOrganization);

        }
        ,onInitialized: function() {
        }

        ,onViewChangedChildObject: function(caseFileId, childObject) {
            CaseFile.Service.People.saveChildObject(caseFileId, childObject);
        }
        ,onViewAddedParticipant: function(caseFileId, participant) {
            CaseFile.Service.People.addParticipant(caseFileId, participant);
        }
        ,onViewUpdatedParticipant: function(caseFileId, participant) {
            CaseFile.Service.People.updateParticipant(caseFileId, participant);
        }
        ,onViewDeletedParticipant: function(caseFileId, participantId) {
            CaseFile.Service.People.deleteParticipant(caseFileId, participantId);
        }
        ,onViewAddedPersonAssociation: function(caseFileId, personAssociation) {
            var pa = CaseFile.Model.People.newPersonAssociation();
            pa.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;
            pa.parentId = caseFileId;
            pa.personType = personAssociation.personType;
            //pa.personDescription = personAssociation.personDescription;
            pa.person.title = personAssociation.person.title;
            pa.person.givenName = personAssociation.person.givenName;
            pa.person.familyName = personAssociation.person.familyName;
            CaseFile.Service.People.addPersonAssociation(caseFileId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(caseFileId, personAssociation) {
            CaseFile.Service.People.updatePersonAssociation(caseFileId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(caseFileId, personAssociationId) {
            CaseFile.Service.People.deletePersonAssociation(caseFileId, personAssociationId);
        }
        ,onViewAddedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.People.addAddress(caseFileId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.People.updateAddress(caseFileId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(caseFileId, personAssociationId, addressId) {
            CaseFile.Service.People.deleteAddress(caseFileId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.People.addContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.People.updateContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            CaseFile.Service.People.deleteContactMethod(caseFileId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.People.addSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.People.updateSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            CaseFile.Service.People.deleteSecurityTag(caseFileId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.People.addPersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.People.updatePersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            CaseFile.Service.People.deletePersonAlias(caseFileId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.People.addOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.People.updateOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(caseFileId, personAssociationId, organizationId) {
            CaseFile.Service.People.deleteOrganization(caseFileId, personAssociationId, organizationId);
        }

        ,newPersonAssociation: function() {
            return {
                id: null
                ,personType: ""
                ,parentId:null
                ,parentType:""
                ,personDescription: ""
                ,notes:""
                ,person:{
                    id: null
                    ,title: ""
                    ,givenName: ""
                    ,familyName: ""
                    ,company: ""
                    /*,hairColor:""
                     ,eyeColor:""
                     ,heightInInches:null*/
                    ,weightInPounds:null
                    /*,dateOfBirth:null
                     ,dateMarried:null*/
                    ,addresses: []
                    ,contactMethods: []
                    ,securityTags: []
                    ,personAliases: []
                    ,organizations: []
                }
            };
        }
        ,findPersonAssociation: function(personAssociationId, personAssociations) {
            var personAssociation = null;
            for (var i = 0; i < personAssociations.length; i++) {
                if (personAssociationId == personAssociations[i].id) {
                    personAssociation = personAssociations[i];
                    break;
                }
            }
            return personAssociation;
        }

        ,validatePersonAssociation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.person)) {
                return false;
            }
            if (!Acm.isArray(data.person.contactMethods)) {
                return false;
            }
            if (!Acm.isArray(data.person.addresses)) {
                return false;
            }
            if (!Acm.isArray(data.person.securityTags)) {
                return false;
            }
            if (!Acm.isArray(data.person.personAliases)) {
                return false;
            }
            if (!Acm.isArray(data.person.organizations)) {
                return false;
            }
            return true;
        }
    }

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo(4);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }


        ,onViewAddedNote: function(note) {
            CaseFile.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            CaseFile.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            CaseFile.Service.Notes.deleteNote(noteId);
        }

        ,validateNotes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,validateNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        }
    }

    ,Tasks: {
        create : function() {
            this.cacheTaskSolr = new Acm.Model.CacheFifo(4);
            this.cacheTasks = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
            CaseFile.Service.Tasks.retrieveTask();
        }
    }
    ,Documents: {
        create : function() {
            //this.cacheDocuments = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }
    ,Correspondence: {
        create : function() {
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_ADD_CORRESPONDENCE, this.onViewClickedAddCorrespondence);
        }
        ,onInitialized: function() {
        }


        ,onViewClickedAddCorrespondence: function(caseFileId, templateName) {
            //var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                CaseFile.Service.Correspondence.createCorrespondence(caseFile, templateName);
            }
        }
    }

    ,References: {
        create : function() {
            this.cacheReferenceList = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }

    ,Events: {
        create : function() {
            this.cacheEventList = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }


    ,Lookup: {
        create: function() {
            this._assignees    = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_ASSIGNEES);
            this._subjectTypes = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_TYPES);
            this._priorities   = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_PRIORITIES);
        }
        ,onInitialized: function() {
            var assignees = CaseFile.Model.Lookup.getAssignees();
            if (Acm.isEmpty(assignees)) {
                CaseFile.Service.Lookup.retrieveAssignees();
            } else {
                CaseFile.Controller.modelFoundAssignees(assignees);
            }

            var subjectTypes = CaseFile.Model.Lookup.getSubjectTypes();
            if (Acm.isEmpty(subjectTypes)) {
                CaseFile.Service.Lookup.retrieveSubjectTypes();
            } else {
                CaseFile.Controller.modelFoundSubjectTypes(subjectTypes);
            }

            var priorities = CaseFile.Model.Lookup.getPriorities();
            if (Acm.isEmpty(priorities)) {
                CaseFile.Service.Lookup.retrievePriorities();
            } else {
                CaseFile.Controller.modelFoundPriorities(priorities);
            }
        }
        
        ,PERSON_SUBTABLE_TITLE_CONTACT_METHODS:   "Communication Devices"
        ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS:     "Organizations"
        ,PERSON_SUBTABLE_TITLE_ADDRESSES:         "Locations"
        ,PERSON_SUBTABLE_TITLE_ALIASES:           "Aliases"
        ,PERSON_SUBTABLE_TITLE_SECURITY_TAGS:     "Security Tags"

        ,getAssignees: function() {
            return this._assignees.get();
        }
        ,setAssignees: function(assignees) {
            this._assignees.set(assignees);
        }
        ,getSubjectTypes: function() {
            return this._subjectTypes.get();
        }
        ,setSubjectTypes: function(subjectTypes) {
            this._subjectTypes.set(subjectTypes);
        }
        ,getPriorities: function() {
            return this._priorities.get();
        }
        ,setPriorities: function(priorities) {
            this._priorities.set(priorities);
        }


        //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
        ,_personTypes : ['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator']
        ,getPersonTypes : function() {
            return this._personTypes;
        }

        ,_personTitles : ['Mr','mr', 'Mrs','mrs', 'Ms','ms', 'Miss','miss']
        ,getPersonTitles : function() {
            return this._personTitles;
        }

        ,_contactMethodTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog']
        ,getContactMethodTypes : function() {
            return this._contactMethodTypes;
        }

        ,_securityTagTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog']
        ,getSecurityTagTypes : function() {
            return this._securityTagTypes;
        }

        ,_organizationTypes : ['Non-profit','Government','Corporation']
        ,getOrganizationTypes : function() {
            return this._organizationTypes;
        }

        ,_addressTypes : ['Business' , 'Home']
        ,getAddressTypes : function() {
            return this._addressTypes;
        }

        ,_aliasTypes : ['FKA' , 'Married']
        ,getAliasTypes : function() {
            return this._aliasTypes;
        }

        ,getCaseTypes: function() {
            return ["SSBI", "Type1", "Type2", "Type3", "Type4"];
            //return ["Type1", "Type2", "Type3", "Type4"];
        }
//    ,getCaseFileTypes: function() {
//        var data = sessionStorage.getItem("AcmCaseFileTypes");
//        var item = ("null" === data)? null : JSON.parse(data);
//        return item;
//    }
//    ,setCaseFileTypes: function(data) {
//        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
//        sessionStorage.setItem("AcmCaseFileTypes", item);
//    }

        ,getCloseDispositions: function() {
            return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
            //return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
        }
    }

    ,Time: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Cost: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

};
