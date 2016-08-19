package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/service/notification/email", "/api/latest/service/notification/email" })
public class SendEmailWithAttachmentsAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private NotificationSender notificationSender;

    @RequestMapping(value = "/withattachments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsDTO createEmailWithAttachments(@RequestBody EmailWithAttachmentsDTO in, Authentication authentication,
            HttpSession session) throws AcmNotificationException, AcmUserActionFailedException
    {

        if (null == in)
        {
            throw new AcmNotificationException("Could not create email message, invalid input : " + in);
        }
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            getNotificationSender().sendEmailWithAttachments(in, authentication, user);
        } catch (Exception e)
        {
            throw new AcmUserActionFailedException(
                    "Could not send emails with attachment,among other things check your request body. Exception message is : ", null, null,
                    e.getMessage(), e);
        }

        return in;
    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

}