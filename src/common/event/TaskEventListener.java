package common.event;

/**
 * Listener of <tt>TaskEvent</tt>.
 * 
 * @author lishunyang
 * @see TaskEvent
 */
public interface TaskEventListener
{
    /**
     * Handle <tt>TaskEvent</tt>.
     * 
     * @param event
     */
    public void handle(TaskEvent event);
}
