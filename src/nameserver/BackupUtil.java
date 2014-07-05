package nameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import common.call.Call;
import common.util.Configuration;
import common.util.Logger;
import nameserver.meta.Meta;

public class BackupUtil
{
    private final static Logger logger = Logger.getLogger(BackupUtil.class);

    private final static String logFileName = "LOG";

    private final static String imageFileName = "IMAGE";

    private final static DateFormat timeFormat = new SimpleDateFormat(
        "yyyyMMddHHmmss");

    private final static String SEPERATOR = " ";

    private final static String ISSUE = "<ISSUE>";

    private final static String COMMIT = "<COMMIT>";

    private static BackupUtil instance = null;

    private static String logDirName;

    private static String imageDirName;

    private BackupUtil()
        throws Exception
    {
        imageDirName =
            Configuration.getInstance().getString(
                Configuration.META_BACKUP_DIR_KEY);
        logDirName =
            Configuration.getInstance().getString(
                Configuration.META_LOG_DIR_KEY);

        if (imageDirName.isEmpty() || logDirName.isEmpty())
        {
            throw new Exception(
                "Failed to initialize BackupUtil, image saving directory or log saving directory not found.");
        }

        File dir = null;
        dir = new File(imageDirName);
        if (!dir.exists())
        {
            logger.info("Create image saving directory " + imageDirName);
            dir.mkdirs();
        }

        dir = new File(logDirName);
        if (!dir.exists())
        {
            logger.info("Create log saving directory " + logDirName);
            dir.mkdirs();
        }
    }

