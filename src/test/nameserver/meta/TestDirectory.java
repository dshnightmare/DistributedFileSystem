package test.nameserver.meta;

import nameserver.meta.Directory;
import nameserver.meta.File;
import junit.framework.TestCase;

public class TestDirectory
    extends TestCase
{
    public void testDirectory()
    {
        Directory d = new Directory("/");

        assertFalse(d.contains("f"));
        assertNull(d.getFile("f"));

        d.addFile(new File("f", 1));

        assertTrue(d.contains("f"));
        assertNotNull(d.getFile("f"));
    }
}
