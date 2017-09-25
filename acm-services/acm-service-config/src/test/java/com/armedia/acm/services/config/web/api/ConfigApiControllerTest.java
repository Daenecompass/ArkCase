package com.armedia.acm.services.config.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.config.service.ConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-config-plugin-test.xml" })
public class ConfigApiControllerTest extends EasyMockSupport
{
    private ConfigService mockConfigService;
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ConfigApiController unit;

    @Before
    public void setUp() throws Exception
    {
        mockConfigService = createMock(ConfigService.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        unit = new ConfigApiController();
        unit.setConfigService(mockConfigService);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getConfig() throws Exception
    {
        // given
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfigService.getConfigAsJson("config")).andReturn("{\"some1\":\"value1\"}");
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        // when
        replayAll();

        MvcResult result = mockMvc.perform(get("/api/v1/service/config/{name}", "config")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).session(mockHttpSession).principal(mockAuthentication))
                .andReturn();

        // then
        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertNotNull(returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some1");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value1");
    }

    @Test
    public void getNotExistingConfig() throws Exception
    {
        // given
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfigService.getConfigAsJson("config_no_such")).andReturn("{}");

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        // when
        replayAll();

        MvcResult result = mockMvc.perform(get("/api/v1/service/config/{name}", "config_no_such")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).session(mockHttpSession).principal(mockAuthentication))
                .andReturn();

        // then
        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertEquals(returned, "{}");
    }

    @Test
    public void getInfo() throws Exception
    {
        // given
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        List<Map<String, String>> info = new ArrayList<>();
        Map<String, String> appConfigName = new HashMap<>();
        appConfigName.put("name", "appConfigName");
        appConfigName.put("description", "appConfigDescription");
        info.add(appConfigName);
        Map<String, String> propertyConfigName = new HashMap<>();
        propertyConfigName.put("name", "propertyConfigName");
        propertyConfigName.put("description", "propertyConfigDescription");
        info.add(propertyConfigName);
        Map<String, String> jsonConfigName = new HashMap<>();
        jsonConfigName.put("name", "jsonConfigName");
        jsonConfigName.put("description", "jsonConfigDescription");
        info.add(jsonConfigName);

        expect(mockConfigService.getInfo()).andReturn(info);

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        // when
        replayAll();

        MvcResult result = mockMvc.perform(get("/api/v1/service/config").accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .session(mockHttpSession).principal(mockAuthentication)).andReturn();

        // then
        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertNotNull(returned);

        ObjectMapper om = new ObjectMapper();

        List<Map<String, String>> resultList = om.readValue(returned, new TypeReference<List<Map<String, String>>>()
        {
        });

        assertNotNull(resultList);
        assertEquals("appConfigName", resultList.get(0).get("name"));
        assertEquals("appConfigDescription", resultList.get(0).get("description"));
        assertEquals("propertyConfigName", resultList.get(1).get("name"));
        assertEquals("propertyConfigDescription", resultList.get(1).get("description"));
        assertEquals("jsonConfigName", resultList.get(2).get("name"));
        assertEquals("jsonConfigDescription", resultList.get(2).get("description"));
    }
}
