package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class NotificationFormatterTest extends EasyMockSupport
{
    private Properties mockProperties;
    private Notification notification;
    private NotificationFormatter notificationFormatter;

    @Before
    public void setUp()
    {
        mockProperties = createMock(Properties.class);
        notification = new Notification();
        notificationFormatter = new NotificationFormatter();
        notificationFormatter.setNotificationProperties(mockProperties);
    }

    /**
     * Test when notification title is not null and contains OBJECT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceObjectTypeLabelInTitle()
    {
        String parentType = "CASE_FILE";
        String keyLabel = parentType + ".label";
        String expectedTitle = "Title with label placeholder: object_label";
        notification.setTitle("Title with label placeholder: " + NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER);
        notification.setParentType(parentType);
        notification.setNote(null);

        expect(mockProperties.getProperty(keyLabel)).andReturn("object_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getTitle(), expectedTitle);
    }

    /**
     * Test when notification note is not null and the note contains OBJECT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceObjectTypeLabelInNote()
    {
        String parentType = "CASE_FILE";
        String keyLabel = parentType + ".label";
        String expectedNote = "Note with label placeholder: object_label";
        notification.setTitle(null);
        notification.setNote("Note with label placeholder: " + NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER);
        notification.setParentType(parentType);

        expect(mockProperties.getProperty(keyLabel)).andReturn("object_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getNote(), expectedNote);
    }

    /**
     * Test when notification title is not null and the contains PARENT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceParentTypeLabelInTitle()
    {
        String relatedType = "CASE_FILE";
        String keyLabel = relatedType + ".label";
        String expectedTitle = "Title with label placeholder: parent_label";
        // set note null to ensure we test the right method
        notification.setNote(null);
        notification.setTitle("Title with label placeholder: " + NotificationConstants.PARENT_TYPE_LABEL_PLACEHOLDER);
        notification.setRelatedObjectType(relatedType);

        expect(mockProperties.getProperty(keyLabel)).andReturn("parent_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getTitle(), expectedTitle);
    }

    /**
     * Test when notification note is not null and the note contains PARENT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceParentTypeLabelInNote()
    {
        String relatedType = "CASE_FILE";
        String keyLabel = relatedType + ".label";
        String expectedNote = "Note with label placeholder: parent_label";
        notification.setNote("Note with label placeholder: " + NotificationConstants.PARENT_TYPE_LABEL_PLACEHOLDER);
        notification.setRelatedObjectType(relatedType);
        // set title null to ensure we test the right method
        notification.setTitle(null);
        expect(mockProperties.getProperty(keyLabel)).andReturn("parent_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getNote(), expectedNote);
    }

}
