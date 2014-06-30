package nameserver.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SolidTask(long tid, String outputDirName, String fileName,
        long period)
    {
        super(tid);
        this.outputDirName = outputDirName;
        this.fileName = fileName;
        this.period = period;
    }

    @Override
    public void run()
    {
        File dir = new File(outputDirName);
        if (!dir.exists())
            dir.mkdirs();

        while (true)
        {
            synchronized (Meta.getInstance())
            {
                String finalName =
                    fileName + "-" + timeFormat.format(new Date());
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleCall(Call call)
    {
    }

    @Override
    public void release()
    {
    }

}
