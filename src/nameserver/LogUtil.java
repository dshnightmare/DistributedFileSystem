package nameserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import nameserver.meta.Meta;
import common.observe.call.Call;
import common.util.Configuration;

public class LogUtil
{
    private static LogUtil instance;

    private final static String SEPERATOR = " ";

    private static String logFileName;

    private LogUtil()
    {
        logFileName =
            Configuration.getInstance().getString(
                Configuration.META_LOG_DIR_KEY)
                + "history";
    }

    public synchronized static LogUtil getInstance()
    {
        if (null == instance)
            instance = new LogUtil();
        return instance;
    }

    public synchronized void writeIssue(long tid, Call.Type type,
        String description)
    {
        Writer writer = null;
        try
        {
            writer = new FileWriter(new File(logFileName), true);
            writer.write("<issue>" + SEPERATOR + tid + SEPERATOR + type
                + SEPERATOR + description + SEPERATOR + "<issue>\n");
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

    public synchronized void writeCommit(long tid)
    {
        Writer writer = null;
        try
        {
            writer = new FileWriter(new File(logFileName), true);
            writer.write("<commit>" + SEPERATOR + tid + SEPERATOR
                + "<commit>\n");
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

    public synchronized void checkpoint()
    {
        File file = new File(logFileName);
        file.delete();
    }

    public synchronized void recover()
    {
        BufferedReader reader = null;
        Queue<String[]> committedTasks = new LinkedList<String[]>();
        Map<String, String[]> uncommittedTasks =
            new HashMap<String, String[]>();

        try
        {
            reader = new BufferedReader(new FileReader(logFileName));
            String line = null;
            String[] tokens;

            while (true)
            {
                line = reader.readLine();
                if (null == line)
                    break;
                tokens = line.split(SEPERATOR);
                if (0 == tokens[0].compareTo("<issue>")
                    && 0 == tokens[tokens.length - 1].compareTo("<issue>"))
                {
                    uncommittedTasks.put(tokens[1], tokens);
                }
                else if (0 == tokens[0].compareTo("<commit>")
                    && 0 == tokens[tokens.length - 1].compareTo("<commit>"))
                {
                    String[] s = uncommittedTasks.remove(tokens[1]);
                    if (null != s)
                        committedTasks.add(s);
                }
            }

            Meta meta = Meta.getInstance();
            synchronized (meta)
            {
                while (!committedTasks.isEmpty())
                {
                    tokens = committedTasks.poll();
                    if (0 == tokens[2].compareTo(Call.Type.ADD_FILE_C2N
                        .toString()))
                    {
                        meta.addFile(tokens[3], new nameserver.meta.File(
                            tokens[4], Long.valueOf(tokens[5])));
                    }
                    else if (0 == tokens[2].compareTo(Call.Type.MOVE_FILE_C2N
                        .toString()))
                    {
                        meta.renameFile(tokens[3], tokens[4], tokens[5],
                            tokens[6]);
                    }
                    else if (0 == tokens[2].compareTo(Call.Type.REMOVE_FILE_C2N
                        .toString()))
                    {
                        meta.removeFile(tokens[3], tokens[4]);
                    }
                    else if (0 == tokens[2].compareTo(Call.Type.APPEND_FILE_C2N
                        .toString()))
                    {
                        // Update file version.
                    }
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
