/**
 *
 */
package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.group.GroupConstants;
import org.mule.api.MuleException;
import org.mule.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author riste.tutureski
 */
public class GroupServiceImpl implements GroupService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private UserDao userDao;
    private AcmGroupDao groupDao;
    private ExecuteSolrQuery executeSolrQuery;

    private Pattern pattern = Pattern.compile(GroupConstants.UUID_REGEX_STRING);

    @Override
    public AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members)
    {
        if (members != null)
        {
            for (AcmUser member : members)
            {
                group.addMember(member);
            }
        }

        return group;
    }

    @Override
    public Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members)
    {
        Set<AcmUser> updatedMembers = new HashSet<>();

        if (members != null)
        {
            for (AcmUser member : members)
            {
                AcmUser updatedMember = getUserDao().findByUserId(member.getUserId());

                if (updatedMember != null)
                {
                    updatedMembers.add(updatedMember);
                }
            }
        }

        return updatedMembers;
    }

    @Override
    public String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException
    {

        logger.info("Taking all groups and subgroups from Solr. Authenticated user is {}", usernamePasswordAuthenticationToken.getName());

        String query = "object_type_s:GROUP AND object_sub_type_s:LDAP_GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        String queryResults = getExecuteSolrQuery().getResultsByPredefinedQuery(usernamePasswordAuthenticationToken,
                SolrCore.ADVANCED_SEARCH, query, 0, 1000, "name asc");

        return queryResults;

    }


    @Override
    public AcmGroup checkAndSaveAdHocGroup(AcmGroup group)
    {
        boolean isGroupNameTaken = isGroupUINameTakenOnASameTreeLevel(group);

        if (!isGroupNameTaken)
        {
            group.setName(group.getName() + "-UUID-" + UUID.getUUID());
            return getGroupDao().save(group);
        } else
        {
            return null;
        }
    }

    private boolean isGroupUINameTakenOnASameTreeLevel(AcmGroup group)
    {
        AcmGroup g = group.getParentGroup() != null ? groupDao.subGroupByUIName(group) : groupDao.groupByUIName(group);
        return g != null && isUUIDPresentInTheGroupName(g.getName()) ? true : false;
    }

    @Override
    public boolean isUUIDPresentInTheGroupName(String str)
    {
        return pattern.matcher(str).matches();
    }

    @Override
    @Transactional
    public void createOrUpdateAdHocGroup(String genericGroupName, String clientId, String clientName, AcmGroup acmGroup)
    {
        if (acmGroup == null)
        {
            acmGroup = new AcmGroup();
            acmGroup.setName(genericGroupName + " " + clientId + " " + clientName);
            acmGroup.setStatus(AcmGroupStatus.ACTIVE);
            acmGroup.setType(AcmGroupType.ADHOC_GROUP);
            checkAndSaveAdHocGroup(acmGroup);
        } else
        {
            AcmGroup newGroup = new AcmGroup();

            // create new ad-hoc group and copy the properties from the original found group.
            newGroup.setSupervisor(acmGroup.getSupervisor());
            newGroup.setType(acmGroup.getType());
            newGroup.setStatus(acmGroup.getStatus());
            newGroup.setDescription(acmGroup.getDescription());
            newGroup.setChildGroups(acmGroup.getChildGroups());
            newGroup.setCreator(acmGroup.getCreator());
            newGroup.setMembers(acmGroup.getMembers());
            newGroup.setParentGroup(acmGroup.getParentGroup());

            // Get the UUID from the original group and concatenate it with the new name of the group.
            String[] split = acmGroup.getName().split("-UUID-");
            String uuid = split[1];
            newGroup.setName(genericGroupName + " " + clientId + " " + clientName + "-UUID-" + uuid);

            AcmGroup saved = getGroupDao().save(newGroup);

            if (saved != null)
            {
                // after saving the group, remove the members and delete the original group
                getGroupDao().removeMembersFromGroup(acmGroup.getName(), acmGroup.getMembers());
                getGroupDao().deleteAcmGroupByName(acmGroup.getName());
            }
        }
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
