package nameserver.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage
{
    private final long id;

    private final String address;

    private long heartbeatTime;

    private int load;

    private List<File> files = new ArrayList<File>();

    private Map<Storage, List<File>> migrateFiles =
        new HashMap<Storage, List<File>>();

    public Storage(long id, String address)
    {
        this.id = id;
        this.address = address;
    }

    public synchronized void setHeartbeatTime(long time)
    {
        this.heartbeatTime = time;
    }

    public synchronized long getHearbeatTime()
    {
        return heartbeatTime;
    }

    public void setLoad(int load)
    {
        this.load = load;
    }

    public int getLoad()
    {
        return load;
    }

    public long getId()
    {
        return id;
    }

    public String getAddress()
    {
        return address;
    }

    public synchronized List<File> getFiles()
    {
        return files;
    }

    public synchronized void addFile(File file)
    {
        files.add(file);
    }

    public synchronized void removeFile(File file)
    {
        files.remove(file);
    }

    public synchronized Map<Storage, List<File>> cleanMigrateFiles()
    {
        Map<Storage, List<File>> result = migrateFiles;
        migrateFiles = new HashMap<Storage, List<File>>();

        return result;
    }

    public synchronized void addMigrateFile(Storage storage, File file)
    {
        List<File> list = migrateFiles.get(storage);
        if (null == list)
        {
            list = new ArrayList<File>();
            migrateFiles.put(storage, list);
        }
        list.add(file);
    }
}
