package test.nameserver.meta;

import org.json.JSONArray;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import junit.framework.TestCase;

public class TestMeta
    extends TestCase
{
    public void testMeta()
    {
        Meta m = Meta.getInstance();

        assertNull(m.getDirectory("/"));
        assertFalse(m.containDirectory("/"));

        m.addDirectory(new Directory("/"));

        assertNotNull(m.getDirectory("/"));
        assertTrue(m.containDirectory("/"));
    }
    
    public void testJson()
    {
        Meta m = Meta.getInstance();
        
        m.addFile("/a/b/c/", new File("f1", 1));
        m.addFile("/a/b/c/", new File("f2", 2));
        m.addFile("/a/b/c/d/", new File("f3", 3));
        m.addFile("/e/g/", new File("f4", 4));
        
        JSONArray json = m.getJsonData();
        System.out.println(json.toString());
    }
}
