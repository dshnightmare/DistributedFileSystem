package nameserver.task;

import java.util.ArrayList;
import java.util.List;

import nameserver.meta.Directory;
import nameserver.meta.Meta;
import common.call.Call;
import common.call.c2n.GetDirectoryCallC2N;
import common.call.n2c.GetDirectoryCallN2C;
import common.network.Connector;
import common.util.Logger;

/**
 * Task of getting directory.
 * <p>
 * Return all files or directories among the specified directory.
 * 
 * @author lishunyang
 * 
 */
public class GetDirectoryTask
    extends NameServerTask
{
    /**
     * Logger.
     */
    private final static Logger logger = Logger
        .getLogger(GetDirectoryTask.class);

    /**
     * Directory name.
     */
    private String dirName;

    /**
     * List of files that among this directory.
     */
    private List<String> fileList;

    /**
     * List of direcoties that among this directory.
     */
    private List<String> dirList;

    /**
     * Construction method.
     * 
     * @param tid
     * @param call
     * @param connector
     */
    public GetDirectoryTask(long tid, Call call, Connector connector)
    {
        super(tid, call, connector);
        GetDirectoryCallC2N c = (GetDirectoryCallC2N) call;
        this.dirName = c.getDirName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCall(Call call)
    {
        if (call.getToTaskId() != getTaskId())
            return;

        if (call.getType() == Call.Type.LEASE_C2N)
        {
            renewLease();
            return;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        final Meta meta = Meta.getInstance();

        synchronized (meta)
        {

            if (!directoryExists())
            {
                sendAbortCall("Task aborted, directory does not exist.");
                setFinish();
                return;
            }
            else
            {
                logger.info("GetDirectoryTask " + getTaskId() + " started.");

                Directory dir = meta.getDirectory(dirName);

                fileList = new ArrayList<String>();
                dirList = new ArrayList<String>();

                for (String fname : dir.getValidFileNameList().keySet())
                    fileList.add(fname);

                for (String dname : meta.getSubDirectoryName(dirName))
                    dirList.add(dname);

                logger.info("GetDirectoryTask " + getTaskId() + " commit.");

                sendResponseCall();
                setFinish();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        setDead();
    }

    /**
     * Test whether the directory that client wants to get exists.
     * 
     * @return
     */
    private boolean directoryExists()
    {
        if (Meta.getInstance().containDirectory(dirName))
            return true;
        else
            return false;
    }

    /**
     * Send response call back to client.
     */
    private void sendResponseCall()
    {
        Call back = new GetDirectoryCallN2C(fileList, dirList);
        sendCall(back);
    }
}
