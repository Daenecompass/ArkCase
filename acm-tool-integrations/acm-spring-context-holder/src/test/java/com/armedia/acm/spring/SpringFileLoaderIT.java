package com.armedia.acm.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-context-holder.xml",
        "classpath*:/spring/spring-library-folder-watcher.xml",
        "/spring/spring-config-context-holder-depends-on-beans.xml"})
public class SpringFileLoaderIT
{
    @Autowired
    private SpringContextHolder springContextHolder;


    @Test
    public void loadSpringFile() throws Exception
    {

        // let's copy a spring file to the incoming folder and see if it gets loaded
        Resource springFile = new ClassPathResource("spring/spring-config-context-holder-test-context.xml");
        assertTrue(springFile.exists());

        String userHome = System.getProperty("user.home");
        String springFolderName = userHome + File.separator + ".acm" + File.separator + "spring";
        String springFileName = springFolderName + File.separator + springFile.getFilename();

        File springFolder = new File(springFolderName);
        springFolder.mkdirs();

        File target = new File(springFileName);

        FileCopyUtils.copy(springFile.getFile(), target);

        // wait for the folder watcher to process the file
        Thread.sleep(4000);

        // see whether our context holder now has a string bean, like it should have, from the spring context
        // we just copied to the load spring folder
        Map<String, String> strings = springContextHolder.getAllBeansOfType(String.class);
        assertEquals(1, strings.size());

        // wait some more to be sure the file watcher has a chance to notice the addition.
        Thread.sleep(3500);

        // now let's remove our spring file and see whether the corresponding child context is unloaded
        File processedSpringFile = new File(springFileName);
        processedSpringFile.delete();

        // wait some more to be sure the file watcher has a chance to notice the deletion.
        Thread.sleep(5000);

        strings = springContextHolder.getAllBeansOfType(String.class);
        assertTrue(strings.isEmpty());

    }
}