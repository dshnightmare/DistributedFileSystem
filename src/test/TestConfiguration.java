package test;

import common.util.Configuration;
import common.util.Constant;
import junit.framework.TestCase;

public class TestConfiguration
    extends TestCase
{
    private static Configuration conf;

    @Override
    protected void setUp()
    {

    }

    public void testConfiguration()
    {
        conf = Configuration.getInstance();
        assertNotNull(conf);

        long heartbeatTime = -1;
        heartbeatTime = conf.getLong(Constant.HEARTBEAT_INTERVAL_KEY);
        System.out.println(heartbeatTime);
        assertFalse(heartbeatTime < 0);
        
        int size = -1;
        size = conf.getInteger("ByteBuffer_size");
        System.out.println(size);
        assertFalse(size < 0);
    }

    @Override
    protected void tearDown()
    {
    }
}
