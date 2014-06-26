package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

public class Directory
{
    private String name;

    private Map<String, File> files = new HashMap<String, File>();

    public Directory(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public synchronized File getFile(String fileName)
    {
        return files.get(fileName);
    }

    public synchronized void addFile(File file)
    {
        files.put(file.getName(), file);
    }

    public synchronized void removeFile(File file)
    {
        files.remove(file);
    }

    public synchronized boolean contains(String fileName)
    {
        return files.containsKey(fileName);
    }
}