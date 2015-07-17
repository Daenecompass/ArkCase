package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by sergey.kolomiets  on 7/8/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class RolesPrivilegesRetrieveRolesByPrivilege  implements RolePrivilegesConstants {
    private Logger log = LoggerFactory.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/privileges/{privilegeName}/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public List<String> retrieveRoles(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName) throws IOException, AcmRolesPrivilegesException{

        try {
            return rolesPrivilegesService.retrieveRolesByPrivilege(privilegeName);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve roles", e);
            }
            throw new AcmRolesPrivilegesException("Can't retrieve roles", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService) {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}