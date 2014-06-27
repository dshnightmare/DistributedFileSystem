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
        File f;

        assertFalse(d.contains("f"));
        f = d.getFile("f");
        assertNull(f);

        d.addFile(new File("f", 1));

        f = d.getFile("f");
        assertTrue(d.contains("f"));
        assertNotNull(f);
        
        d.removeFile(f.getName());
        assertFalse(d.contains("f"));
        f = d.getFile("f");
        assertNull(f);
    }
}
