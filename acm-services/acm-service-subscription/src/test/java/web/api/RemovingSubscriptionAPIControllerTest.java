package web.api;

/*-
 * #%L
 * ACM Service: Subscription
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
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;
import com.armedia.acm.services.subscription.service.SubscriptionService;
import com.armedia.acm.services.subscription.web.api.RemovingSubscriptionAPIController;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 12.02.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-subscription-web-api-test.xml"
})
public class RemovingSubscriptionAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private RemovingSubscriptionAPIController mockRemovingSubscriptionAPIController;
    private SubscriptionService mockSubscriptionService;
    private AcmPlugin mockSubscriptionPlugin;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        mockRemovingSubscriptionAPIController = new RemovingSubscriptionAPIController();

        mockSubscriptionService = createMock(SubscriptionService.class);
        mockSubscriptionPlugin = createMock(AcmPlugin.class);
        mockSubscriptionPlugin = createMock(AcmPlugin.class);

        mockHttpSession = new MockHttpSession();

        mockRemovingSubscriptionAPIController.setSubscriptionService(mockSubscriptionService);
        mockRemovingSubscriptionAPIController.setSubscriptionPlugin(mockSubscriptionPlugin);

        mockMvc = MockMvcBuilders.standaloneSetup(mockRemovingSubscriptionAPIController).setHandlerExceptionResolvers(exceptionResolver)
                .build();
        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void removeSubscription() throws Exception
    {

        String objectType = "NEW_OBJECT_TYPE";
        Long objectId = 100L;
        String userId = "user-acm";

        Map<String, Object> prop = new HashMap<>();
        prop.put(SubscriptionConstants.SUCCESS_MSG, "SUCCESS");

        expect(mockSubscriptionPlugin.getPluginProperties()).andReturn(prop).once();
        expect(mockSubscriptionService.deleteSubscriptionForGivenObject(userId, objectId, objectType)).andReturn(1);
        mockSubscriptionService.deleteSubscriptionEventsForGivenObject(userId, objectId, objectType);
        expectLastCall();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(userId);

        replayAll();
        MvcResult result = mockMvc.perform(
                delete("/api/latest/service/subscription/{userId}/{objType}/{objId}", userId, objectType, objectId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    public MockMvc getMockMvc()
    {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    public Authentication getMockAuthentication()
    {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication)
    {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver()
    {
        return exceptionResolver;
    }

    public void setExceptionResolver(ExceptionHandlerExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }
}
