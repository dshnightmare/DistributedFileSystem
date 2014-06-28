package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

public class Directory
{
    private String name;

    private Map<String, File> files = new HashMap<String, File>();

    /**
     * Indicate whether this directory has committed. If it's false, someone
     * could be using the directory now.
     */
    private boolean valid = false;

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

    public synchronized void removeFile(String fileName)
    {
        files.remove(fileName);
    }

    public synchronized boolean contains(String fileName)
    {
        return files.containsKey(fileName);
    }

    public synchronized void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public synchronized boolean isValid()
    {
        return valid;
    }
}
