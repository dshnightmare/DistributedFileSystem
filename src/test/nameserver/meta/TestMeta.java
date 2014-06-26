package test.nameserver.meta;

import nameserver.meta.Directory;
import nameserver.meta.Meta;
import junit.framework.TestCase;

public class TestMeta
    extends TestCase
{
    public void testMeta()
    {
        Meta m = new Meta();

        assertNull(m.getDirectory("/"));
        assertFalse(m.contains("/"));

        m.addDirectory(new Directory("/"));

        assertNotNull(m.getDirectory("/"));
        assertTrue(m.contains("/"));
    }
}
