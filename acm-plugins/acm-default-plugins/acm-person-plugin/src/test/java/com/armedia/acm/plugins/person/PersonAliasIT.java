package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.PersonAliasDao;
import com.armedia.acm.plugins.person.model.PersonAlias;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
                                   "/spring/spring-library-person.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PersonAliasIT
{
    @Autowired
    private PersonAliasDao personAliasDao;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void savePersonAlias() throws Exception
    {
        PersonAlias pa = new PersonAlias();
        
        pa.setAliasTypeId("Others");
        pa.setAliasValue("ACM");
        pa.setModifier("testModifier");
        pa.setCreator("testCreator");
        pa.setCreated(new Date());
        pa.setModified(new Date());
       
      

        PersonAlias saved = personAliasDao.save(pa);

        assertNotNull(saved.getId());

        log.info("Person ID: " + saved.getId());
    }
}

