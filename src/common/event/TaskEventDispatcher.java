package common.event;

/**
 * The <tt>TaskEvent</tt> dispatcher.
 * 
 * @author lishunyang
 * @see TaskEvent
 * @see TaskEventListener
 */
public interface TaskEventDispatcher
{
    /**
     * 
     * 
     * @param listener
     */
    public void addListener(TaskEventListener listener);

    /**
     * Remove <tt>TaskEventListener</tt>.
     * 
     * @param listener
     */
    public void removeListener(TaskEventListener listener);

    /**
     * Fire <tt>TaskEvent</tt>.
     * 
     * @param event
     */
    public void fireEvent(TaskEvent event);
}
