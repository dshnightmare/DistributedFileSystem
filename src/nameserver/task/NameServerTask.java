package nameserver.task;

import common.call.Call;
import common.call.n2c.AbortCallN2C;
import common.network.Connector;
import common.task.Task;

/**
 * Abstract name server task.
 * 
 * @author lishunyang
 * @see Task
 */
public abstract class NameServerTask
    extends Task
{
    /**
     * Instance of <tt>ServerConnector</tt>.
     */
    private Connector connector;

    /**
     * Indicate who initiated this task.
     */
    private String initiator;

    /**
     * Indicate remote peer's task id.
     */
    private long remoteTaskId;

    /**
     * Indicate whether this task is dead.
     */
    private boolean dead = false;

    /**
     * Construction method.
     * 
     * @param tid
     * @param call
     * @param connector
     */
    public NameServerTask(long tid, Call call, Connector connector)
    {
        super(tid);
        this.connector = connector;
        this.initiator = call.getInitiator();
        this.remoteTaskId = call.getFromTaskId();
    }

    /**
     * Get initiator.
     * 
     * @return
     */
    protected String getInitiator()
    {
        return initiator;
    }

    /**
     * Send abort call back to remote peer.
     * 
     * @param reason
     */
    protected void sendAbortCall(String reason)
    {
        sendCall(new AbortCallN2C(reason));
    }

    /**
     * Send call back to remote peer.
     * 
     * @param call
     */
    protected void sendCall(Call call)
    {
        call.setFromTaskId(getTaskId());
        call.setToTaskId(remoteTaskId);
        call.setInitiator(initiator);

        connector.sendCall(call);
    }

    /**
     * Set task status to dead.
     */
    protected void setDead()
    {
        this.dead = true;
    }

    /**
     * Test whether the task is dead.
     * 
     * @return
     */
    protected boolean isDead()
    {
        return dead;
    }
}
