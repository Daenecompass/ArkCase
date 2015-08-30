package com.armedia.acm.files.capture;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

@RunWith(EasyMockRunner.class)
public class CaptureFileWatcherTest extends EasyMockSupport
{
    @Mock
    private FileObject mockFileObject;

    @Mock
    private ApplicationEventPublisher mockPublisher;

    @Mock
    private FileChangeEvent mockFileChangeEvent;

    @Mock
    private FileName mockFileName;

    private CaptureFileWatcher unit;
    private String fileSeparator = File.separator;

    private Logger log = LoggerFactory.getLogger(getClass());
    
    // for this test to pass, Windows and Linux require different file URL prefixes
    private final String fileUrlPrefix = "file:" + ( File.separator.equals("/") ? "" : "/" );
    
    // do not put a period before the extension
    private final String allowedFileExtensions = "pdf,xml";

    @Before
    public void setUp() throws Exception
    {
        unit = new CaptureFileWatcher();
        unit.setBaseFolderPath("C:" + fileSeparator + "temp");
        unit.setFileExtensions(allowedFileExtensions);
    }
    
    @Test
    public void baseFolderPath_shouldBeSet_afterSourceFolderIsSet() throws Exception
    {
        expect(mockFileObject.getURL()).andReturn(new URL("file:///C:" + fileSeparator + "temp"));

        replayAll();

        unit.setBaseFolder(mockFileObject);

        verifyAll();


        String expected = "C:" + fileSeparator + "temp";
        // cross platform canonical path names...
        if ( "/".equals(fileSeparator) )
        {
            expected = "/" + expected;
        }

        assertEquals(expected, unit.getBaseFolderPath());
    }
    
    @Test
    public void fileExtensionsList_shouldBeSet_afterFileExtensionsIsSet() throws Exception
    {
        String fileExtensions = "pdf,txt,html";

        unit.setFileExtensions(fileExtensions);
       
        List<String> expected = new ArrayList<String>();
        expected.add("pdf");
        expected.add("txt");
        expected.add("html");
        
        assertEquals(expected, unit.getFileExtensionsList());
    }

    @Test
    public void raiseEvent_whenFileIsAdded_allowed() throws Exception
    {
        
        Capture<AbstractCaptureFileEvent> capturedEvent =
                setupEventTest(fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator + "file.xml", "xml");

        unit.fileCreated(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(CaptureFileAddedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsAdded_notallowed() throws Exception
    {
        Capture<AbstractCaptureFileEvent> capturedEvent =
                setupEventTest(fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator + "file.png", "png");

        unit.fileCreated(mockFileChangeEvent);

        // no event should be captured
    }
    
    private void verifyEventTestResults(Capture<AbstractCaptureFileEvent> capturedEvent)
    {
        verifyAll();

        assertEquals("file.xml", capturedEvent.getValue().getCaptureFile().getName());
        assertNotNull(capturedEvent.getValue().getCaptureFile());
    }

    private Capture<AbstractCaptureFileEvent> setupEventTest(String fileUrl, String extension) throws FileSystemException, MalformedURLException
    {
        unit.setApplicationEventPublisher(mockPublisher);


        Capture<AbstractCaptureFileEvent> capturedEvent = new Capture<>();

        expect(mockFileChangeEvent.getFile()).andReturn(mockFileObject).atLeastOnce();
        expect(mockFileObject.getName()).andReturn(mockFileName).anyTimes();
        expect(mockFileName.getExtension()).andReturn(extension);
        
        URL fileUrlObj = new URL(fileUrl);
        expect(mockFileObject.getURL()).andReturn(fileUrlObj).times(1, 2);

        log.debug("File URL: " + fileUrl);

        mockPublisher.publishEvent(capture(capturedEvent));

        replayAll();
        return capturedEvent;
    }
}
