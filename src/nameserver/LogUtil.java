package nameserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import common.observe.call.Call;
import common.util.Configuration;

public class LogUtil
{
    private static LogUtil instance;

    private final static String SEPERATOR = " ";

    private LogUtil()
    {
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
            writer =
                new FileWriter(new File(Configuration.getInstance().getString(
                    Configuration.META_LOG_DIR_KEY)), true);
            writer.write("<issue>" + SEPERATOR + tid + SEPERATOR + type + SEPERATOR
                + description + SEPERATOR + "<issue>\n");
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
            writer =
                new FileWriter(new File(Configuration.getInstance().getString(
                    Configuration.META_LOG_DIR_KEY)), true);
            writer.write("<commit>" + SEPERATOR + tid + SEPERATOR + "<commit>\n");
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
