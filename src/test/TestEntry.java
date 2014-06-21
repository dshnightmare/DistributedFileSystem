package test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestEntry
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("TestSuite");

        suite.addTestSuite(TestTaskMonitor.class);

        return suite;
    }
}
