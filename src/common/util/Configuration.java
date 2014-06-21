package common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration
{
    private static Properties prop = new Properties();

    private volatile static Configuration instance = null;

    private Configuration()
        throws IOException
    {
        InputStream in =
            new BufferedInputStream(new FileInputStream(
                Constant.CONFIGURATION_PATH));
        prop.load(in);
        in.close();
    }

    public static Configuration getInstance() throws IOException
    {
        if (null == instance)
        {
            synchronized (Configuration.class)
            {
                if (null == instance)
                    instance = new Configuration();
            }
        }

        return instance;
    }

    private String getProperty(String key)
    {
        return prop.getProperty(key);
    }

    public Long getLong(String key)
    {
        if (prop.contains(key))
            return Long.valueOf(getProperty(key));
        else
            return Constant.CONF_DEFAULT_LONG;
    }

    public Integer getInteger(String key)
    {
        if (prop.contains(key))
            return Integer.valueOf(getProperty(key));
        else
            return Constant.CONF_DEFAULT_INTEGER;
    }

    public String getString(String key)
    {
        if (prop.contains(key))
            return getProperty(key);
        else
            return Constant.CONF_DEFAULT_STRING;
    }
}