    public synchronized static BackupUtil getInstance()
    {
        try
        {
            if (null == instance)
                instance = new BackupUtil();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return instance;
    }

    public synchronized void writeBackupImage()
    {
        final Meta meta = Meta.getInstance();

        synchronized (meta)
        {
            logger.info("Write backup image file.");
            final String imageFilePath =
                imageDirName + imageFileName + "_"
                    + timeFormat.format(new Date());
            BufferedWriter writer = null;

            try
            {
                writer = new BufferedWriter(new FileWriter(imageFilePath));

                for (Entry<String, Map<String, String>> e : meta
                    .getValidDirectoryList().entrySet())
                {
                    writer.write(e.getKey() + SEPERATOR + e.getValue().size());
                    writer.newLine();
                    for (Entry<String, String> en : e.getValue().entrySet())
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized void readBackupImage()
    {
        final Meta meta = Meta.getInstance();

        synchronized (meta)
        {
            logger.info("Read backup image file.");
            final File dir = new File(imageDirName);

            File[] backups = dir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.startsWith(imageFileName);
                }
            });

            if (backups.length <= 0)
            {
                logger
                    .warn("Failed to restore meta, no image files were found.");
                return;
            }

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
                    new BufferedReader(new FileReader(imageDirName
                        + backups[0].getName()));
                String line = null;

                while (true)
                {
                    line = reader.readLine();
                    if (null == line)
                        break;

                    String[] dirInfo = line.split(SEPERATOR);

                    // +---------------+------------+
                    // |0              |1           |
                    // +---------------+------------+
                    // |directory name |file number |
                    // +---------------+------------+
                    if (dirInfo.length != 2)
                        break;

                    final String dirName = dirInfo[0];
                    final int numFiles = Integer.valueOf(dirInfo[1]);
                    for (int i = 0; i < numFiles; i++)
                    {
                        String fileLine = reader.readLine();
                        String[] fileInfo = fileLine.split(SEPERATOR);

                        // +--+----------+--------+
                        // |0 |1         |2       |
                        // +--+----------+--------+
                        // |  |file name |file id |
                        // +--+----------+--------+
                        if (fileInfo.length != 3)
                        {
                            logger
                                .warn("Some files couldn't be restored, information was lost.");
                            continue;
                        }

                        final String fileName = fileInfo[1];
                        final String fileId = fileInfo[2];
                        final long bareFileId =
                            nameserver.meta.File.getBareIdFromFileId(fileId);
                        final long fileVersion =
                            nameserver.meta.File.getVersionFromFileId(fileId);
                        final nameserver.meta.File file =
                            new nameserver.meta.File(fileName, bareFileId);
                        file.setVersion(fileVersion);

                        meta.addFile(dirName, file);
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
        }

        // for (File f : backups)
        // {
        // System.out.println(f.getName());
        // }
    }

    public synchronized void writeLogIssue(long tid, Call.Type type,
        String description)
    {
        final String logFilePath = logDirName + logFileName;
        Writer writer = null;

        try
        {
            writer = new FileWriter(new File(logFilePath), true);

            writer.write(ISSUE + SEPERATOR + tid + SEPERATOR + type + SEPERATOR
                + description + SEPERATOR + ISSUE + "\n");
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
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void writeLogCommit(long tid)
    {
        final String logFilePath = logDirName + logFileName;
        Writer writer = null;

        try
        {
            writer = new FileWriter(new File(logFilePath), true);

            writer.write(COMMIT + SEPERATOR + tid + SEPERATOR + COMMIT + "\n");
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
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void deleteBackLog()
    {
        final String logFilePath = logDirName + logFileName;

        File file = new File(logFilePath);
        file.delete();
    }

    public synchronized void readBackupLog()
    {
        final Meta meta = Meta.getInstance();
        final Queue<String[]> committedTasks = new LinkedList<String[]>();
        final Map<String, String[]> suspendedTasks =
            new HashMap<String, String[]>();
        final String logFilePath = logDirName + logFileName;
        BufferedReader reader = null;

        synchronized (meta)
        {
            try
            {
                reader = new BufferedReader(new FileReader(logFilePath));
                String line = null;
                String[] tokens;

                while (true)
                {
                    line = reader.readLine();

                    // EOF
                    if (null == line)
                        break;

                    tokens = line.split(SEPERATOR);

                    // Issue
                    if (0 == tokens[0].compareTo(ISSUE)
                        && 0 == tokens[tokens.length - 1].compareTo(ISSUE))
                    {
                        suspendedTasks.put(tokens[1], tokens);
                    }
                    // Commit
                    else if (0 == tokens[0].compareTo(COMMIT)
                        && 0 == tokens[tokens.length - 1].compareTo(COMMIT))
                    {
                        String[] s = suspendedTasks.remove(tokens[1]);

                        if (null != s)
                            committedTasks.add(s);
                    }
                }

                while (!committedTasks.isEmpty())
                {
                    tokens = committedTasks.poll();
                    String logCall = tokens[2];

                    if (callEqual(logCall, Call.Type.ADD_FILE_C2N))
                    {
                        final String dirName = tokens[3];
                        final String fileName = tokens[4];
                        final String fileId = tokens[5];
                        final long bareFileId =
                            nameserver.meta.File.getBareIdFromFileId(fileId);

                        meta.addFile(dirName, new nameserver.meta.File(
                            fileName, bareFileId));
                    }
                    else if (callEqual(logCall, Call.Type.ADD_DIRECTORY_C2N))
                    {
                        final String dirName = tokens[3];

                        meta.addDirectory(new nameserver.meta.Directory(dirName));
                    }
                    else if (callEqual(logCall, Call.Type.MOVE_FILE_C2N))
                    {
                        final String oldDirName = tokens[3];
                        final String oldfileName = tokens[4];
                        final String newDirName = tokens[5];
                        final String newfileName = tokens[6];

                        meta.renameFile(oldDirName, oldfileName, newDirName,
                            newfileName);
                    }
                    else if (callEqual(logCall, Call.Type.MOVE_DIRECTORY_C2N))
                    {
                        final String oldDirName = tokens[3];
                        final String newDirName = tokens[4];

                        meta.renameDirectory(oldDirName, newDirName);
                    }
                    else if (callEqual(logCall, Call.Type.REMOVE_FILE_C2N))
                    {
                        final String dirName = tokens[3];
                        final String fileName = tokens[4];

                        meta.removeFile(dirName, fileName);
                    }
                    else if (callEqual(logCall, Call.Type.REMOVE_DIRECTORY_C2N))
                    {
                        final String dirName = tokens[3];

                        meta.removeDirectory(dirName);
                    }
                    else if (callEqual(logCall, Call.Type.APPEND_FILE_C2N))
                    {
                        final String dirName = tokens[3];
                        final String fileName = tokens[4];

                        meta.getFile(dirName, fileName).updateVersion();
                    }
                    else
                    {
                        logger
                            .info("BackupUtil, unknown operation: " + logCall);
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
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean callEqual(String logCall, Call.Type call)
    {
        return logCall.compareTo(call.toString()) == 0;
    }
}
