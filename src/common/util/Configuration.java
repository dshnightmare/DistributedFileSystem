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

    private Configuration(String fileName)
        throws IOException
    {
        InputStream in = new BufferedInputStream(new FileInputStream(fileName));
        prop.load(in);
        in.close();
    }

    public static Configuration getInstance(String fileName) throws IOException
    {
        if (null == instance)
        {
            synchronized (Configuration.class)
            {
                if (null == instance)
                    instance = new Configuration(fileName);
            }
        }

        return instance;
    }

    public String getProperty(String key)
    {
        return prop.getProperty(key);
    }
}
