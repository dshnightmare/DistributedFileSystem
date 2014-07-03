package nameserver.meta;

import java.util.HashMap;
import java.util.Map;

/**
 * Directory structure, keep directory meta information such as name, files.
 * <p>
 * <strong>Warning:</strong> This structure is thread-unsafe.
 * 
 * @author lishunyang
 * @see File
 * 
 */
public class Directory
{
    private String name;

    /**
     * Files belong to this directory. {file name, file}
     */
    private Map<String, File> files = new HashMap<String, File>();

    /**
     * Indicate whether this directory is valid. After the directory has been
     * successfully created, this should be true.
     */
    private boolean valid = false;

    /**
     * Construction method.
     * 
     * @param name
     */
    public Directory(String name)
    {
        this.name = name;
    }

    /**
     * Get directory name.
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get specified file that belongs to this directory.
     * 
     * @param fileName
     * @return
     */
    public synchronized File getFile(String fileName)
    {
        return files.get(fileName);
    }

    /**
     * Add new file to this directory.
     * <p>
     * <strong>Warning:</strong> If there is already a same file, it will be
     * replaced.
     * 
     * @param file
     */
    public synchronized void addFile(File file)
    {
        files.put(file.getName(), file);
    }

    /**
     * Remove a specified file from this directory.
     * 
     * @param fileName
     * @return The removed file.
     */
    public synchronized File removeFile(String fileName)
    {
        return files.remove(fileName);
    }

    /**
     * Test whether a specified file belongs to this directory.
     * 
     * @param fileName
     * @return
     */
    public synchronized boolean contains(String fileName)
    {
        return files.containsKey(fileName);
    }

    /**
     * Set valid bit of this directory.
     * 
     * @param valid
     */
    public synchronized void setValid(boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Test whether the directory is valid.
     * 
     * @return
     */
    public synchronized boolean isValid()
    {
        return valid;
    }

    /**
     * Set the directory name.
     * 
     * @param name
     */
    public synchronized void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get all valid files of this directory.
     * 
     * @return
     */
    public synchronized Map<String, String> getValidFileNameList()
    {
        Map<String, String> fileList = new HashMap<String, String>();

        for (File f : files.values())
        {
            if (f.isValid())
                fileList.put(f.getName(), f.getId());
        }

        return fileList;
    }
}
