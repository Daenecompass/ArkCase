package com.armedia.acm.auth;

import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

public abstract class AcmAbstractAuthenticationEvent extends AcmEvent
{
    private final Authentication authentication;

    public AcmAbstractAuthenticationEvent(Authentication authentication)
    {
        super(authentication);
        this.authentication = authentication;

        setEventDate(new Date());

        if (authentication == null)
        {
            return;
        }

        setUserId(authentication.getName());

        if (authentication.getDetails() == null || !AcmAuthenticationDetails.class.isAssignableFrom(authentication.getDetails().getClass()))
        {
            return;
        }

        AcmAuthenticationDetails details = (AcmAuthenticationDetails) authentication.getDetails();
        setIpAddress(details.getRemoteAddress());

    }

    public Authentication getAuthentication()
    {
        return authentication;
    }
}
