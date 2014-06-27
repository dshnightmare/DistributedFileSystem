package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

public class Meta
{
    private Map<String, Directory> directories =
        new HashMap<String, Directory>();

    public static final String SEPERATOR = "/";

    public synchronized Directory getDirectory(String dirName)
    {
        return directories.get(dirName);
    }

    public synchronized void addDirectory(Directory directory)
    {
        directories.put(directory.getName(), directory);
    }

    public synchronized boolean contains(String dirName)
    {
        return directories.containsKey(dirName);
    }
}
