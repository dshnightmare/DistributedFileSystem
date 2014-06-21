package test;

import common.util.Logger;

import junit.framework.TestCase;

public class TestLogger extends TestCase
{
    @Override
    protected void setUp()
    {
    }

    public void testLogger()
    {
        Logger logger = Logger.getLogger(Logger.class);
        logger.debug("debug");
        logger.error("error");
        logger.fatal("fatal");
        logger.info("info");
        logger.trace("trace");
        logger.warn("warn");
    }

    @Override
    protected void tearDown()
    {
    }
}
