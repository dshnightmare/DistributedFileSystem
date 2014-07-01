package nameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import common.util.Configuration;

import nameserver.meta.Meta;

public class BackupUtil
{
    private static BackupUtil instance = null;

    private String outputDirName;

    private final String fileNamePrefix = "backup";

    private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String SEPERATOR = "\t";

    private BackupUtil()
    {
        outputDirName = Configuration.getInstance()
            .getString(Configuration.META_BACKUP_DIR_KEY);
    }

    public static BackupUtil getInstance()
    {
        synchronized (BackupUtil.class)
        {
            if (null == instance)
                instance = new BackupUtil();
        }

        return instance;
    }

    public void backup()
    {
        File dir = new File(outputDirName);
        if (!dir.exists())
            dir.mkdirs();

        synchronized (Meta.getInstance())
        {
            String finalName =
                outputDirName + fileNamePrefix + "-"
                    + timeFormat.format(new Date());
            BufferedWriter writer = null;
            try
            {
                writer = new BufferedWriter(new FileWriter(finalName));

                for (Entry<String, Map<String, Long>> e : Meta.getInstance()
                    .getDirectoryList().entrySet())
                {
                    writer.write(e.getKey() + SEPERATOR + e.getValue().size());
                    writer.newLine();
                    for (Entry<String, Long> en : e.getValue().entrySet())
                    {
                        writer.write(SEPERATOR + en.getKey() + SEPERATOR
                            + en.getValue());
                        writer.newLine();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (null != writer)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void restore()
    {
        File dir = new File(outputDirName);
        if (!dir.exists())
            return;

        File[] backups = dir.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.startsWith(fileNamePrefix);
            }

        });

        if (backups.length <= 0)
            return;

        Arrays.sort(backups, new Comparator<File>()
        {
            @Override
            public int compare(File f1, File f2)
            {
                return f2.getName().compareTo(f1.getName());
            }
        });

        BufferedReader reader = null;
        try
        {
            reader =
                new BufferedReader(new FileReader(outputDirName
                    + backups[0].getName()));
            String line = null;
            while (true)
            {
                line = reader.readLine();
                if (null == line)
                    break;
                String[] strs = line.split(SEPERATOR);
                // [directory name, file count]
                if (strs.length != 2)
                    break;
                String dirName = strs[0];
                final int numFile = Integer.valueOf(strs[1]);
                for (int i = 0; i < numFile; i++)
                {
                    String fileLine = reader.readLine();
                    String[] fileInfo = fileLine.split(SEPERATOR);
                    // [, file name, file id]
                    if (fileInfo.length != 3)
                    {
                        System.out
                            .println("Some file can not be restored, information missing.");
                        continue;
                    }
                    nameserver.meta.File file =
                        new nameserver.meta.File(fileInfo[1],
                            Long.valueOf(fileInfo[2]));
                    Meta.getInstance().addFile(dirName, file);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        for (File f : backups)
        {
            System.out.println(f.getName());
        }
    }
}
