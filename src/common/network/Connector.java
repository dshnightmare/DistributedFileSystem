package common.network;

import common.observe.request.Request;

public interface Connector
{
    public void sendRequest(Request request);
}
