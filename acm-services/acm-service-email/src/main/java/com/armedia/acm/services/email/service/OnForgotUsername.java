package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.auth.web.ForgotUsernameEvent;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OnForgotUsername implements ApplicationListener<ForgotUsernameEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmEmailSenderService emailSenderService;
    private AcmApplication acmAppConfiguration;
    private String forgotUsernameEmailSubject;
    /**
     * Formatting string to be used for producing text to inserted as a body in the forgot username email. The
     * formatting
     * string accepts the login link string twice.
     * e.g: "Proceed to login <a href='%s'>%s</a>"
     */
    private String forgotUsernameEmailBodyTemplate;
    private EmailBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBuilder = (emailUserData, messageProps) -> {
        messageProps.put("to", emailUserData.getKey());
        messageProps.put("subject", forgotUsernameEmailSubject);
    };
    private EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBodyBuilder = emailUserData -> {

        int userAccountsNum = emailUserData.getValue().size();
        String userAccounts = toUserAccountsString(emailUserData.getValue());

        return String.format(forgotUsernameEmailBodyTemplate, userAccountsNum, userAccounts,
                acmAppConfiguration.getBaseUrl(), acmAppConfiguration.getBaseUrl());
    };

    @Override
    public void onApplicationEvent(ForgotUsernameEvent forgotUsernameEvent)
    {
        if (forgotUsernameEvent.isSucceeded())
        {
            AbstractMap.SimpleImmutableEntry<String, List<String>> emailUserData = (AbstractMap.SimpleImmutableEntry<String, List<String>>) forgotUsernameEvent
                    .getSource();
            sendUsernameEmail(emailUserData);
        }
    }

    private void sendUsernameEmail(AbstractMap.SimpleImmutableEntry<String, List<String>> emailUserData)
    {
        String userEmailAddress = emailUserData.getKey();
        String userAccounts = toUserAccountsString(emailUserData.getValue());
        try
        {
            log.debug("Sending email on address: [{}] with found user accounts: [{}]", userEmailAddress, userAccounts);
            emailSenderService.sendPlainEmail(Stream.of(emailUserData), emailBuilder, emailBodyBuilder);
        }
        catch (Exception e)
        {
            log.error("Email with username was not sent to address: [{}]", userEmailAddress);
        }
    }

    private String toUserAccountsString(List<String> accounts)
    {
        return accounts.stream()
                .collect(Collectors.joining(","));
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void setForgotUsernameEmailSubject(String forgotUsernameEmailSubject)
    {
        this.forgotUsernameEmailSubject = forgotUsernameEmailSubject;
    }

    public void setForgotUsernameEmailBodyTemplate(String forgotUsernameEmailBodyTemplate)
    {
        this.forgotUsernameEmailBodyTemplate = forgotUsernameEmailBodyTemplate;
    }
}
