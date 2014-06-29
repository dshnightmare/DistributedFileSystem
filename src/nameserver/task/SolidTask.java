package nameserver.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import nameserver.meta.Meta;
import common.observe.call.Call;
import common.thread.TaskThread;
import common.util.Timestamp;

public class SolidTask
    extends TaskThread
{
    private String outputDirName;

    private String fileName;

    private long period;

    public SolidTask(long tid, String outputDirName, String fileName,
        long period)
    {
        super(tid);
        this.outputDirName = outputDirName;
        this.fileName = fileName;
        this.period = period;
    }

    @Override
    public void handleCall(Call call)
    {
        File dir = new File(outputDirName);
        if (!dir.exists())
            dir.mkdirs();

        while (true)
        {
            synchronized (Meta.getInstance())
            {
                String finalName =
                    fileName + "-" + Timestamp.getInstance().getTimestamp();
                Writer writer = null;
                try
                {
                    writer = new FileWriter(finalName);
                    JSONArray json = Meta.getInstance().getJsonData();
                    writer.write(json.toString());
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

            try
            {
                TimeUnit.SECONDS.sleep(period);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run()
    {
        synchronized (Meta.getInstance())
        {

        }
    }

    @Override
    public void release()
    {
    }

}
