package nameserver.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Meta
{
    private static Meta instance = null;

    private Map<String, Directory> directories =
        new HashMap<String, Directory>();

    private Map<Long, File> files = new HashMap<Long, File>();

    public static final String SEPERATOR = "/";

    private Lock lock = new ReentrantLock();

    private Meta()
    {
    }

    public static Meta getInstance()
    {
        if (null == instance)
        {
            synchronized (Meta.class)
            {
                if (null == instance)
                {
                    instance = new Meta();
                }
            }
        }

        return instance;
    }

    public Directory getDirectory(String dirName)
    {
        return directories.get(dirName);
    }

    public void addDirectory(Directory directory)
    {
        directories.put(directory.getName(), directory);
    }

    public Directory removeDirectory(String dirName)
    {
        return directories.remove(dirName);
    }

    public boolean containDirectory(String dirName)
    {
        return directories.containsKey(dirName);
    }

    public boolean isDirectoryValid(String dirName)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
            return false;
        return dir.isValid();
    }

    public void setDirectoryValid(String dirName, boolean valid)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
            return;
        dir.setValid(valid);
    }
    
    public void renameDirectory(String oldDirName, String newDirName)
    {
        Directory dir = removeDirectory(oldDirName);
        if (null == dir)
            return;
        dir.setName(newDirName);
        addDirectory(dir);
    }

    public File getFile(String dirName, String fileName)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
            return null;
        return dir.getFile(fileName);
    }
    
    public File getFile(Long fid)
    {
        return files.get(fid);
    }

    /**
     * Add file to meta structure.
     * <p>
     * <strong>Warning:</strong> This method would create new directory if it's
     * not existed. Also, this method would replaced the file if there already
     * have one, So you must insure that.
     * 
     * @param dirName
     * @param file
     */
    public void addFile(String dirName, File file)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
        {
            dir = new Directory(dirName);
            addDirectory(dir);
        }
        dir.addFile(file);
        files.put(file.getId(), file);
    }

    public void removeFile(String dirName, String fileName)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
            return;
        File file = dir.removeFile(fileName);
        if (null != file)
            files.remove(file.getId());
    }

    public boolean containFile(String dirName, String fileName)
    {
        Directory dir = getDirectory(dirName);
        if (null == dir)
            return false;
        return dir.contains(fileName);
    }

    public boolean containFile(Long fid)
    {
        return files.containsKey(fid);
    }

    public boolean isFileValid(String dirName, String fileName)
    {
        File file = getFile(dirName, fileName);
        if (null == file)
            return false;
        return file.isValid();
    }

    public void setFileValid(String dirName, String fileName, boolean valid)
    {
        File file = getFile(dirName, fileName);
        if (null == file)
            return;
        file.setValid(valid);
    }
    
    public void renameFile(String oldDirName, String oldFileName, String newDirName, String newFileName)
    {
        Directory oldDir = getDirectory(oldDirName);
        if (null == oldDir)
            return;
        File file = oldDir.removeFile(oldFileName);
        if (null == file)
            return;
        file.setName(newFileName);
        addFile(newDirName, file);
    }

    public void lock(String dirName)
    {
        lock.lock();
    }

    public void unlock()
    {
        lock.unlock();
    }

    public boolean tryLock()
    {
        return lock.tryLock();
    }
}
