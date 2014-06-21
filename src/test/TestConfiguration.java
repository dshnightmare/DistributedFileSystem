package test;

import java.io.IOException;

import common.util.Configuration;
import junit.framework.TestCase;

public class TestConfiguration extends TestCase
{
    private static Configuration conf;
    private static String fileName = "conf.properties";
    
    @Override
    protected void setUp()
    {
        
    }

    public void testConfiguration()
    {
        try
        {
            conf = Configuration.getInstance(fileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        assertNotNull(conf);
        
        String heartbeatTime = conf.getProperty("heartbeat_interval");
        System.out.println(heartbeatTime);
        assertFalse(heartbeatTime.isEmpty());
    }

    @Override
    protected void tearDown()
    {
    }
}
