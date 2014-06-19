package common.network;

import common.observe.call.Call;

/**
 * implemented by: ClientConnector
 * @author gengyufeng
 *
 */
public interface IF_Connector
{
    public void sendCommand(Call command);
}
