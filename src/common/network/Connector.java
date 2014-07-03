package common.network;

import common.call.Call;
/**
 * implemented by: ClientConnector
 * @author gengyufeng
 *
 */
public interface Connector
{
    public void sendCall(Call command);
}
