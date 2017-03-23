package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
public class CmisConfigurationService
{
    private Logger log = LoggerFactory.getLogger(CmisConfigurationService.class);

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private String cmisConfigurationLocation;
    private String cmisFile;
    private String cmisPropertiesFile;

    private String cmisConfigurationTemplatesLocation;
    private String cmisTemplateFile;
    private String cmisTemplatePropertiesFile;

    private String cmisPropertiesFileRegex;

    /**
     * Create CMIS Config config files
     *
     * @param cmisId Config identifier
     * @param props  Config properties data
     * @throws AcmCmisConfigurationException
     */
    public void createCmisConfig(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        Matcher matcher = pattern.matcher(cmisId);
        if (!matcher.find())
        {
            throw new AcmCmisConfigurationException("ID has wrong format. Only numbers, characters symbols and '.' are allowed");
        }

        // Check if CMIS files exist
        if (propertiesFileExist(cmisId) || cmisFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("CMIS config with ID='%s' exists", cmisId));
        }

        try
        {
            createPropertiesFile(cmisId, props);
            createCmisFile(cmisId, props);
        } catch (Exception e)
        {
            // Delete created files quietly
            deletePropertiesFileQuietly(cmisId);
            deleteCmisFileQuietly(cmisId);

            log.error("Can't create CMIS config '{}' ", cmisId, e);
            throw new AcmCmisConfigurationException(String.format("Can't create CMIS config with ID='%s'", cmisId), e);
        }
    }

    /**
     * Update CMIS Config settings
     *
     * @param cmisId Config identifier
     * @param props  Config properties data
     */
    public void updateCmisConfig(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(cmisId);

        if (!propertiesFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
        }

        writePropertiesFile(cmisId, props);
    }

    /**
     * Delete CMIS Config
     *
     * @param cmisId Config identifier
     * @throws AcmCmisConfigurationException
     */
    public void deleteCmisConfig(String cmisId) throws AcmCmisConfigurationException
    {

        String[] extensions = new String[]{"properties"};
        List<File> propertiesFiles = (List<File>) FileUtils.listFiles(new File(cmisConfigurationLocation), extensions, false);

        Pattern pattern = Pattern.compile(cmisPropertiesFileRegex);
        int matchedFiles = 0;
        for (File fileIter : propertiesFiles)
        {
            String fileName = fileIter.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find())
            {
                matchedFiles++;
            }
        }

        if (matchedFiles == 0)
        {
            throw new AcmCmisConfigurationException("There are no CMIS properties files");
        }

        if (matchedFiles == 1)
        {
            throw new AcmCmisConfigurationException("Can't delete last CMIS file");
        }

        // Delete CMIS config files. If something goes wrong then go ahead add information to the log only

        forceDeleteFileQuietly(getPropertiesFileName(cmisId));
        forceDeleteFileQuietly(getCmisFileName(cmisId));
    }

    /**
     * Create Properties file
     *
     * @param cmisId Config identifier
     * @param props  Config properties data
     * @throws AcmCmisConfigurationException
     * @throws IOException
     */
    private void createPropertiesFile(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException, IOException
    {
        if (propertiesFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("Properties file with ID='%s' is present in the system.", cmisId));
        }

        writePropertiesFile(cmisId, props);
    }

    /**
     * Write data into the properties file
     *
     * @param cmisId
     * @param props
     * @throws IOException
     * @throws AcmCmisConfigurationException
     */
    private void writePropertiesFile(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(cmisId);

        try
        {
            // Create Properties file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(cmisConfigurationTemplatesLocation));

            Template tmplProperties = cfg.getTemplate(cmisTemplatePropertiesFile);

            Writer writerProp = null;
            try
            {
                writerProp = new FileWriter(new File(propertiesFileName));
                tmplProperties.process(props, writerProp);
            } finally
            {
                if (writerProp != null)
                {
                    writerProp.close();
                }
            }

        } catch (Exception e)
        {
            log.error("Can't write CMIS properties file with ID '{}' ", cmisId, e);
            throw new AcmCmisConfigurationException("Can't write CMIS properties file ", e);
        }
    }

    /**
     * Create CMIS file
     *
     * @param cmisId Config identifier
     * @param props  Config properties data
     * @throws IOException
     * @throws AcmCmisConfigurationException
     */
    private void createCmisFile(String cmisId, Map<String, Object> props) throws IOException, AcmCmisConfigurationException
    {
        String cmisFileName = getCmisFileName(cmisId);
        if (cmisFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("CMIS file '%s' is present in the system.", cmisFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(cmisConfigurationTemplatesLocation));

        try
        {
            // CMIS file
            Template tmplSig = cfg.getTemplate(cmisTemplateFile);
            Writer writerSig = null;
            try
            {
                writerSig = new FileWriter(new File(cmisFileName));
                tmplSig.process(props, writerSig);
            } finally
            {
                if (writerSig != null)
                {
                    writerSig.close();
                }
            }
        } catch (Exception e)
        {
            log.error("Can't create CMIS file with ID '{}' ", cmisId, e);
            throw new AcmCmisConfigurationException("Can't create CMIS file ", e);
        }
    }

    /**
     * Convert CMIS JSON Object to properties map
     *
     * @param jsonObj
     * @return
     * @throws AcmEncryptionException
     * @throws JSONException
     */
    public HashMap<String, Object> getProperties(JSONObject jsonObj) throws JSONException, AcmEncryptionException
    {
        HashMap<String, Object> props = new HashMap<>();
        props.put("id", jsonObj.getString(CmisConfigurationProperties.CMIS_ID));
        props.put("baseUrl", jsonObj.getString(CmisConfigurationProperties.CMIS_BASEURL));
        props.put("username", jsonObj.getString(CmisConfigurationProperties.CMIS_USERNAME));
        props.put("password", encryptablePropertyUtils.encryptPropertyValue(jsonObj.getString(CmisConfigurationProperties.CMIS_PASSWORD)));
        props.put("useAlfrescoExtension", jsonObj.getString(CmisConfigurationProperties.CMIS_USEALFRESCOEXTENSION));
        props.put("endpoint", jsonObj.getString(CmisConfigurationProperties.CMIS_ENDPOINT));
        props.put("maxIdle", jsonObj.getString(CmisConfigurationProperties.CMIS_MAXIDLE));
        props.put("maxActive", jsonObj.getString(CmisConfigurationProperties.CMIS_MAXACTIVE));
        props.put("maxWait", jsonObj.getString(CmisConfigurationProperties.CMIS_MAXWAIT));
        props.put("minEvictionMillis", jsonObj.getString(CmisConfigurationProperties.CMIS_MINEVICTIONMILLIS));
        props.put("evictionCheckIntervalMillis", jsonObj.getString(CmisConfigurationProperties.CMIS_EVICTIONCHECKINTERVALMILLIS));
        props.put("reconnectCount", jsonObj.getString(CmisConfigurationProperties.CMIS_RECONNECTCOUNT));
        props.put("reconnectFrequency", jsonObj.getString(CmisConfigurationProperties.CMIS_RECONNECTFREQUENCY));
        props.put("repositoryId", jsonObj.has(CmisConfigurationProperties.CMIS_REPOSITORYID)
                ? jsonObj.getString(CmisConfigurationProperties.CMIS_REPOSITORYID) : "");
        props.put("versioningState", jsonObj.getString(CmisConfigurationProperties.CMIS_VERSIONINGSTATE));

        return props;
    }

    public List<File> getPropertiesFiles()
    {
        // Get All properties files
        String[] extensions = new String[]{"properties"};
        List<File> files = (List<File>) FileUtils.listFiles(new File(cmisConfigurationLocation), extensions, false);
        List<File> propertiesFiles = new ArrayList<>();

        // Get all properties files that match to cmisPropertiesFileRegex
        Pattern pattern = Pattern.compile(cmisPropertiesFileRegex);

        for (File fileIter : files)
        {
            String fileName = fileIter.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find())
            {
                propertiesFiles.add(fileIter);
            }
        }

        return propertiesFiles;
    }

    private void forceDeleteFileQuietly(String fileName)
    {
        try
        {
            FileUtils.forceDelete(new File(fileName));
        } catch (IOException e)
        {
            log.error("Can't delete file {} ", fileName, e);
        }
    }

    private void deletePropertiesFileQuietly(String cmisId)
    {
        FileUtils.deleteQuietly(new File(getPropertiesFileName(cmisId)));
    }

    private void deleteCmisFileQuietly(String cmisId)
    {
        FileUtils.deleteQuietly(new File(getCmisFileName(cmisId)));
    }

    public String getPropertiesFileName(String cmisId)
    {
        return cmisConfigurationLocation + String.format(cmisPropertiesFile, cmisId);
    }

    public String getCmisFileName(String cmisId)
    {
        return cmisConfigurationLocation + String.format(cmisFile, cmisId);
    }

    public boolean propertiesFileExist(String cmisId)
    {
        String fileName = getPropertiesFileName(cmisId);
        return new File(fileName).exists();
    }

    public boolean cmisFileExist(String cmisId)
    {
        String fileName = getCmisFileName(cmisId);
        return new File(fileName).exists();
    }

    public void setCmisConfigurationLocation(String cmisConfigurationLocation)
    {
        this.cmisConfigurationLocation = cmisConfigurationLocation;
    }

    public void setCmisFile(String cmisFile)
    {
        this.cmisFile = cmisFile;
    }

    public void setCmisPropertiesFile(String cmisPropertiesFile)
    {
        this.cmisPropertiesFile = cmisPropertiesFile;
    }

    public void setCmisConfigurationTemplatesLocation(String cmisConfigurationTemplatesLocation)
    {
        this.cmisConfigurationTemplatesLocation = cmisConfigurationTemplatesLocation;
    }

    public void setCmisTemplateFile(String cmisTemplateFile)
    {
        this.cmisTemplateFile = cmisTemplateFile;
    }

    public void setCmisTemplatePropertiesFile(String cmisTemplatePropertiesFile)
    {
        this.cmisTemplatePropertiesFile = cmisTemplatePropertiesFile;
    }

    public void setCmisPropertiesFileRegex(String cmisPropertiesFileRegex)
    {
        this.cmisPropertiesFileRegex = cmisPropertiesFileRegex;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}
