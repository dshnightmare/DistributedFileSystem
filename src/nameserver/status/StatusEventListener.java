package nameserver.status;

/**
 * <tt>StatusEvent</tt> listener. It will receive <tt>StatusEvent</tt> and
 * handle them.
 * 
 * @author lishunyang
 * 
 */
public interface StatusEventListener
{
    /**
     * Handle <tt>StatusEvent</tt>.
     * 
     * @param event
     */
    public void handle(StatusEvent event);
}
