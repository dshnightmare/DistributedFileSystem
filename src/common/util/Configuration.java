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

    public static final String CONFIGURATION_PATH = "conf.properties";

    public static final String CONF_DEFAULT_STRING = "NOT FOUND";

    public static final long CONF_DEFAULT_LONG = -1;

    public static final int CONF_DEFAULT_INTEGER = -1;

    public static final String HEARTBEAT_INTERVAL_KEY = "heartbeat_interval";

    public static final String LEASE_PERIOD_KEY = "lease_period";

    public static final String TASK_CHECK_INTERVAL_KEY = "task_check_interval";

    public static final String DUPLICATE_KEY = "duplicate_number";

    private Configuration()
        throws IOException
    {
        InputStream in =
            new BufferedInputStream(new FileInputStream(
                Configuration.CONFIGURATION_PATH));
        prop.load(in);
        in.close();
    }

    public static Configuration getInstance()
    {
        if (null == instance)
        {
            synchronized (Configuration.class)
            {
                if (null == instance)
                {
                    try
                    {
                        instance = new Configuration();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        instance = null;
                    }
                }
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
        if (prop.containsKey(key))
            return Long.valueOf(getProperty(key));
        else
            return Configuration.CONF_DEFAULT_LONG;
    }

    public Integer getInteger(String key)
    {
        if (prop.containsKey(key))
            return Integer.valueOf(getProperty(key));
        else
            return Configuration.CONF_DEFAULT_INTEGER;
    }

    public String getString(String key)
    {
        if (prop.containsKey(key))
            return getProperty(key);
        else
            return Configuration.CONF_DEFAULT_STRING;
    }
}
