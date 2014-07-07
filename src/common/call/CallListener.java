package common.call;

/**
 * register on CallDispather, when a call is fired, the listener will receive it
 * so to handle it.
 * 
 * @author gengyufeng
 * 
 */
public interface CallListener
{
    public void handleCall(Call call);
}
